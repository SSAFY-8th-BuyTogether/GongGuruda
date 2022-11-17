package com.buy.together.data.repository

import android.util.Log
import com.buy.together.data.dto.BoardDto
import com.buy.together.data.dto.firestore.FireStoreResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreBoardRepositor_싸피"
class FirestoreBoardRepository {
    private val db = FirebaseFirestore.getInstance().collection("Board")

    fun getSavedBoardList(category: String) : Task<QuerySnapshot>{
       return db.document(category)
            .collection(category)
            .get()
    }

    fun getEachBoard(category : String, id : String) = flow {
        val query = db.document(category).collection(category).whereEqualTo("id", id)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.get().await().documents[0]))
    }.catch {
        emit(FireStoreResponse.Failure("존재하지 않는 게시글입니다."))
    }

    fun saveBoard(boardDto : BoardDto)= flow {
        val query = db.document(boardDto.category)
            .collection(boardDto.category)
            .document(boardDto.id)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.set(boardDto).await()))
    }.catch {
        emit(FireStoreResponse.Failure("데이터를 저장하는데 실패했습니다."))
    }
}