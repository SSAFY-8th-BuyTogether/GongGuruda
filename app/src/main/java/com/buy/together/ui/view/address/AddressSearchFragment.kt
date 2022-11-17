package com.buy.together.ui.view.address

import android.os.Bundle
import com.buy.together.R
import com.buy.together.databinding.FragmentAddressSearchBinding
import com.buy.together.ui.base.BaseBottomSheetDialogFragment

class AddressSearchFragment() : BaseBottomSheetDialogFragment<FragmentAddressSearchBinding>(FragmentAddressSearchBinding::inflate) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation;
    }

    override fun initView() {
        binding.apply {
            
        }
    }

    override fun setEvent() { }

}