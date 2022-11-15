package com.buy.together.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buy.together.data.dto.BoardDto
import com.buy.together.util.RepositoryUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage

private const val TAG = "BoardViewModel_싸피"
class BoardViewModel : ViewModel() {
    private var repository = RepositoryUtils.firestoreBoardRepository
    private val categoryListKr = arrayListOf("전체","식품","문구","생활용품","기타")
    private val fbStore = FirebaseStorage.getInstance().reference.child("images")
    var category : String = ""

    val boardDtoListLiveData: LiveData<List<BoardDto>> get() = _boardDtoListLiveData
    private var _boardDtoListLiveData : MutableLiveData<List<BoardDto>> = MutableLiveData()

    // 카테고리별 데이터 가져오기
    fun getSavedBoard(category : String){
        _boardDtoListLiveData.postValue(mutableListOf())
        if(category == "전체"){
            getAllSavedBoard()
        }else{
            Log.d(TAG, "가져오기 시작 =========================")
            repository.getSavedBoardList(category)
                .addOnSuccessListener { documents ->
                    val savedBoardListDto : MutableList<BoardDto> = mutableListOf()
                    for(doc in documents){
                        val dto = makeBoard(doc)
                        savedBoardListDto.add(dto)
                        Log.d(TAG, "가져오기 성공 | dto : $dto==========")
                    }
                    _boardDtoListLiveData.postValue(savedBoardListDto)
                }
        }
    }

    //모든 데이터 가져오기
    fun getAllSavedBoard(){
        val savedBoardListDto : MutableList<BoardDto> = mutableListOf()
        for(i in 1..4){
            repository.getSavedBoardList(categoryListKr[i])
                .addOnSuccessListener { documents ->
                    for(doc in documents){
                        val dto = makeBoard(doc)
                        savedBoardListDto.add(dto)
                    }
                }
        }
        _boardDtoListLiveData.postValue(savedBoardListDto)
    }

    var boardDtoLiveData : MutableLiveData<BoardDto> = MutableLiveData()

    //해당 id의 데이터 가져오기
    fun getBoard(category : String, id : String) : LiveData<BoardDto> {
        repository.getSavedBoard(category,id)
            .addOnSuccessListener { documents ->
                Log.d(TAG, "getUserToken: total - ${documents["title"]}")
                val dto = makeBoard(documents)
                Log.d(TAG, "getBoard: dto : $dto")
                // 비동기라서 리턴 안됨 -> liveDat에 Post해서 받기 필요
                boardDtoLiveData.postValue(dto)
            }
        return boardDtoLiveData
    }

    //게시글 저장
    fun saveBoardToFirebase(boardDto : BoardDto, imgs : ArrayList<Uri>){ //TODO : board 넣기, 자동 id 가져오기
        if(imgs.isNotEmpty()){
            val list = arrayListOf<String>()
            for(i in 0..imgs.size-1){ //이미지 저장
                    fbStore.child("IMAGE_${boardDto.id}_${i}.png").putFile(imgs[i])
                        .addOnSuccessListener{
                            list.add("IMAGE_${boardDto.id}_${i}.png")
                            Log.d(TAG, "이미지 저장 성공~~~~~~~~~~$i\n image : ${list}")
                            if(i == imgs.size-1){
                                Log.d(TAG, "모든 이미지 완료 ${list}")
                                boardDto.images = list
                                insertBoardData(boardDto)
                            }
                        }
            }
        }else{
            insertBoardData(boardDto)
        }
    }

    //게시글 넣기
    private fun insertBoardData(boardDto: BoardDto){
        repository.saveBoardItem(boardDto).addOnSuccessListener {
            Log.v(TAG,"데이터 넣기 성공!!")
            getSavedBoard(boardDto.category)
        }
    }
    
    //이미지 가져오기
    fun getImage(url : String, listener : OnSuccessListener<Uri>){
        fbStore.child(url).downloadUrl
            .addOnSuccessListener(listener)
            .addOnFailureListener{
                Log.d(TAG, "getImage: Fail ${it.message}")
            }
    }

    private fun makeBoard(document : DocumentSnapshot) : BoardDto {
        val meetPoint: LatLng? =
            if(document["meetPoint"] as GeoPoint? == null) null
            else LatLng((document["meetPoint"] as GeoPoint?)?.latitude!!,(document["meetPoint"] as GeoPoint?)?.longitude!!)
        val buyPoint: LatLng? =
            if(document["buyPoint"] as GeoPoint? == null) null
            else LatLng((document["buyPoint"] as GeoPoint?)?.latitude!!,(document["buyPoint"] as GeoPoint?)?.longitude!!)

        val dto = BoardDto(
            document.id,
            document["title"] as String,
            document["category"] as String,
            document["deadLine"] as Long,
            (document["price"] as Long).toInt(),
            document["content"] as String,
            document["writeTime"] as Long,
            document["writer"] as String,

            document["images"] as List<String>,
            (document["maxPeople"] as Long?)?.toInt(),
            meetPoint,
            document["meetTime"] as Long?,
            buyPoint,
        )
        return dto
    }

}