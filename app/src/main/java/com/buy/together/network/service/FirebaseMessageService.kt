package com.buy.together.network.service

import com.buy.together.Application
import com.buy.together.network.api.FirebaseMessageApi
import com.buy.together.util.RetrofitCallback


// TODO : 얘로 활용
class FirebaseMessageService {
    private val service  = Application.retrofit.create(FirebaseMessageApi::class.java)

    fun sendFcm(title : String, body : String, callback : (item : Boolean?) -> Unit ){
        service.sendFcm(title, body).enqueue(RetrofitCallback<Boolean>(callback))
    }

}