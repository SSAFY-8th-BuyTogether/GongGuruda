package com.buy.together.data.repository

import android.util.Log
import com.buy.together.data.dto.BoardDto
import com.buy.together.data.dto.CommentDto
import com.buy.together.data.dto.usercollection.UserBoard
import com.buy.together.data.dto.usercollection.UserComment
import com.buy.together.data.dto.usercollection.UserParticipate
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreBoardRepositor_싸피"
class BoardRepository {
    private val boardDB = FirebaseFirestore.getInstance().collection("Board")
    private val userDB = FirebaseFirestore.getInstance().collection("User")

    fun getBoardList(category: String) = flow {
       Log.d(TAG, "getBoardList: 카테고리 : ${category}")
        val query = boardDB.document(category).collection(category)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.get().await().documents))
    }.catch {
        emit(FireStoreResponse.Failure("게시글을 받아오는데 실패했습니다."))
    }

    fun getEachBoard(category : String, id : String) = flow {
        val query = boardDB.document(category).collection(category).whereEqualTo("id", id)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.get().await().documents[0]))
    }.catch {
        emit(FireStoreResponse.Failure("존재하지 않는 게시글입니다."))
    }

    fun saveBoard(boardDto : BoardDto)= flow {
        val query = boardDB.document(boardDto.category)
            .collection(boardDto.category)
            .document(boardDto.id)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.set(boardDto).await()))
    }.catch {
        emit(FireStoreResponse.Failure("데이터를 저장하는데 실패했습니다."))
    }

    fun insertBoardToUser(userId:String, dto : UserBoard)= flow{
        val query = userDB.document(userId)
            .collection("Board")
            .document(dto.id)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.set(dto).await()))
    }.catch {
        emit(FireStoreResponse.Failure("데이터를 저장하는데 실패했습니다."))
    }

    fun insertParticipator(userId: String, participate: UserParticipate)= flow{
        val query = userDB.document(userId)
            .collection("Participate")
            .document(participate.id)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.set(participate).await()))
    }.catch {
        emit(FireStoreResponse.Failure("데이터를 저장하는데 실패했습니다."))
    }

    fun deleteParticipator(userId: String, participate: UserParticipate)= flow{
        val query = userDB.document(userId)
            .collection("Participate")
            .document(participate.id)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.delete().await()))
    }.catch {
        emit(FireStoreResponse.Failure("데이터를 삭제하는데 실패했습니다."))
    }
    
    fun getCommentList(category: String, boardId : String) = flow {
        val query = boardDB.document(category)
            .collection(category).document(boardId).collection("Comment")
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.get().await().documents))
    }.catch {
        emit(FireStoreResponse.Failure("댓글을 받아오는데 실패했습니다."))
    }
    
    fun insertComment(category : String ,comment: CommentDto) = flow {
        val query = boardDB.document(category)
            .collection(category).document(comment.boardId)
            .collection("Comment").document(comment.id)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.set(comment).await()))
    }.catch{
        emit(FireStoreResponse.Failure("데이터를 저장하는데 실패했습니다."))
    }

    fun insertCommentToUser(userId:String, dto : UserComment)= flow{
        val query = userDB.document(userId)
            .collection("Comment")
            .document(dto.id)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.set(dto).await()))
    }.catch {
        emit(FireStoreResponse.Failure("데이터를 저장하는데 실패했습니다."))
    }
}