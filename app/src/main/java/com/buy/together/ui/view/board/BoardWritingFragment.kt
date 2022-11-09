package com.buy.together.ui.view.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buy.together.R
import com.buy.together.databinding.FragmentBoardBinding
import com.buy.together.databinding.FragmentBoardWritingBinding
import com.buy.together.ui.base.BaseFragment

class BoardWritingFragment : BaseFragment<FragmentBoardWritingBinding>(
    FragmentBoardWritingBinding::bind, R.layout.fragment_board_writing
){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}