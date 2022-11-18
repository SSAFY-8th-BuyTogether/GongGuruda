package com.buy.together

import android.app.Application
import com.buy.together.data.repository.UserRepository
import com.buy.together.util.SharedPreferencesUtil

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
        fcmToken = sharedPreferences.getFCMToken()
    }

    private fun initRepository(){
        UserRepository.initialize(this)
    }


}