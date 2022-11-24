package com.buy.together

import android.app.Application
import com.buy.together.data.repository.UserRepository
import com.buy.together.util.SharedPreferencesUtil
import com.naver.maps.map.NaverMapSdk
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Application : Application() {

    private val serverURL = "http://192.168.33.131:9090/"    // TODO : AWS Hosting + URL 변경

    companion object{
        lateinit var retrofit: Retrofit
        lateinit var sharedPreferences: SharedPreferencesUtil
        var authToken : String? = null
        var fcmToken : String? = null
    }

    override fun onCreate() {
        super.onCreate()
        initRetrofit()
        initSharedPreference()
        initRepository()
    }

    private fun initRetrofit(){
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(30, TimeUnit.SECONDS).build()

        retrofit = Retrofit.Builder()
            .baseUrl(serverURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
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