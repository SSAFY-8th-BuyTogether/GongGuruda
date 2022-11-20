package com.buy.together.ui.view.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.domain.MyWriteCommentDto
import com.buy.together.databinding.FragmentMyCommentBinding
import com.buy.together.ui.adapter.MyWriteCommentAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.MyPageViewModel

class MyCommentFragment : BaseFragment<FragmentMyCommentBinding>(FragmentMyCommentBinding::bind, R.layout.fragment_my_comment) {
    private val viewModel : MyPageViewModel by viewModels()
    private lateinit var rvAdapter : MyWriteCommentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.getMyCommentInfo().observe(viewLifecycleOwner){
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
        binding.rvMyComment.adapter = rvAdapter
    }

    private fun setEmptyView(isSet : Boolean){
        if (isSet){
            binding.layoutMyCommentEmptyView.layoutEmptyView.visibility = View.VISIBLE
            binding.layoutMyCommentEmptyView.tvEmptyView.text = "작성하신 댓글"
        }else binding.layoutMyCommentEmptyView.layoutEmptyView.visibility = View.GONE
    }
    private fun setRVView(isSet : Boolean, itemList : ArrayList<MyWriteCommentDto> = arrayListOf()){
        if (isSet){
            binding.rvMyComment.visibility = View.VISIBLE
            rvAdapter.setListData(itemList)
        }else binding.rvMyComment.visibility = View.GONE
    }
}