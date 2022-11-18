package com.buy.together.ui.view.user

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.Application.Companion.sharedPreferences
import com.buy.together.R
import com.buy.together.data.model.network.firestore.FireStoreInfo
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentLoginBinding
import com.buy.together.restartActivity
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.UserViewModel
import com.buy.together.util.CommonUtils.makeToast
import com.buy.together.util.hideKeyboard

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login){
    private val viewModel: UserViewModel by viewModels()
    private lateinit var connectionManager : ConnectivityManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (context){
            null -> {
                makeToast(requireContext(), "에러가 발생했습니다.\n앱을 재부팅합니다.")
                restartActivity()
            }else -> connectionManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        viewModel.onSuccessGettingFCMToken.observe(viewLifecycleOwner){fcmToken ->
            viewModel.logIn(getIdInfo(), getPwdInfo(), fcmToken).observe(viewLifecycleOwner){ response ->
                when(response){
                    is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                    is FireStoreResponse.Success -> {
                        saveUserInfo(fcmToken)
                        showMainFragment()
                        dismissLoadingDialog()
                    }
                    is FireStoreResponse.Failure -> {
                        showCustomDialogBasicOneButton("로그인에 실패했습니다.\n아이디 혹은 비밀번호를 확인해주세요.")
                        dismissLoadingDialog()
                    }
                }
            }
        }

        binding.apply {
            fragmentRootLayout.setOnClickListener {view ->  hideKeyboard(view) }
            btnLogin.setOnClickListener {
                checkServiceState {
                    if (getIdInfo().isBlank()) showCustomDialogBasicOneButton("아이디를 입력해주세요.")
                    else if (getPwdInfo().isBlank()) showCustomDialogBasicOneButton("비밀번호를 입력해주세요.")
                    else viewModel.getFCMToken()
                }
            }
            btnFindId.setOnClickListener {
                checkServiceState{ showFindInfoFragment(FireStoreInfo.USER_ID) }
            }
            btnFindPwd.setOnClickListener {
                checkServiceState{ showFindInfoFragment(FireStoreInfo.USER_PWD) }
            }
            btnJoin.setOnClickListener {
                checkServiceState { showJoinFragment() }
            }
        }
    }

    private fun checkServiceState(action : ()->Unit) {
        if (connectionManager.activeNetwork != null ) action()
        else makeToast(requireContext(), "인터넷 연결이 불안정합니다.\nWifi 상태를 체킹해주세요.")
    }

    private fun getIdInfo() : String = binding.etLoginId.text.toString().trim()
    private fun getPwdInfo() : String = binding.etLoginPwd.text.toString().trim()

    private fun saveUserInfo(fcmToken : String) {
        sharedPreferences.putAuthToken(getIdInfo())
        sharedPreferences.putFCMToken(fcmToken)
    }

    private fun showMainFragment() { findNavController().navigate(R.id.action_loginFragment_to_mainFragment) }
    private fun showFindInfoFragment(findType : String) { findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToFindInfoGraph(findType)) }
    private fun showJoinFragment() { findNavController().navigate(R.id.action_loginFragment_to_joinGraph) }
}
