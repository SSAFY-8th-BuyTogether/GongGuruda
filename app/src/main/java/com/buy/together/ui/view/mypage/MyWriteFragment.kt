package com.buy.together.ui.view.mypage


import android.view.View
import androidx.fragment.app.viewModels
import com.buy.together.R
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.databinding.FragmentMyWriteBinding
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.MyPageViewModel

class MyWriteFragment  : BaseFragment<FragmentMyWriteBinding>(FragmentMyWriteBinding::bind, R.layout.fragment_my_write) {
    private val myPageViewModel : MyPageViewModel by viewModels()




    private fun setEmptyView(isSet : Boolean){
        if (isSet){
            binding.layoutMyWriteEmptyView.layoutEmptyView.visibility = View.VISIBLE
        }else binding.layoutMyWriteEmptyView.layoutEmptyView.visibility = View.GONE
    }
    private fun setRVView(isSet : Boolean, itemList : ArrayList<AddressDto> = arrayListOf()){
        if (isSet){
            binding.rvMyWrite.visibility = View.VISIBLE
        }else binding.rvMyWrite.visibility = View.GONE
    }
}