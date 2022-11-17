package com.buy.together.ui.view.address


import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.databinding.FragmentAddressBinding
import com.buy.together.ui.base.BaseBottomSheetDialogFragment



class AddressFragment() : BaseBottomSheetDialogFragment<FragmentAddressBinding>(FragmentAddressBinding::inflate) {

    override fun initView() {
        binding.apply {
            layoutAddress
        }
    }

    override fun setEvent() {
        binding.apply {
            btnAddressSearch.setOnClickListener { showAddressSearchResultFragment() }
            btnAddressLocationSearch.setOnClickListener { showAddressMapFragment() }
        }
    }

    private fun showAddressSearchResultFragment(){ findNavController().navigate(R.id.action_addressFragment_to_addressSearchFragment) }
    private fun showAddressMapFragment(){ findNavController().navigate(R.id.action_addressFragment_to_addressMapFragment) }


}