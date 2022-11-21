package com.buy.together.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.buy.together.ui.view.mypage.MyCommentFragment
import com.buy.together.ui.view.mypage.MyWriteFragment

class MyWriteCommentViewPageAdapter (activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyWriteFragment()
            1 -> MyCommentFragment()
            else -> MyWriteFragment()
        }
    }
}