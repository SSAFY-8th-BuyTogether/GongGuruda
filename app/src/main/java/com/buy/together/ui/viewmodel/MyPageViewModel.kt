package com.buy.together.ui.viewmodel

import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

class MyPageViewModel : UserViewModel() {

    fun getMyWriteInfo() = liveData(Dispatchers.IO){
        userRepository.getMyWriteInfo(authToken).collect { writeList ->
            val myWriteCommentList = writeList?.map { write-> write?.makeToMyWriteCommentDto() }
            emit(myWriteCommentList)
        }
    }

    fun getMyCommentInfo() = liveData(Dispatchers.IO){
        userRepository.getMyCommentInfo(authToken).collect { commentList ->
            val myWriteCommentList = commentList?.map { comment-> comment?.makeToMyWriteCommentDto() }
            emit(myWriteCommentList)
        }
    }

}