package com.buy.together.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.buy.together.data.dto.BoardDto
import com.buy.together.data.dto.CommentDto
import com.buy.together.data.dto.usercollection.UserBoard
import com.buy.together.data.dto.usercollection.UserComment
import com.buy.together.data.dto.usercollection.UserParticipate
import com.buy.together.data.repository.BoardRepository
import com.buy.together.util.GalleryUtils.insertImage
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*

private const val TAG = "BoardViewModel_싸피"
class BoardViewModel : ViewModel() {
    private var repository = BoardRepository()
    val categoryListKr = arrayListOf("전체","식품","문구","생활용품","기타")

    var category : String = "" //main -> boardCategory
    var boardDto : BoardDto? = null //main, boardCategory -> board

    // 카테고리별 데이터 가져오기
    fun getBoardList(category : String) = liveData(Dispatchers.IO){ //TODO : 코루틴, 지역 내에 게시글만 가져오기, 정렬
        Log.d(TAG, "가져오기 시작 =========================")
        if(category == "전체"){
            for(i in 1..4){
                repository.getBoardList(categoryListKr[i]).collect { emit(it) }
            }
        }else{
            repository.getBoardList(category).collect { emit(it) }
        }
    }

    //해당 id의 데이터 가져오기
    fun getEachBoard(category : String, id : String) = liveData(Dispatchers.IO){
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
            insertImage("IMAGE_${boardDto.id}_${i}.png",imgs[i]){
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

    //게시글 저장
    fun saveBoard(boardDto : BoardDto) = liveData(Dispatchers.IO){
        repository.saveBoard(boardDto).collect{emit(it)}
    }

    fun saveBoardToUser(userId: String,boardDto: BoardDto) = liveData(Dispatchers.IO) {
        val userBoard = UserBoard(
            id= boardDto.id,
            title= boardDto.title,
            category= boardDto.category,
            content= boardDto.content,
            time= Timestamp.now(),
        )
        repository.insertBoardToUser(userId,userBoard).collect{emit(it)}
    }

    fun removeBoardFromUser(userId: String,boardDto: BoardDto) = liveData(Dispatchers.IO) {
        val userBoard = UserBoard(
            id= boardDto.id,
            title= boardDto.title,
            category= boardDto.category,
            content= boardDto.content,
            time= Timestamp.now(),
        )
        repository.deleteBoardFromUser(userId,userBoard).collect{emit(it)}
    }

    fun removeBoard(boardDto: BoardDto) = liveData(Dispatchers.IO) {
        repository.deleteBoard(boardDto).collect{emit(it)}
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

    fun insertUserParticipate(userId: String, boardDto: BoardDto,flag : Boolean) = liveData(Dispatchers.IO) {
        val participate = UserParticipate(
            id= boardDto.id,
            category= boardDto.category,
        )
        if(flag){
            repository.insertParticipator(userId,participate).collect{emit(it)}
        }else{
            repository.deleteParticipator(userId,participate).collect{emit(it)}
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