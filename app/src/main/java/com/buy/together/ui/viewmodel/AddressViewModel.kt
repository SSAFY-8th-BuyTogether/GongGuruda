package com.buy.together.ui.viewmodel

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.repository.AddressRepository
import com.buy.together.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddressViewModel : BaseViewModel() {
    private val repository = AddressRepository.getInstance()

    fun getAddress() = liveData(Dispatchers.IO){
        repository.getAddress(authToken).collect { addressList ->
            val addressDtoList = addressList?.map { address-> address?.makeToAddressDto() }
            emit(addressDtoList)
        }
    }

    fun getAddressList() = liveData(Dispatchers.IO){
        repository.getAddressList(authToken).collect { addressList ->
            val addressDtoList = addressList?.map { address-> address?.makeToAddressDto() }
            emit(addressDtoList)
        }
    }

    fun changeSelectedAddress(oldAddress : AddressDto, newAddress : AddressDto) = liveData(Dispatchers.IO) {
        repository.changeSelectedAddress(authToken, oldAddress.makeToAddress(), newAddress.makeToAddress())
            .collect(){ emit(it) }
    }

    fun addAddress(dto: AddressDto) = liveData(Dispatchers.IO) {
        repository.addAddressToServer(authToken, dto.makeToAddress()).collect(){ emit(it) }
    }

    fun deleteAddress(dto: AddressDto) = viewModelScope.launch{
        repository.deleteAddressFromServer(authToken, dto)
    }

}