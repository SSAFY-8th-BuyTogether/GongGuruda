package com.buy.together.data.model.network.firestore

sealed class FireStoreResponse<out T> {
    class Loading<out T>: FireStoreResponse<T>()

    data class Success<out T>(
        val data: T
    ): FireStoreResponse<T>()

    data class Failure<out T>(
        val errorMessage: String
    ): FireStoreResponse<T>()
}