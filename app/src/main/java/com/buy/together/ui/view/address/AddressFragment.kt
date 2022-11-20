package com.buy.together.ui.view.address


import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.buy.together.R
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentAddressBinding
import com.buy.together.ui.adapter.AddressAdapter
import com.buy.together.ui.base.BaseBottomSheetDialogFragment
import com.buy.together.ui.viewmodel.AddressViewModel

class AddressFragment() : BaseBottomSheetDialogFragment<FragmentAddressBinding>(FragmentAddressBinding::inflate) {
    private val navArgs : AddressFragmentArgs by navArgs()
    private val viewModel: AddressViewModel by viewModels()
    private lateinit var rvAdapter : AddressAdapter
    private var selectedAddressDto : AddressDto = AddressDto()

    override fun initView() {
        if (navArgs.isSelected) {
            viewModel.getAddress().observe(viewLifecycleOwner) {
                if (!(it == null || it.isEmpty())) {
                    selectedAddressDto = (it as ArrayList<AddressDto>)[0]
                    setSelectedView(selectedAddressDto)
                }
            }
        }
        rvAdapter = AddressAdapter().apply {
            setItemClickListener(object : AddressAdapter.ItemClickListener{
                override fun onClickItem(view: View, position: Int, addressDto: AddressDto) {
                    if (navArgs.isSelected) {
                        viewModel.changeSelectedAddress(selectedAddressDto.apply { selected = false }, addressDto.apply { selected = true })
                            .observe(viewLifecycleOwner){ response ->
                            when(response){
                                is FireStoreResponse.Loading -> showLoadingDialog(requireContext())
                                is FireStoreResponse.Success -> {
                                    dismissLoadingDialog()
                                    this@AddressFragment.dismiss()
                                }
                                is FireStoreResponse.Failure -> {
                                    showToast(response.errorMessage)
                                    dismissLoadingDialog()
                                }
                            }
                        }
                    } else{
                        val bundle = Bundle().apply { putSerializable("address",addressDto) }
                        requireActivity().supportFragmentManager.setFragmentResult("getAddress",bundle)
                        this@AddressFragment.dismiss()
                    }
                }
                override fun onClickRemove(view: View, position: Int, addressDto: AddressDto) {
                    viewModel.deleteAddress(addressDto)
                }
            })
        }
        binding.rvAddressSearchRecent.adapter = rvAdapter
        viewModel.getAddressList().observe(viewLifecycleOwner){
            try{
                if (it==null || it.isEmpty()) {
                    setEmptyView(true)
                    setRVView(false)
                }else{
                    val itemList = it as ArrayList<AddressDto>
                    setEmptyView(false)
                    setRVView(true, itemList)
                }
            }
            catch (e : Exception){
                setEmptyView(true)
                setRVView(false)
            }
        }
    }

    override fun setEvent() {
        binding.apply {
            btnAddressSearch.setOnClickListener { showAddressSearchResultFragment() }
            btnAddressLocationSearch.setOnClickListener { showAddressMapFragment() }
        }
    }
    private fun setSelectedView(addressDto: AddressDto){
        binding.layoutAddressSelected.layoutAddressSelected.visibility = View.VISIBLE
        binding.layoutAddressSelected.tvAddressItem.text = addressDto.addressDetail
    }
    private fun setEmptyView(isSet : Boolean){
        if (isSet){
            binding.layoutAddressEmptyView.layoutAddressEmptyView.visibility = View.VISIBLE
            binding.layoutAddressEmptyView.tvEmptyView.text = getString(R.string.tv_address_empty)
        }else binding.layoutAddressEmptyView.layoutAddressEmptyView.visibility = View.GONE
    }
    private fun setRVView(isSet : Boolean, itemList : ArrayList<AddressDto> = arrayListOf()){
        if (isSet) {
            binding.rvAddressSearchRecent.visibility = View.VISIBLE
            rvAdapter.setListData(itemList)
        } else binding.rvAddressSearchRecent.visibility = View.GONE
    }
    private fun showAddressSearchResultFragment(){ findNavController().navigate(R.id.action_addressFragment_to_addressSearchFragment) }
    private fun showAddressMapFragment(){ findNavController().navigate(R.id.action_addressFragment_to_addressMapFragment) }


}