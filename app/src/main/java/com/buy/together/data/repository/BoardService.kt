package com.buy.together.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.buy.together.data.model.Board
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

private const val TAG = "BoardService_싸피"
class BoardService {
    private val db = FirebaseFirestore.getInstance().collection("Board")

    fun getBoardList(category: String): MutableLiveData<ArrayList<Board>> ?{
        db.document(category)
            .collection("1") // category로 바꾸기
            .get()
            .addOnSuccessListener { documents ->
                for(doc in documents){
//                    Log.d(TAG, "getBoardList: $doc")
                    val dto = makeBoard(doc)
                    Log.d(TAG, "getBoard: dto : $dto")
                }
            }
            .addOnFailureListener{

            }
        return null
    }

    fun getBoard(category : String, id : String) : Board? {
        db.document(category)
            .collection(id)
            .document(id)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "getUserToken: total - ${documents["title"]}")
                val dto = makeBoard(documents)
                Log.d(TAG, "getBoard: dto : $dto")
                // 비동기라서 리턴 안됨 -> liveDat에 Post해서 받기 필요
            }
            .addOnFailureListener{
                Log.d(TAG, "getUserToken: Fail..ㅠㅠㅠㅠㅠㅠ")
            }
        return null
    }

    fun makeBoard(document : DocumentSnapshot) : Board{
        val dto = Board(
            (document.id).toInt(),
            document["title"] as String,
            document["category"] as String,
            (document["deadLine"] as com.google.firebase.Timestamp).seconds,
            (document["price"] as Long).toInt(),
            document["content"] as String,
            (document["writeTime"] as com.google.firebase.Timestamp).seconds,
            document["writer"] as String,

            document["images"] as List<String>,
            (document["maxPeople"] as Long).toInt(),
            LatLng((document["meetPoint"] as GeoPoint).latitude,(document["meetPoint"] as GeoPoint).longitude),
            (document["meetTime"] as com.google.firebase.Timestamp).seconds,
            LatLng((document["buyPoint"] as GeoPoint).latitude,(document["buyPoint"] as GeoPoint).longitude),
        )
        return dto
    }
}