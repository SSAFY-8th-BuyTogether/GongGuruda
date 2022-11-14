package com.buy.together.ui.view.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
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
        boardAdapter = BoardAdapter()
        binding.rvMainBoard.adapter = boardAdapter
        val random = (1..4).random()
        Log.d(TAG, "initAdapter: $random")
        viewModel.getSavedBoard(random)
        viewModel.boardListLiveData.observe(viewLifecycleOwner){
            boardAdapter.boardList = it
            boardAdapter.notifyDataSetChanged()
        }
    }

    fun initListener(){
        binding.apply {
            fabWriteBoard.setOnClickListener{
                showBoardWritingFragment()
            }
            llCategoryLayout.apply{
                ibImgAll.setOnClickListener{
                    onclickCategory(0)
                }
                ibImgFood.setOnClickListener{
                    onclickCategory(1)
                }
                ibImgStationery.setOnClickListener{
                    onclickCategory(2)
                }
                ibImgDailyNeccessity.setOnClickListener{
                    onclickCategory(3)
                }
                ibImgEtc.setOnClickListener{
                    onclickCategory(4)
                }
            }
        }
    }

    fun onclickCategory(type : Int){
        viewModel.categoryIdx = type
        showBoardFragment()
    }

    private fun showBoardWritingFragment() { findNavController().navigate(R.id.action_mainFragment_to_boardWritingFragment) }
    private fun showBoardFragment() { findNavController().navigate(R.id.action_mainFragment_to_boardFragment)}
}