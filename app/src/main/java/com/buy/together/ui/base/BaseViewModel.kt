package com.buy.together.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buy.together.Application
import com.buy.together.data.repository.UserRepository
import com.buy.together.util.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

abstract class BaseViewModel : ViewModel() {

    private var _fireStore : FirebaseFirestore = FirebaseFirestore.getInstance()
    val fireStore : FirebaseFirestore get() = _fireStore
    private val _userRepository: UserRepository = UserRepository.get()
    val userRepository : UserRepository get() = _userRepository

    private var _fcmToken: String? = null
    val fcmToken: String get() = _fcmToken!!
    private var _authToken: String? = null
    val authToken: String get() = _authToken!!
    val isTokenAvailable: Boolean get() = _authToken != null

    init {
        _authToken = Application.authToken
        _fcmToken = Application.fcmToken
    }

    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>> = _error

    private val _onSuccessGettingFCMToken = MutableLiveData<String>()
    val onSuccessGettingFCMToken: LiveData<String> get() = _onSuccessGettingFCMToken

    fun handleError(exception: Throwable) {
        val message = exception.message ?: ""
        _error.value = Event(message)
    }

    fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let { _onSuccessGettingFCMToken.postValue(it) }
            } else error("FCM 토큰 얻기에 실패하였습니다. 잠시 후 다시 시도해주세요.")
        }
    }

}