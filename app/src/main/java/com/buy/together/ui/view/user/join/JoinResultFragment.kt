package com.buy.together.ui.view.user.join

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.databinding.FragmentJoinResultBinding
import com.buy.together.ui.base.BaseFragment

class JoinResultFragment : BaseFragment<FragmentJoinResultBinding>(FragmentJoinResultBinding::bind, R.layout.fragment_join_result) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnMain.setOnClickListener { showPopFragment() }
    }

    private fun showPopFragment() { findNavController().navigate(R.id.action_joinResultFragment_pop) }
}