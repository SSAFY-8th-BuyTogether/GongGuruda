package com.buy.together.ui.view.splash

import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.getSystemService
import androidx.navigation.fragment.findNavController
import com.buy.together.Application
import com.buy.together.R
import com.buy.together.databinding.FragmentSplashBinding
import com.buy.together.ui.base.BaseFragment
import com.buy.together.util.CommonUtils.makeToast
import kotlinx.coroutines.*


private const val TAG = "SplashFragment"
class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::bind, R.layout.fragment_splash){
    private lateinit var connectionManager : ConnectivityManager

    private fun initViewStart() {
        when (context){
            null -> {
                makeToast(requireContext(), "에러가 발생했습니다.\n앱을 재부팅합니다.")
                //restartActivity()
            }
            else -> connectionManager = requireContext().getSystemService<ConnectivityManager>()!!
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewStart()
        if (Application.authToken.isNullOrEmpty()) moveToLoginFragment()
        // else findNavController().navigate(R.id.)
    }

    override fun onResume() {
        super.onResume()
        if (checkServiceState()){

        }else{
            makeToast(requireContext(), "인터넷 연결이 불안정합니다.")
            if (Application.authToken==null){
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        delay(5000L)
                        Handler(Looper.getMainLooper()).postDelayed({ moveToLoginFragment() }, 1000L)
                    }
                }
            }else{

            }
        }
    }

    private fun checkServiceState() : Boolean { return connectionManager.activeNetwork != null }
    private fun moveToLoginFragment() { findNavController().navigate(R.id.action_splashFragment_to_loginFragment) }

}

