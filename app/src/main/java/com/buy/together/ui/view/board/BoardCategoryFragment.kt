package com.buy.together.ui.view.board

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.data.dto.BoardDto
import com.buy.together.databinding.FragmentBoardCategoryBinding
import com.buy.together.ui.adapter.BoardAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.BoardViewModel

class BoardCategoryFragment : BaseFragment<FragmentBoardCategoryBinding>(
    FragmentBoardCategoryBinding::bind, R.layout.fragment_board_category
){
    private val viewModel : BoardViewModel by activityViewModels()
    private lateinit var boardAdapter : BoardAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCategory.text = viewModel.category
        initAdapter()
        initListener()

    }

    fun initAdapter(){
        boardAdapter = BoardAdapter(viewModel)
        binding.rvBoard.adapter = boardAdapter
        viewModel.getSavedBoard(viewModel.category)
        showLoadingDialog(requireContext())
        viewModel.boardDtoListLiveData.observe(viewLifecycleOwner){
            boardAdapter.boardDtoList = it
            boardAdapter.notifyDataSetChanged()
            if(!viewModel.isLoading){
                dismissLoadingDialog()
            }
        }
        boardAdapter.itemClickListener = object : BoardAdapter.ItemClickListener {
            override fun onClick(view: View, dto : BoardDto) {
                viewModel.dto = dto
                showBoardFragment()
            }
        }
    }

    fun initListener(){
        binding.ibBackButton.setOnClickListener{
            findNavController().popBackStack()
        }
    }
    private fun showBoardFragment() {findNavController().navigate(R.id.action_boardCategoryFragment_to_boardFragment)}
}