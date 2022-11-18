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
    private var repository = RepositoryUtils.boardRepository
    val categoryListKr = arrayListOf("전체","식품","문구","생활용품","기타")
    private val fbStore = FirebaseStorage.getInstance().reference.child("images")

    var category : String = "" //main -> boardCategory
    var boardDto : BoardDto? = null //main, boardCategory -> board

    // 카테고리별 데이터 가져오기
    fun getBoardList(category : String) = liveData(Dispatchers.IO){ //TODO : 코루틴, 지역 내에 게시글만 가져오기, 정렬
        Log.d(TAG, "가져오기 시작 =========================")
        if(category == "전체"){
            for(i in 1..4){
                repository.getBoardList(categoryListKr[i]).collect { emit(it) }
            }
        }else {
            repository.getBoardList(category).collect { emit(it) }
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

    //참여자 추가 true / 참여자 삭제 false
    fun insertParticipator(boardDto: BoardDto, userId : String, flag : Boolean) = liveData(Dispatchers.IO){
        val arrayList : ArrayList<String> = ArrayList(boardDto.participator)
        if(flag){
            arrayList.add(userId)
        }else{
            arrayList.remove(userId)
        }
        boardDto.participator = arrayList
        repository.saveBoard(boardDto).collect{emit(it)}
    }

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

            document["participator"] as List<String>,
            document["images"] as List<String>,
            (document["maxPeople"] as Long?)?.toInt(),
            document["meetPoint"] as String?,
            document["meetTime"] as Long?,
            document["buyPoint"] as String?,
        )
        return dto
    }

}