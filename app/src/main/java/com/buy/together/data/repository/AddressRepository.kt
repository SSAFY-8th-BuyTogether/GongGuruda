package com.buy.together.data.repository

import android.content.Context
import android.util.Log
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.network.Address
import com.buy.together.data.model.network.firestore.FireStoreInfo
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.data.model.network.firestore.observeCollection
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AddressRepository {

    companion object{
        private var INSTANCE : AddressRepository? =null

        fun initialize(context: Context){
            if(INSTANCE == null) INSTANCE = AddressRepository()
        }

        fun get() : AddressRepository {
            return INSTANCE ?: throw IllegalStateException("NoteRepository must be initialized")
        }

        fun getInstance() : AddressRepository {
            return INSTANCE ?: synchronized(this){
                val instance = AddressRepository()
                INSTANCE = instance
                instance
            }
        }
    }

    private val fireStore = FirebaseFirestore.getInstance()

    fun getAddressList(userId : String) :  Flow<List<Address?>?> {
        return observeCollection(fireStore.collection(FireStoreInfo.USER).document(userId)
            .collection(FireStoreInfo.ADDRESS).orderBy(FireStoreInfo.ADDRESS_DATETIME, Query.Direction.DESCENDING))
    }

    fun addAddressToServer(userId : String, dto: AddressDto)= flow {
        val query = fireStore.collection(FireStoreInfo.USER).document(userId)
            .collection(FireStoreInfo.ADDRESS).document(dto.addressDetail)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.set(dto.makeToAddress()).await()))
    }.catch {
        emit(FireStoreResponse.Failure("주소 설정 중 에러가 발생했습니다.\n잠시 후 다시 시도해주세요."))
    }

    fun deleteAddressFromServer(userId: String, dto: AddressDto) {
        fireStore.collection(FireStoreInfo.USER).document(userId)
            .collection(FireStoreInfo.ADDRESS).document(dto.addressDetail).delete()
    }

}