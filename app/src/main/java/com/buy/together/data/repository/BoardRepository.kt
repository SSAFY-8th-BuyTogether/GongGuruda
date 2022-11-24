package com.buy.together.data.repository

import android.net.Uri
import android.util.Log
import com.buy.together.data.model.domain.BoardDto
import com.buy.together.data.model.domain.CommentDto
import com.buy.together.data.model.domain.usercollection.UserBoard
import com.buy.together.data.model.domain.usercollection.UserComment
import com.buy.together.data.model.network.Alarm
import com.buy.together.data.model.network.FireStoreMessage
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.data.model.network.firestore.observeCollection
import com.buy.together.network.service.FirebaseMessageService
import com.buy.together.util.GalleryUtils
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreBoardRepositor_싸피"
class BoardRepository {
    private val boardDB = FirebaseFirestore.getInstance().collection("Board")
    private val userDB = FirebaseFirestore.getInstance().collection("User")

    // 카테고리별 Board 가져오기
    fun getBoardList(category: String) : Flow<List<BoardDto?>?> {
        return observeCollection(
            boardDB.document(category).collection(category)
                .orderBy("deadLine", Query.Direction.ASCENDING)
        )
    }

    //모든 Board 가져오기
    fun getAllBoardList(categories: ArrayList<String>) = flow{
        emit(FireStoreResponse.Loading())
        val boardList = arrayListOf<DocumentSnapshot>()
        for(i in 1..categories.size-1){
            val boards = boardDB.document(categories[i]).collection(categories[i])
                .orderBy("deadLine",Query.Direction.ASCENDING).get().await().documents
            boardList.addAll(boards)
        }
        emit(FireStoreResponse.Success(boardList))
    }.catch {
        emit(FireStoreResponse.Failure("게시글을 받아오는데 실패했습니다."))
    }

    //각각의 Board 가져오기
    fun getEachBoard(category : String, id : String) = flow {
        emit(FireStoreResponse.Loading())
        val query = boardDB.document(category).collection(category).whereEqualTo("id", id)
        emit(FireStoreResponse.Success(query.get().await().documents[0]))
    }.catch {
        emit(FireStoreResponse.Failure("존재하지 않는 게시글입니다."))
    }

    //Board 저장하기
    fun saveBoard(boardDto : BoardDto, imgs : ArrayList<Uri>)= flow {
        emit(FireStoreResponse.Loading())
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val urlList : ArrayList<String> = imgs.mapIndexed{ i, it ->
                    "IMAGE_${boardDto.id}_${i}.png"
                } as ArrayList<String>
                boardDto.images = GalleryUtils.insertImage(urlList,imgs)
                Log.d("싸피", "이미지 저장 성공~~~~~~~~~~\n")
            }
        }.join()
        Log.d("싸피", "saveBoard: 이미지 완료")
        boardDB.document(boardDto.category) //board
            .collection(boardDto.category)
            .document(boardDto.id).set(boardDto).await()

        userDB.document(boardDto.writer) //participate
            .collection("Participate")
            .document(boardDto.id)

        val userBoard = UserBoard(boardDto) //User
        val query = userDB.document(boardDto.writer)
            .collection("Board")
            .document(boardDto.id)
        emit(FireStoreResponse.Success(query.set(userBoard).await()))
    }.catch {
        emit(FireStoreResponse.Failure("데이터를 저장하는데 실패했습니다."))
    }

    //Board 삭제하기( user 삭제 -> board 삭제 )
    fun deleteBoard(userId:String, boardDto : BoardDto)= flow {
        emit(FireStoreResponse.Loading())
        userDB.document(userId)
            .collection("Board")
            .document(boardDto.id).delete().await()
        val query = boardDB.document(boardDto.category)
            .collection(boardDto.category)
            .document(boardDto.id)
        emit(FireStoreResponse.Success(query.delete().await()))
    }.catch {
        emit(FireStoreResponse.Failure("데이터를 삭제하는데 실패했습니다."))
    }

    fun insertParticipator(userId: String, boardDto : BoardDto)= flow{
        emit(FireStoreResponse.Loading())
        boardDB.document(boardDto.category) //board
            .collection(boardDto.category)
            .document(boardDto.id).set(boardDto).await()
        val usrBoard = UserBoard(boardDto)

        val query = userDB.document(userId)// User Participate
            .collection("Participate")
            .document(usrBoard.id)

        //FCM
        val userTokenList = userDB.document(boardDto.writer).get().await()["devices"] as ArrayList<String>
        val fcm = FireStoreMessage(userTokenList,"참가자가 생겼어요!","작성하신 글 \"${boardDto.title}\"에 ${userId}님이 참여하였습니다!")
        CoroutineScope(Dispatchers.IO).launch{
            FirebaseMessageService().sendFcm(fcm){
                Log.d(TAG, "insertComment: nfc 보내기 $it")
            }
        }.join()
        emit(FireStoreResponse.Success(query.set(usrBoard).await()))
    }.catch {
        emit(FireStoreResponse.Failure("데이터를 저장하는데 실패했습니다."))
    }

    fun deleteParticipator(userId: String, boardDto : BoardDto)= flow{
        emit(FireStoreResponse.Loading())
        boardDB.document(boardDto.category) //board
            .collection(boardDto.category)
            .document(boardDto.id).set(boardDto).await()
        val usrBoard = UserBoard(boardDto)

        val query = userDB.document(userId)//User Participate
            .collection("Participate")
            .document(usrBoard.id)
        emit(FireStoreResponse.Success(query.delete().await()))
    }.catch {
        emit(FireStoreResponse.Failure("데이터를 삭제하는데 실패했습니다."))
    }

    fun getCommentList(category: String, boardId : String) : Flow<List<CommentDto?>?> {
        return observeCollection(
            boardDB.document(category).collection(category).document(boardId)
                .collection("Comment"))
    }
    
    fun insertComment(writer : String, mentionComent: String? , category : String ,comment: CommentDto) = flow {
        emit(FireStoreResponse.Loading())
        boardDB.document(category).collection(category).document(comment.boardId) //comment
            .collection("Comment").document(comment.id).set(comment).await()

        val dto = UserComment(category,comment) //user - 댓글 작성자
        val query = userDB.document(comment.writer)
            .collection("Comment")
            .document(comment.id)

        if(writer != comment.writer && comment.mention == null){ //본인 제외, mention 없을 때만
            val alarm = Alarm(category,comment) //Alarm - 글작성자에게
            userDB.document(writer)
                .collection("Alarm").document(alarm.id).set(alarm).await()
            //FCM
            val userTokenList = userDB.document(writer).get().await()["devices"] as ArrayList<String>
            val fcm = FireStoreMessage(userTokenList,"댓글이 달렸어요!","작성하신 글 \"${comment.boardTitle}\"에 댓글이 달렸어요!")
            withTimeoutOrNull(1000){
                FirebaseMessageService().sendFcm(fcm){
                    Log.d(TAG, "insertComment: nfc 보내기 $it")
                }
            }
        }

        if(comment.mention != null && mentionComent != null){ //Alarm - mention 작성자에게
            val alarmToMention = Alarm(category,mentionComent,comment) //Alarm - 글작성자에게
            userDB.document(comment.mention!!)
                .collection("Alarm").document(alarmToMention.id).set(alarmToMention).await()
            //FCM
            val userTokenListMention = userDB.document(comment.mention!!).get().await()["devices"] as ArrayList<String>
            val fcmMention = FireStoreMessage(userTokenListMention,"댓글이 달렸어요!","작성하신 댓글 \"${comment.boardTitle}\"에 댓글이 달렸어요!")
            CoroutineScope(Dispatchers.IO).launch{
                FirebaseMessageService().sendFcm(fcmMention){
                    Log.d(TAG, "insertComment: nfc 보내기 $it")
                }
            }.join()
        }
        emit(FireStoreResponse.Success(query.set(dto).await()))
    }.catch{
        emit(FireStoreResponse.Failure("데이터를 저장하는데 실패했습니다."))
    }

    fun deleteComment(category : String ,comment: CommentDto) = flow {
        emit(FireStoreResponse.Loading())
        userDB.document(comment.writer) //user 삭제
            .collection("Comment")
            .document(comment.id).delete().await()

        val query = boardDB.document(category) //comment 삭제
            .collection(category).document(comment.boardId)
            .collection("Comment").document(comment.id)
        emit(FireStoreResponse.Success(query.delete().await()))
    }.catch{
        emit(FireStoreResponse.Failure("데이터를 저장하는데 실패했습니다."))
    }
}