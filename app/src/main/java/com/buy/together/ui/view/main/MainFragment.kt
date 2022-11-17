package com.buy.together.ui.view.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.data.dto.BoardDto
import com.buy.together.databinding.FragmentMainBinding
import com.buy.together.ui.adapter.BoardAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.BoardViewModel

private const val TAG = "MainFragment_싸피"
class MainFragment : BaseFragment<FragmentMainBinding>(
    FragmentMainBinding::bind, R.layout.fragment_main
) {
    private val viewModel : BoardViewModel by activityViewModels()
    private lateinit var boardAdapter : BoardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initListener()
    }

    fun initAdapter(){
        boardAdapter = BoardAdapter(viewModel)
        binding.rvMainBoard.adapter = boardAdapter
        val random = (1..4).random()
        Log.d(TAG, "initAdapter: $random")
        viewModel.getSavedBoard(viewModel.categoryListKr[random])
        showLoadingDialog(requireContext())
        viewModel.boardDtoListLiveData.observe(viewLifecycleOwner){ response ->
            boardAdapter.boardDtoList = response
            boardAdapter.notifyDataSetChanged()
            if(!viewModel.isLoading){
                dismissLoadingDialog()
            }
        }
    }

    fun initListener(){
        binding.apply {
            fabWriteBoard.setOnClickListener{
                showBoardWritingFragment()
            }
            llCategoryLayout.apply{
                ibImgAll.setOnClickListener{
                    onclickCategory("전체")
                }
                ibImgFood.setOnClickListener{
                    onclickCategory("식품")
                }
                ibImgStationery.setOnClickListener{
                    onclickCategory("문구")
                }
                ibImgDailyNeccessity.setOnClickListener{
                    onclickCategory("생활용품")
                }
                ibImgEtc.setOnClickListener{
                    onclickCategory("기타")
                }
            }
            boardAdapter.itemClickListener = object : BoardAdapter.ItemClickListener {
                override fun onClick(view: View, dto : BoardDto) {
                    viewModel.dto = dto
                    showBoardFragment()
                }
            }
        }
    }

    fun onclickCategory(type : String){
        viewModel.category = type
        showBoardCategoryFragment()
    }

    private fun showBoardWritingFragment() { findNavController().navigate(R.id.action_mainFragment_to_boardWritingFragment) }
    private fun showBoardCategoryFragment() { findNavController().navigate(R.id.action_mainFragment_to_boardCategoryFragment)}
    private fun showBoardFragment() {findNavController().navigate(R.id.action_mainFragment_to_boardFragment)}
}