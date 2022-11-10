package com.buy.together.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.buy.together.R
import com.buy.together.databinding.FragmentSplashBinding
import com.buy.together.ui.base.BaseFragment

class SplashFragment : Fragment(){  //BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::bind, R.id.splashFragment)
    private lateinit var binding : FragmentSplashBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

}