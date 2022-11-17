package com.buy.together.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.buy.together.data.dto.BoardDto
import com.buy.together.util.RepositoryUtils
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*

private const val TAG = "BoardViewModel_싸피"
class BoardViewModel : ViewModel() {
    private var repository = RepositoryUtils.firestoreBoardRepository
    val categoryListKr = arrayListOf("전체","식품","문구","생활용품","기타")
    private val fbStore = FirebaseStorage.getInstance().reference.child("images")

    var category : String = "" //main -> boardCategory

    //boardlist
    val boardDtoListLiveData: LiveData<List<BoardDto>> get() = _boardDtoListLiveData
    private var _boardDtoListLiveData : MutableLiveData<List<BoardDto>> = MutableLiveData()
    var isLoading = false

    var dto : BoardDto? = null //main, boardCategory -> board

    // 카테고리별 데이터 가져오기
    fun getSavedBoard(category : String){ //TODO : 코루틴, 지역 내에 게시글만 가져오기, 정렬
        isLoading = true
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
                        isLoading = false
                    }
                    _boardDtoListLiveData.postValue(savedBoardListDto)
                }
                .addOnFailureListener{
                    Log.d(TAG, "통신 오류")
                    isLoading = false
                }
        }
    }

    //모든 데이터 가져오기
    fun getAllSavedBoard(){
        isLoading = true
        val savedBoardListDto : MutableList<BoardDto> = mutableListOf()
        for(i in 1..4){
            repository.getSavedBoardList(categoryListKr[i])
                .addOnSuccessListener { documents ->
                    for(doc in documents){
                        val dto = makeBoard(doc)
                        savedBoardListDto.add(dto)
                    }
                    if(i == 4){
                        _boardDtoListLiveData.postValue(savedBoardListDto)
                        isLoading = false
                    }
                }
        }
    }

    //해당 id의 데이터 가져오기
    fun getEachBoard(category : String, id : String) = liveData(Dispatchers.IO){
        Log.d(TAG, "getBoard: category : ${category}, id : $id")
        repository.getEachBoard(category,id).collect{emit(it)}
    }

    //imagelist
    val ImgLiveData: LiveData<List<String>> get() = _ImgLiveData
    private var _ImgLiveData : MutableLiveData<List<String>> = MutableLiveData()

    fun saveImage(boardDto : BoardDto, imgs : ArrayList<Uri>){
        if(imgs.isEmpty()){
            _ImgLiveData.postValue(listOf())
            return
        }
        val list = arrayListOf<String>()
        for(i in 0..imgs.size-1){ //이미지 저장
            fbStore.child("IMAGE_${boardDto.id}_${i}.png").putFile(imgs[i])
                .addOnSuccessListener{
                    CoroutineScope(Dispatchers.IO).launch {
                        getImage("IMAGE_${boardDto.id}_${i}.png"){
                            list.add(it.toString())
                            Log.d(TAG, "이미지 저장 성공~~~~~~~~~~$i\n image : ${list}")
                            if (list.size == imgs.size) {
                                Log.d(TAG, "모든 이미지 완료 ${list}")
                                boardDto.images = list
                                _ImgLiveData.postValue(list)
                            }
                        }
                    }
                }
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

    //게시글 저장
    fun saveBoard(boardDto : BoardDto) = liveData(Dispatchers.IO){ //TODO : user에 넣기
        repository.saveBoard(boardDto).collect{emit(it)}
    }

//    //이미지 가져오기
//    suspend fun getImage(url : String) : String{
//        var _uri : String = ""
//        fbStore.child(url).downloadUrl
//            .addOnSuccessListener{
//                _uri = it.toString()
//            }
//            .addOnFailureListener{
//                Log.d(TAG, "getImage: Fail ${it.message}")
//            }
//            .await()
//        return _uri
//    }

    fun makeBoard(document : DocumentSnapshot) : BoardDto {
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
            document["meetPoint"] as String?,
            document["meetTime"] as Long?,
            document["buyPoint"] as String?,
        )
        return dto
    }

}