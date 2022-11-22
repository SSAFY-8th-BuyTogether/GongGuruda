package com.buy.together.ui.viewmodel

import android.util.Log
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect

class MyPageViewModel : UserViewModel() {

    fun getMyAlarmInfo() = liveData(Dispatchers.IO) {
        userRepository.getMyAlarmInfo(authToken).collect(){ alarmList ->
            val myAlarmList = alarmList?.map { alarm -> alarm?.makeToAlarmDto() }
            emit(myAlarmList)
        }
    }

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

    fun getMyParticipateInfo() = liveData(Dispatchers.IO) {
        userRepository.getMyParticipate(authToken).collect{ participateList ->
            val myParticipateList = participateList?.map { participate -> participate?.makeToMyParticipateDto() }
            emit(myParticipateList)
        }
    }

}