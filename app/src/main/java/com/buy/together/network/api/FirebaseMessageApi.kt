package com.buy.together.network.api

import com.buy.together.data.model.network.FireStoreMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FirebaseMessageApi {
    @POST("fcm")
    fun sendFcm(@Body fcmMessage: FireStoreMessage) : Call<Boolean>

}