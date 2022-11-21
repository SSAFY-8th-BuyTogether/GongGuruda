package com.buy.together.ui.view.mypage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.data.model.domain.BoardDto
import com.buy.together.data.model.domain.MyParticipateDto
import com.buy.together.databinding.FragmentMyParticipateBinding
import com.buy.together.ui.adapter.MyParticipateAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.MyPageViewModel


class MyParticipateFragment : BaseFragment<FragmentMyParticipateBinding>(FragmentMyParticipateBinding::bind, R.layout.fragment_my_participate) {
    private val viewModel : MyPageViewModel by viewModels()
    private lateinit var rvAdapter : MyParticipateAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.getMyParticipateInfo().observe(viewLifecycleOwner){
            try {
                if (it==null || it.isEmpty()){
                    setEmptyView(true)
                    setRVView(false)
                }else{
                    val itemList = it as ArrayList<MyParticipateDto>
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
        rvAdapter = MyParticipateAdapter().apply {
            setItemClickListener(object : MyParticipateAdapter.ItemClickListener{
                override fun onClickItem(view: View, position: Int, participateDto: MyParticipateDto) {
                    showBoardFragment(BoardDto(id=participateDto.id, category = participateDto.category))
                }
            })
        }
        binding.rvMyParticipate.adapter = rvAdapter
    }

    private fun setEmptyView(isSet : Boolean){
        if (isSet){
            binding.layoutMyParticipateEmptyView.layoutEmptyView.visibility = View.VISIBLE
            binding.layoutMyParticipateEmptyView.tvEmptyView.text = "참여하신 글이"
        }else binding.layoutMyParticipateEmptyView.layoutEmptyView.visibility = View.GONE
    }
    private fun setRVView(isSet : Boolean, itemList : ArrayList<MyParticipateDto> = arrayListOf()){
        if (isSet){
            binding.rvMyParticipate.visibility = View.VISIBLE
            rvAdapter.setListData(itemList)
        }else binding.rvMyParticipate.visibility = View.GONE
    }


    private fun showBoardFragment(itemDto: BoardDto){ findNavController().navigate(MyParticipateFragmentDirections.actionMyParticipateFragmentToBoardFragment(boardDto = itemDto)) }
}