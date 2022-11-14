package com.buy.together

import android.app.Application
import android.util.Log
import androidx.viewbinding.ViewBinding
import com.buy.together.data.repository.UserRepository
import com.buy.together.ui.base.BaseFragment
import com.buy.together.util.SharedPreferencesUtil
import com.google.firebase.firestore.FirebaseFirestore

class Application : Application() {

    companion object{
        lateinit var sharedPreferences: SharedPreferencesUtil
        var authToken : String? = null
        var fcmToken : String? = null
    }

    override fun onCreate() {
        super.onCreate()
        initSharedPreference()
        initRepository()
    }

    private fun initSharedPreference(){
        sharedPreferences = SharedPreferencesUtil(this)
        authToken = sharedPreferences.getAuthToken()
        Log.d("체크", "initSharedPreference: $authToken")
        fcmToken = sharedPreferences.getFCMToken()
    }

    private fun initRepository(){
        UserRepository.initialize(this)
    }


}