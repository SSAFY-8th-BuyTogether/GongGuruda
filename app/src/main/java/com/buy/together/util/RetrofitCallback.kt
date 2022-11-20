package com.buy.together.util

import android.content.Context
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RetrofitCallbackItem<T>(private val context: Context, private val operation: (item : T?) -> Unit) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if(response.code() == 200) {
            val result = response.body()
            operation(result)
        } else {
            val errorMessage = "onResponse : Error code ${response.code()}"
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
    override fun onFailure(call: Call<T>, t: Throwable) {
        val errorMessage = when(t){
            is IOException -> "No Internet Connection!"
            is HttpException -> "Internet Connection is Wrong!"
            else -> t.localizedMessage
        }
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }
}

class RetrofitCallbackItemList<T>(private val context: Context, private val operation: (item : MutableList<T>) -> Unit) : Callback<MutableList<T>> {
    override fun onResponse(call: Call<MutableList<T>>, response: Response<MutableList<T>>) {
        if(response.code() == 200) {
            val result = response.body() ?: mutableListOf()
            operation(result)
        } else {
            val errorMessage = "onResponse : Error code ${response.code()}"
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
    override fun onFailure(call: Call<MutableList<T>>, t: Throwable) {
        val errorMessage = when(t){
            is IOException -> "No Internet Connection!"
            is HttpException -> "Internet Connection is Wrong!"
            else -> t.localizedMessage
        }
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }
}