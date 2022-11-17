package com.buy.together.ui.view.address

import android.os.Bundle
import com.buy.together.R
import com.buy.together.databinding.FragmentAddressMapBinding
import com.buy.together.ui.base.BaseBottomSheetDialogFragment


class AddressMapFragment() : BaseBottomSheetDialogFragment<FragmentAddressMapBinding>(FragmentAddressMapBinding::inflate) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation;
    }

    override fun initView() { }

    override fun setEvent() {
        binding.apply {

        }
    }

}