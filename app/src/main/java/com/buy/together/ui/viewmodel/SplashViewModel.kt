package com.buy.together.ui.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.buy.together.data.dto.firestore.FireStoreInfo
import com.buy.together.ui.base.BaseViewModel

class SplashViewModel : BaseViewModel(){

    private val _onSuccessGettingToken = MutableLiveData<Boolean>()
    val onSuccessGettingToken: LiveData<Boolean> get() = _onSuccessGettingToken

    fun getUserStatus() {
        fireStore.collection(FireStoreInfo.USER).document(authToken)
            .get()
            .addOnSuccessListener {
                if (it.data != null && it.data!![FireStoreInfo.USER_ID] == authToken) _onSuccessGettingToken.postValue(true)
                else  _onSuccessGettingToken.postValue(false)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                handleError(Throwable("알 수 없는 오류가 발생했습니다. 잠시 후에 시도해주세요."))
            }
    }


}