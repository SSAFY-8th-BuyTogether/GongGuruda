package com.buy.together.ui.view.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.buy.together.R
import com.buy.together.data.model.Board
import com.buy.together.databinding.FragmentBoardBinding
import com.buy.together.databinding.FragmentMainBinding
import com.buy.together.ui.adapter.BoardAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.BoardViewModel

class BoardFragment : BaseFragment<FragmentBoardBinding>(
    FragmentBoardBinding::bind, R.layout.fragment_board
){
    private val viewModel : BoardViewModel by activityViewModels()
    private val boardList = arrayListOf<Board>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCategory.text = viewModel.category
        initAdapter()
    }

    fun initAdapter(){
        setNoneData()
        binding.rvBoard.adapter = BoardAdapter(boardList)
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