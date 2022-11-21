package com.buy.together.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.liveData
import com.buy.together.data.model.domain.BoardDto
import com.buy.together.data.model.domain.CommentDto
import com.buy.together.data.model.domain.usercollection.UserComment
import com.buy.together.data.repository.BoardRepository
import com.buy.together.ui.base.BaseViewModel
import com.buy.together.util.GalleryUtils.insertImage
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*

private const val TAG = "BoardViewModel_싸피"
class BoardViewModel : BaseViewModel() {
    private var repository = BoardRepository()
    val categoryListKr = arrayListOf("전체","식품","문구","생활용품","기타")

    var category : String = "" //main -> boardCategory
    var boardDto : BoardDto? = null //main, boardCategory -> board
    var selectedAddress : String = "" //선택된 주소

    // 카테고리별 데이터 가져오기
    fun getBoardList(category : String) = liveData(Dispatchers.IO){
        repository.getBoardList(category).collect { emit(it) }
    }

    fun getBoardListAll() = liveData(Dispatchers.IO) {
        repository.getAllBoardList(categoryListKr).collect{emit(it)}
    }

    //해당 id의 데이터 가져오기
    fun getEachBoard(category : String, id : String) = liveData(Dispatchers.IO){
        repository.getEachBoard(category,id).collect{emit(it)}
    }

    //게시글 저장
    fun saveBoard(boardDto : BoardDto, imgs : ArrayList<Uri>) = liveData(Dispatchers.IO){
        if(imgs.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                val list = arrayListOf<String>()
                for(i in 0..imgs.size-1){ //이미지 저장
                    insertImage("IMAGE_${boardDto.id}_${i}.png",imgs[i]){
                        list.add(it.toString())
                        Log.d(TAG, "이미지 저장 성공~~~~~~~~~~$i\n image : ${list}")
                        if (list.size == imgs.size) {
                            Log.d(TAG, "모든 이미지 완료 ${list}")
                            boardDto.images = list
                        }
                    }
                }
            }
        }
        Log.d(TAG, "saveBoard: 이미지 완료")
        repository.saveBoard(boardDto).collect{emit(it)}
    }

    fun removeBoard(userId: String, boardDto: BoardDto) = liveData(Dispatchers.IO) {
        repository.deleteBoard(userId, boardDto).collect{emit(it)}
    }

    //참여자 추가 true / 참여자 삭제 false
    fun insertParticipator(boardDto: BoardDto, userId : String, flag : Boolean) = liveData(Dispatchers.IO){
        val arrayList : ArrayList<String> = ArrayList(boardDto.participator)
        if(flag){
            arrayList.add(userId)
            boardDto.participator = arrayList
            repository.insertParticipator(userId,boardDto).collect{emit(it)}
        }else{
            arrayList.remove(userId)
            boardDto.participator = arrayList
            repository.deleteParticipator(userId,boardDto).collect{emit(it)}
        }
    }

    fun getComments(category: String, boardId : String) = liveData(Dispatchers.IO) {
        repository.getCommentList(category,boardId).collect{emit(it)}
    }

    fun insertComment(category: String, comment: CommentDto) = liveData(Dispatchers.IO) {
        repository.insertComment(category,comment).collect{emit(it)}
    }

    fun removeComment(category: String, comment: CommentDto) = liveData(Dispatchers.IO) {
        repository.deleteComment(category,comment).collect{emit(it)}
    }

    fun saveCommentToUser(userId: String, category: String, commentDto: CommentDto) = liveData(Dispatchers.IO) {
        val userComment = UserComment(
            id= commentDto.id,
            boardId = commentDto.boardId,
            boardTitle= commentDto.boardTitle,
            category= category,
            content= commentDto.content,
            time= Timestamp.now(),
        )
        repository.insertCommentToUser(userId, userComment).collect{emit(it)}
    }

    fun removeCommentFromUser(userId: String, category: String, commentDto: CommentDto) = liveData(Dispatchers.IO) {
        val userComment = UserComment(
            id= commentDto.id,
            boardId= commentDto.boardId,
            boardTitle= commentDto.boardTitle,
            category= category,
            content= commentDto.content,
            time= Timestamp.now(),

        )
        repository.deleteCommentFromUser(userId, userComment).collect{emit(it)}
    }

    fun makeBoard(document : DocumentSnapshot) : BoardDto { //TODO : Board내로 옮기기
        val dto = BoardDto(
            id= document.id,
            title=  document["title"] as String,
            category= document["category"] as String,
            deadLine= document["deadLine"] as Long,
            price= (document["price"] as Long).toInt(),
            content=  document["content"] as String,
            writeTime=  document["writeTime"] as Long,
            writer = document["writer"] as String,

            writerProfile = document["writerProfile"] as String?,
            participator = document["participator"] as List<String>,
            images= document["images"] as List<String>,
            maxPeople= (document["maxPeople"] as Long?)?.toInt(),
            meetPoint= document["meetPoint"] as String?,
            meetTime= document["meetTime"] as Long?,
            buyPoint= document["buyPoint"] as String?,
        )
        return dto
    }

}