package com.ssafy.smartstoredb.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.firebase.firestore.auth.User


class SharedPreferencesUtil (context: Context) {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "Application_Preferences"
        private const val AUTH_TOKEN = "AuthToken"
        private const val FCM_TOKEN = "FCMToken"
    }

    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun putAuthToken(authToken: String) {
        preferences.edit {
            putString(AUTH_TOKEN, authToken)
            apply()
        }
    }

    fun getAuthToken(): String? = preferences.getString(AUTH_TOKEN, "")
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

    fun getFCMToken(): String? = preferences.getString(FCM_TOKEN, "")

    fun deleteFCMToken() {
        preferences.edit {
            remove(FCM_TOKEN)
        }
    }

}