package com.buy.together.network.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface FirebaseMessageApi {
    @POST("fcm")
    fun sendFcm(@Query("title") title:String, @Query("body") body:String ) : Call<Boolean>

}