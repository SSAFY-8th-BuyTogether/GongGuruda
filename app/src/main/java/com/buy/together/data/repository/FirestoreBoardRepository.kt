package com.buy.together.data.repository

import android.util.Log
import com.buy.together.data.model.Board
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

private const val TAG = "FirestoreBoardRepositor_싸피"
class FirestoreBoardRepository {
    private val db = FirebaseFirestore.getInstance().collection("Board")

    fun getSavedBoardList(category: String) : Task<QuerySnapshot>{
       return db.document(category)
            .collection("1") // category로 바꾸기
            .get()
           .addOnFailureListener{
               Log.d(TAG, "getBoardList: onFail")
           }
    }

    fun getSavedBoard(category : String, id : String):Task<DocumentSnapshot>{
        return db.document(category)
            .collection(id)
            .document(id)
            .get()
            .addOnFailureListener{
                Log.d(TAG, "getUserToken: Fail..ㅠㅠㅠㅠㅠㅠ")
            }
    }

    fun saveBoardItem(board : Board) : Task<DocumentReference>{
        return db.document(board.category)
            .collection(board.category)
            .add(board)
            .addOnFailureListener{ exception ->
                Log.e(TAG, "saveBoardItem: Error getting doccumment ${exception}")
            }
    }
}