package com.buy.together.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RetrofitCallback<T>(private val callback: (item : T?) -> Unit) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if(response.code() == 200) response.body()?.let { res -> callback(res) }
        else error("onResponse: Error Code ${response.code()}")
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        error(t.message ?: "통신오류")
    }
}