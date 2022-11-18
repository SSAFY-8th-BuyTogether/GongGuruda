package com.buy.together.ui.viewmodel

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.network.firestore.FireStoreInfo
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.data.repository.AddressRepository
import com.buy.together.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddressViewModel : BaseViewModel() {
    private val repository = AddressRepository.getInstance()

    fun getAddressList() = liveData(Dispatchers.IO){
        repository.getAddressList(authToken).collect { addressList ->
            val addressDtoList = addressList?.map { address-> address?.makeToAddressDto() }
            emit(addressDtoList)
        }
    }

    fun addAddress(dto: AddressDto) = liveData(Dispatchers.IO) {
        repository.addAddressToServer(authToken, dto).collect(){ emit(it) }
    }

    fun deleteAddress(dto: AddressDto) = viewModelScope.launch{
        repository.deleteAddressFromServer(authToken, dto)
    }

}