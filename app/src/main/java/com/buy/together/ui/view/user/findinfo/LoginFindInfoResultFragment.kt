package com.buy.together.ui.view.user.findinfo

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.buy.together.R
import com.buy.together.data.dto.firestore.FireStoreInfo
import com.buy.together.databinding.FragmentLoginFindInfoResultBinding
import com.buy.together.ui.base.BaseFragment

class LoginFindInfoResultFragment : BaseFragment<FragmentLoginFindInfoResultBinding>(FragmentLoginFindInfoResultBinding::bind, R.layout.fragment_login_find_info_result) {
    private val navArgs : LoginFindInfoResultFragmentArgs by navArgs()
    private lateinit var findInfoType : String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        binding.apply {
            btnFindInfo.setOnClickListener { showFindInfoFragment(findInfoType) }
            btnLogin.setOnClickListener { showLoginFragment() }
        }
    }

    private fun initView(){
        val findInfoResult = navArgs.findInfoResult
        when (navArgs.findInfoType){
            null -> requireActivity().supportFragmentManager.popBackStack()
            FireStoreInfo.USER_PWD -> setView("비밀번호", findInfoResult[0], findInfoResult[1], "아이디 찾기")
            else -> setView("아이디", findInfoResult[0], findInfoResult[1], "비밀번호 찾기")
        }
        findInfoType = navArgs.findInfoType!!
    }

    private fun setView(resultType:String, userName : String, resultInfo:String, btnText:String){
        binding.run {
            tvFindInfoUserName.text = userName
            tvFindInfoType.text = resultType
            tvFindInfoResult.text = resultInfo
            btnFindInfo.text = btnText
        }
    }

    private fun showFindInfoFragment(type : String) {
        val findInfoType = if (type==FireStoreInfo.USER_ID) FireStoreInfo.USER_PWD else FireStoreInfo.USER_ID
        findNavController().navigate(LoginFindInfoResultFragmentDirections.actionLoginFindInfoResultFragmentToLoginFindInfoFragment(findInfoType))
    }
    private fun showLoginFragment(){ findNavController().navigate(R.id.action_loginFindInfoResultFragment_pop) }

}