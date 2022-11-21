package com.buy.together.ui.view.mypage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.domain.MyWriteCommentDto
import com.buy.together.databinding.FragmentMyWriteBinding
import com.buy.together.ui.adapter.MyWriteCommentAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.MyPageViewModel

class MyWriteFragment  : BaseFragment<FragmentMyWriteBinding>(FragmentMyWriteBinding::bind, R.layout.fragment_my_write) {
    private val viewModel : MyPageViewModel by viewModels()
    private lateinit var rvAdapter : MyWriteCommentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.getMyWriteInfo().observe(viewLifecycleOwner){
            try {
                if (it==null || it.isEmpty()){
                    setEmptyView(true)
                    setRVView(false)
                }else{
                    val itemList = it as ArrayList<MyWriteCommentDto>
                    setEmptyView(false)
                    setRVView(true, itemList)
                }
            }catch (e : Exception){
                setEmptyView(true)
                setRVView(false)
            }
        }
    }

    private fun initView(){
        rvAdapter = MyWriteCommentAdapter().apply {
            setItemClickListener(object : MyWriteCommentAdapter.ItemClickListener{
                override fun onClickItem(view: View, position: Int, writeCommentDto: MyWriteCommentDto) {
                    findNavController().navigate(MyWriteCommentFragmentDirections.actionMyWriteCommentFragmentToBoardFragment(writeCommentDto))
                }
            })
        }
        binding.rvMyWrite.adapter = rvAdapter
    }

    private fun setEmptyView(isSet : Boolean){
        if (isSet){
            binding.layoutMyWriteEmptyView.layoutEmptyView.visibility = View.VISIBLE
            binding.layoutMyWriteEmptyView.tvEmptyView.text = "작성하신 글이"
        }else binding.layoutMyWriteEmptyView.layoutEmptyView.visibility = View.GONE
    }
    private fun setRVView(isSet : Boolean, itemList : ArrayList<MyWriteCommentDto> = arrayListOf()){
        if (isSet){
            binding.rvMyWrite.visibility = View.VISIBLE
            rvAdapter.setListData(itemList)
        }else binding.rvMyWrite.visibility = View.GONE
    }
}