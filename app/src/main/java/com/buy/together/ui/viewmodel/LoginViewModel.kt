package com.buy.together.ui.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buy.together.data.dto.FireStore
import com.buy.together.ui.base.BaseViewModel

class LoginViewModel : BaseViewModel(){

    private val _onSuccessLogin = MutableLiveData<Boolean>()
    val onSuccessLogin: LiveData<Boolean> get() = _onSuccessLogin

    fun sendLoginInfo(userId : String, userPwd : String){
        fireStore.collection(FireStore.USER)
            .whereEqualTo(FireStore.USER_ID, userId)
            .whereEqualTo(FireStore.USER_PWD, userPwd)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) _onSuccessLogin.postValue(false)
                else _onSuccessLogin.postValue(true)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                handleError(Throwable("로그인 과정 중에 오류가 발생했습니다. 잠시 후에 시도해주세요."))
            }
    }

}