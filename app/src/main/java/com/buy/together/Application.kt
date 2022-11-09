package com.buy.together

import android.app.Application
import com.ssafy.smartstoredb.util.SharedPreferencesUtil

class Application : Application() {

    companion object{
        private lateinit var sharedPreferences: SharedPreferencesUtil
        var authToken : String? = null
        var fcmToken : String? = null
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = SharedPreferencesUtil(this)
        authToken = sharedPreferences.getAuthToken()
        fcmToken = sharedPreferences.getFCMToken()
    }

}