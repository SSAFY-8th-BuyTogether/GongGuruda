package com.buy.together.data.repository

import android.util.Log
import com.buy.together.data.dto.BoardDto
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

private const val TAG = "FirestoreBoardRepositor_싸피"
class FirestoreBoardRepository {
    private val db = FirebaseFirestore.getInstance().collection("Board")

    fun getSavedBoardList(category: String) : Task<QuerySnapshot>{
       return db.document(category)
            .collection(category)
            .get()
    }

    fun getSavedBoard(category : String, id : String):Task<DocumentSnapshot>{
        return db.document(category)
            .collection(id)
            .document(id)
            .get()
            .addOnFailureListener{
                Log.d(TAG, "getSavedBoard : Error getting field ${it}")
            }
    }

    fun saveBoardItem(boardDto : BoardDto) : Task<Void> {
        return db.document(boardDto.category)
            .collection(boardDto.category)
            .document(boardDto.id)
            .set(boardDto)
            .addOnFailureListener{ exception ->
                Log.e(TAG, "saveBoardItem: Error getting doccumment ${exception}")
            }
    }
}