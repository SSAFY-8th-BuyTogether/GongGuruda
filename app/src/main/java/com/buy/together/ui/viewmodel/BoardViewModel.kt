package com.buy.together.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buy.together.data.dto.Board
import com.buy.together.util.RepositoryUtils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint

private const val TAG = "BoardViewModel_싸피"
class BoardViewModel : ViewModel() {
    private var repository = RepositoryUtils.firestoreBoardRepository
    private val categoryList = arrayListOf("all","food","stationery","dailyNeccessity","etc")
    private val categoryListKr = arrayListOf("전체","식품","문구","생활용품","기타")
    var categoryIdx : Int = 0

    val boardListLiveData: LiveData<List<Board>> get() = _boardListLiveData
    private var _boardListLiveData : MutableLiveData<List<Board>> = MutableLiveData()

    fun getCategoryNameKr(): String{
        return categoryListKr[categoryIdx]
    }

    // 카테고리별 데이터 가져오기
    fun getSavedBoard(category : Int){
        if(category == 0){
            getAllSavedBoard()
        }else{
            repository.getSavedBoardList(categoryList[category])
                .addOnSuccessListener { documents ->
                    val savedBoardList : MutableList<Board> = mutableListOf()
                    for(doc in documents){
                        val dto = makeBoard(doc)
                        savedBoardList.add(dto)
                        Log.d(TAG, "getBoard: dto : $dto")
                    }
                    _boardListLiveData.postValue(savedBoardList)
                }
        }
    }

    fun getAllSavedBoard(){
        val savedBoardList : MutableList<Board> = mutableListOf()
        for(i in 1..4){
            repository.getSavedBoardList(categoryList[i])
                .addOnSuccessListener { documents ->
                    val savedBoardList : MutableList<Board> = mutableListOf()
                    for(doc in documents){
                        val dto = makeBoard(doc)
                        savedBoardList.add(dto)
                    }
                }
        }
        _boardListLiveData.postValue(savedBoardList)
    }

    var boardLiveData : MutableLiveData<Board> = MutableLiveData()

    //해당 id의 데이터 가져오기
    fun getBoard(category : String, id : String) : LiveData<Board> {
        repository.getSavedBoard(category,id)
            .addOnSuccessListener { documents ->
                Log.d(TAG, "getUserToken: total - ${documents["title"]}")
                val dto = makeBoard(documents)
                Log.d(TAG, "getBoard: dto : $dto")
                // 비동기라서 리턴 안됨 -> liveDat에 Post해서 받기 필요
                boardLiveData.postValue(dto)
            }
        return boardLiveData
    }

    fun saveBoardToFirebase(board : Board){ //TODO : board 넣기, 자동 id 가져오기
        repository.saveBoardItem(board).addOnSuccessListener {
            Log.e(TAG,"데이터 넣기 성공!!")
        }
    }

    private fun makeBoard(document : DocumentSnapshot) : Board {
        val dto = Board(
            (document.id).toInt(),
            document["title"] as String,
            document["category"] as String,
            (document["deadLine"] as Timestamp).seconds,
            (document["price"] as Long).toInt(),
            document["content"] as String,
            (document["writeTime"] as Timestamp).seconds,
            document["writer"] as String,

            document["images"] as List<String>,
            (document["maxPeople"] as Long).toInt(),
            LatLng((document["meetPoint"] as GeoPoint).latitude,(document["meetPoint"] as GeoPoint).longitude),
            (document["meetTime"] as Timestamp).seconds,
            LatLng((document["buyPoint"] as GeoPoint).latitude,(document["buyPoint"] as GeoPoint).longitude),
        )
        return dto
    }

}