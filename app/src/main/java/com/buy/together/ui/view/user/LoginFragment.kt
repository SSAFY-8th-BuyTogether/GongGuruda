package com.buy.together.ui.view.user

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.databinding.FragmentLoginBinding
import com.buy.together.ui.base.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            btnJoin.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_joinGraph)
            }
            btnLogin.setOnClickListener {

            }
        }
    }

    //fun getIdInfo() : String = binding.etLoginId.text.toString().trim()
    //fun getPwdInfo() : String = binding.etLoginPwd.text.toString().trim()
}