package com.buy.together.ui.view.board

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.buy.together.R
import com.buy.together.data.model.Board
import com.buy.together.databinding.FragmentBoardBinding
import com.buy.together.ui.adapter.BoardAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.BoardViewModel

class BoardFragment : BaseFragment<FragmentBoardBinding>(
    FragmentBoardBinding::bind, R.layout.fragment_board
){
    private val viewModel : BoardViewModel by activityViewModels()
    private val boardList = arrayListOf<Board>()
    private lateinit var boardAdapter : BoardAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCategory.text = viewModel.categoryName
        initAdapter()
    }

    fun initAdapter(){
        boardAdapter = BoardAdapter()
        binding.rvBoard.adapter = boardAdapter
//        viewModel.boardLiveData = service.getBoardList("food")
        //TODO : category 받아와서 넣기
        viewModel.getSavedBoard("food").observe(viewLifecycleOwner, Observer { it->
            boardAdapter.boardList = it
            boardAdapter.notifyDataSetChanged()
        })
    }

    fun setNoneData(){
        boardList.add(Board(
            0,"num1","food",System.currentTimeMillis()+(24*60*60*1000),50000,"test",System.currentTimeMillis(),"아름"
        ))
        boardList.add(Board(
            0,"num2","test",System.currentTimeMillis()+(24*60*60*2000),50000,"test",System.currentTimeMillis(),"아름"
        ))
        boardList.add(Board(
            0,"num3","food",System.currentTimeMillis(),50000,"test",System.currentTimeMillis(),"아름"
        ))
    }

    override fun onResume() {
        super.onResume()
        //새로 가져오기
    }
}