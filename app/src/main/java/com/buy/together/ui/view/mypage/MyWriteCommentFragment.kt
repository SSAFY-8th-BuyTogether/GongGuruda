package com.buy.together.ui.view.mypage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.databinding.FragmentMyWriteCommentBinding
import com.buy.together.ui.adapter.MyWriteCommentViewPageAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.MyPageViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MyWriteCommentFragment : BaseFragment<FragmentMyWriteCommentBinding>(FragmentMyWriteCommentBinding::bind, R.layout.fragment_my_write_comment) {
    private lateinit var viewPagerAdapter : MyWriteCommentViewPageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        binding.apply {
            btnBack.setOnClickListener{ showPopFragment() }
        }
    }

    private fun initViewPager() {
        binding.run {
            viewPagerAdapter  = MyWriteCommentViewPageAdapter(requireActivity())
            vpMyWriteComment.adapter = viewPagerAdapter
            tlMyWriteComment.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) { vpMyWriteComment.setCurrentItem(tab!!.position, false) }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) { vpMyWriteComment.setCurrentItem(tab!!.position, false) } })
            TabLayoutMediator(tlMyWriteComment, vpMyWriteComment){ tab, position ->
                val tabTextList = arrayListOf("내가 쓴 글", "댓글")
                tab.text = tabTextList[position] }.attach() }
    }
    private fun showPopFragment() { findNavController().navigate(R.id.action_myWriteCommentFragment_pop) }
    private fun showBoardFragment() { findNavController().navigate(R.id.action_myWriteCommentFragment_to_boardFragment) }
}