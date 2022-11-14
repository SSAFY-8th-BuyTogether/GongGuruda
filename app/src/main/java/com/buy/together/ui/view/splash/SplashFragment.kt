package com.buy.together.ui.view.splash

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.databinding.FragmentSplashBinding
import com.buy.together.restartActivity
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.SplashViewModel
import com.buy.together.util.CommonUtils.makeToast
import com.buy.together.util.EventObserver
import kotlinx.coroutines.*


class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::bind, R.layout.fragment_splash){
    private val viewModel: SplashViewModel by viewModels()
    private lateinit var connectionManager : ConnectivityManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (context){
            null -> {
                makeToast(requireContext(), "에러가 발생했습니다.\n앱을 재부팅합니다.")
                restartActivity()
            }else -> connectionManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        initViewStart()
    }

    override fun onResume() {
        super.onResume()
        if (checkServiceState()){
            if (viewModel.isTokenAvailable) viewModel.getUserStatus()
            else Handler(Looper.getMainLooper()).postDelayed({ showLoginFragment() }, 1000L)
        }else{
            makeToast(requireContext(), "인터넷 연결이 불안정합니다.\n" + "Wifi 상태를 체킹해주세요.")
            if (viewModel.isTokenAvailable) viewModel.getUserStatus()
            else {
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO){
                            delay(5000L)
                            Handler(Looper.getMainLooper()).postDelayed({ showLoginFragment() }, 1000L)
                        }
                    }
            }
        }
    }

    private fun initViewStart() {
        viewModel.error.observe(viewLifecycleOwner, EventObserver { message ->
            makeToast(requireContext(), message)
        })
        viewModel.onSuccessGettingToken.observe(viewLifecycleOwner) {
            when (it) {
                true -> showMainFragment()
                false -> notUserEvent()
                else -> throw IllegalArgumentException("MemberStatus Error")
            }
        }
    }

    private fun checkServiceState() : Boolean { return connectionManager.activeNetwork != null }
    private fun showLoginFragment() { findNavController().navigate(R.id.action_splashFragment_to_loginFragment) }
    private fun showMainFragment() { findNavController().navigate(R.id.action_splashFragment_to_mainFragment) }

    private fun notUserEvent() {  // App 내 Token 은 유효하지만 서버 Token 이 유효하지 않을 경우
        try { } catch (e: Exception) {
            findNavController().navigate(R.id.action_global_introFragment)
        } finally {
            showLoginFragment()
        }
    }

}

