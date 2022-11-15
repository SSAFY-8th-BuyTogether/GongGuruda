package com.buy.together.data.dto.firestore

sealed class FireStoreResponse<out T> {
    class Loading<out T>: FireStoreResponse<T>()

    data class Success<out T>(
        val data: T
    ): FireStoreResponse<T>()

    data class Failure<out T>(
        val errorMessage: String
    ): FireStoreResponse<T>()
}