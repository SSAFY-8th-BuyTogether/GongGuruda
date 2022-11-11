package com.buy.together.ui.view.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.buy.together.R
import com.buy.together.databinding.FragmentMainBinding
import com.buy.together.ui.adapter.BoardAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.BoardViewModel

class MainFragment : BaseFragment<FragmentMainBinding>(
    FragmentMainBinding::bind, R.layout.fragment_main
) {
    private val viewModel : BoardViewModel by activityViewModels()
    private lateinit var boardAdapter : BoardAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    fun initAdapter(){
//        service.getBoard("food","1")
        boardAdapter = BoardAdapter()
        binding.rvMainBoard.adapter = boardAdapter
//        viewModel.boardLiveData = service.getBoardList("food")
        viewModel.getSavedBoard("food").observe(viewLifecycleOwner, Observer { it->
            boardAdapter.boardList = it
            boardAdapter.notifyDataSetChanged()
        })
    }

//    fun setNoneData(){
//        boardList.add(Board(
//            0,"num1","food",System.currentTimeMillis()+(24*60*60*1000),50000,"test",System.currentTimeMillis()-(1000*60),"아름"
//        ))
//        boardList.add(Board(
//            0,"num2","test",System.currentTimeMillis()+(24*60*60*2000),50000,"test",System.currentTimeMillis()-(1000*60*3),"아름"
//        ))
//        boardList.add(Board(
//            0,"num3","food",System.currentTimeMillis(),50000,"test",System.currentTimeMillis()-(1000*60*60+60*1000),"아름"
//        ))
//    }

    override fun onResume() {
        super.onResume()
        //새로 가져오기
    }
}