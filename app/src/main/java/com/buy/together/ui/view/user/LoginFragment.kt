package com.buy.together.ui.view.user

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.Application.Companion.sharedPreferences
import com.buy.together.R
import com.buy.together.data.dto.FireStore
import com.buy.together.databinding.FragmentLoginBinding
import com.buy.together.restartActivity
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.LoginViewModel
import com.buy.together.util.CommonUtils.makeToast
import com.buy.together.util.CustomDialogBasicOneButton
import com.buy.together.util.hideKeyboard

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login){
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var connectionManager : ConnectivityManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (context){
            null -> {
                makeToast(requireContext(), "에러가 발생했습니다.\n앱을 재부팅합니다.")
                restartActivity()
            }else -> connectionManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        //ToDO : test 용으로 사용 해당 부분 다 빼도 됨
        binding.apply {
            fragmentRootLayout.setOnClickListener {view ->  hideKeyboard(view) }
            btnLogin.setOnClickListener {
                showMainFragment()
//                if (checkServiceState()) viewModel.sendLoginInfo(getIdInfo(), getPwdInfo())
//                else makeToast(requireContext(), "인터넷 연결이 불안정합니다.\nWifi 상태를 체킹해주세요.")
            }
            btnFindId.setOnClickListener { showFindInfoFragment(FireStore.USER_ID) }
            btnFindPwd.setOnClickListener { showFindInfoFragment(FireStore.USER_PWD) }
            btnJoin.setOnClickListener { showJoinFragment()  }
        }

        viewModel.onSuccessLogin.observe(viewLifecycleOwner){
            if (it) {
                showMainFragment()
            }
            else showCustomDialogBasicOneButton("로그인에 실패했습니다.\n아이디나 비밀번호를 확인해주세요.")
        }
    }

    private fun checkServiceState() : Boolean { return connectionManager.activeNetwork != null }
    private fun getIdInfo() : String = binding.etLoginId.text.toString().trim()
    private fun getPwdInfo() : String = binding.etLoginPwd.text.toString().trim()
    private fun saveUserInfo() {
        // TODO FCM TOKEN 로직 추가하기
        sharedPreferences.putAuthToken(getIdInfo())
    }

    private fun showMainFragment() { findNavController().navigate(R.id.action_loginFragment_to_mainFragment) }
    private fun showFindInfoFragment(findType : String) { findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToFindInfoGraph(findType)) }
    private fun showJoinFragment() { findNavController().navigate(R.id.action_loginFragment_to_joinGraph) }
}
