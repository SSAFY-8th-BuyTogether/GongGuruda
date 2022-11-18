package com.buy.together.ui.view.board

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.data.dto.BoardDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
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
        boardAdapter = BoardAdapter()
        binding.rvBoard.adapter = boardAdapter
        if(viewModel.category == "전체"){
            getDataAll()
        }else{
            getData()
        }

        boardAdapter.itemClickListener = object : BoardAdapter.ItemClickListener {
            override fun onClick(view: View, dto : BoardDto) {
                viewModel.boardDto = dto
                showBoardFragment()
            }
        }
    }

    fun getData(){
        viewModel.getBoardList(viewModel.category).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    val list = mutableListOf<BoardDto>()
                    response.data.forEach{
                        list.add(viewModel.makeBoard(it))
                    }
                    boardAdapter.boardDtoList = list
                    boardAdapter.notifyDataSetChanged()
                    dismissLoadingDialog()
                }
                is FireStoreResponse.Failure -> {
                    Toast.makeText(requireContext(), "게시글을 받아올 수 없습니다", Toast.LENGTH_SHORT).show()
                    dismissLoadingDialog()
                }
            }
        }
    }

    fun getDataAll(){
        val list = mutableListOf<BoardDto>()
        var count = 0
        viewModel.getBoardList(viewModel.category).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    response.data.forEach{
                        list.add(viewModel.makeBoard(it))
                    }
                    count++
                    if(count == viewModel.categoryListKr.size -1){
                        boardAdapter.boardDtoList = list
                        boardAdapter.notifyDataSetChanged()
                    }
                    dismissLoadingDialog()
                }
                is FireStoreResponse.Failure -> {
                    Toast.makeText(requireContext(), "게시글을 받아올 수 없습니다", Toast.LENGTH_SHORT).show()
                    dismissLoadingDialog()
                }
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