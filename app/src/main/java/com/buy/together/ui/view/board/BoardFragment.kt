package com.buy.together.ui.view.board

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.databinding.FragmentBoardBinding
import com.buy.together.ui.adapter.BoardAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.BoardViewModel

class BoardFragment : BaseFragment<FragmentBoardBinding>(
    FragmentBoardBinding::bind, R.layout.fragment_board
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
    }

    fun initListener(){
        binding.ibBackButton.setOnClickListener{
            findNavController().popBackStack()
        }
    }
}