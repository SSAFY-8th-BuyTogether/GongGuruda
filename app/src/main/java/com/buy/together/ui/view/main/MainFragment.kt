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

// TODO : 인터넷 연결 여부 체크 필요.
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
//        viewModel.getSavedBoard(random)
//        viewModel.boardListLiveData.observe(viewLifecycleOwner){
//            boardAdapter.boardList = it
//            boardAdapter.notifyDataSetChanged()
//        }
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
            tvAddress.setOnClickListener { showAddressFragment() }
        }
    }

    fun onclickCategory(type : String){
        viewModel.category = type
        showBoardFragment()
    }

    private fun showAddressFragment(){ findNavController().navigate(R.id.action_mainFragment_to_addressGraph) }
    private fun showBoardWritingFragment() { findNavController().navigate(R.id.action_mainFragment_to_boardWritingFragment) }
    private fun showBoardFragment() { findNavController().navigate(R.id.action_mainFragment_to_boardFragment)}
}