package com.buy.together.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit


class SharedPreferencesUtil (context: Context) {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "Application_Preferences"
        private const val AUTH_TOKEN = "AuthToken"
        private const val FCM_TOKEN = "FCMToken"
        private const val USER_PROFILE = "UserProfile"
    }

    var preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun putAuthToken(authToken: String) {
        preferences.edit {
            putString(AUTH_TOKEN, authToken)
            apply()
            Log.d("체크", "icdddcdd!!!e: $authToken")
        }
        Log.d("체크", "initSharedPreference: ${getAuthToken()}")
    }

    fun getAuthToken(): String? = preferences.getString(AUTH_TOKEN, null)
    fun deleteAuthToken() {
        preferences.edit {
            remove(AUTH_TOKEN)
        }
    }

    fun putFCMToken(fcmToken: String) {
        preferences.edit {
            putString(FCM_TOKEN, fcmToken)
            apply()
        }
    }

    fun getFCMToken(): String? = preferences.getString(FCM_TOKEN, null)

    fun deleteFCMToken() {
        preferences.edit {
            remove(FCM_TOKEN)
        }
    }

    fun putUserProfile(userProfile: String){
        preferences.edit{
            putString(USER_PROFILE, userProfile)
            apply()
        }
    }

    fun getUserProfile(): String? = preferences.getString(USER_PROFILE,null)

    fun deleteProfile(){
        preferences.edit{
            remove(USER_PROFILE)
        }
    }

}