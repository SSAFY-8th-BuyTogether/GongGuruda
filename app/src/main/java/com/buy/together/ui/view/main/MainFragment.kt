package com.buy.together.ui.view.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.buy.together.R
import com.buy.together.data.model.Board
import com.buy.together.data.repository.BoardService
import com.buy.together.databinding.FragmentMainBinding
import com.buy.together.ui.adapter.BoardAdapter
import com.buy.together.ui.base.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding>(
    FragmentMainBinding::bind, R.layout.fragment_main
) {
    val boardList = arrayListOf<Board>()
    val service = BoardService()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    fun initAdapter(){
        setNoneData()
//        service.getBoard("food","1")
        service.getBoardList("food")
        binding.rvMainBoard.adapter = BoardAdapter(boardList)
    }

    fun setNoneData(){
        boardList.add(Board(
            0,"num1","food",System.currentTimeMillis()+(24*60*60*1000),50000,"test",System.currentTimeMillis()-(1000*60),"아름"
        ))
        boardList.add(Board(
            0,"num2","test",System.currentTimeMillis()+(24*60*60*2000),50000,"test",System.currentTimeMillis()-(1000*60*3),"아름"
        ))
        boardList.add(Board(
            0,"num3","food",System.currentTimeMillis(),50000,"test",System.currentTimeMillis()-(1000*60*60+60*1000),"아름"
        ))
    }

    override fun onResume() {
        super.onResume()
        //새로 가져오기
    }
}