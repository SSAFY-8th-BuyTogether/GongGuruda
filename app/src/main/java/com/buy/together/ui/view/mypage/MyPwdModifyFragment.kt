package com.buy.together.ui.view.mypage

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentMyPwdModifyBinding
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.MyPageViewModel
import com.buy.together.util.RegularExpression


class MyPwdModifyFragment : BaseFragment<FragmentMyPwdModifyBinding>(FragmentMyPwdModifyBinding::bind, R.layout.fragment_my_pwd_modify) {
    private val viewModel: MyPageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        binding.apply {
            btnBack.setOnClickListener{ showPopFragment() }
            btnSubmit.setOnClickListener {
                viewModel.modifyPwd(getUserPwd(), getUserPwdCheck()).observe(viewLifecycleOwner){ response ->
                    when(response){
                        is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                        is FireStoreResponse.Success -> {
                            showPopFragment()
                            dismissLoadingDialog()
                        }
                        is FireStoreResponse.Failure -> {
                            showCustomDialogBasicOneButton(response.errorMessage)
                            dismissLoadingDialog()
                        }
                    }
                }
            }
        }
    }

    private fun init(){
        binding.apply {
            viewModel.getUserInfo().observe(viewLifecycleOwner){
                it?.let { userDto ->
                    etUserPwd.setText(userDto.password)
                    etUserPwdCheck.setText(userDto.password)
                    setObservers()
                    setFocusChangeListener()
                    setTextChangeListener()
                }
            }
        }
    }

    private fun setObservers(){
        viewModel.run {
            checkUserPwdLiveData.observe(viewLifecycleOwner){
                when(it){
                    true -> setErrorMsg(RegularExpression.Regex.PWD, null)
                    else -> setErrorMsg(RegularExpression.Regex.PWD, it as String)
                }
            }
            checkUserPwdCheckLiveData.observe(viewLifecycleOwner){
                when(it){
                    true -> setErrorMsg(msg = null)
                    else -> setErrorMsg(msg = it as String)
                }
            }
        }
    }

    private fun setFocusChangeListener(){
        binding.run {

            etUserPwd.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && (getUserPwd().isEmpty()|| getUserPwd().isBlank())) {
                    setErrorMsg(RegularExpression.Regex.PWD, getString(R.string.tv_error_msg_pwd))
                }else setErrorMsg(RegularExpression.Regex.PWD, null)
            }
            etUserPwdCheck.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && (getUserPwd().isEmpty()|| getUserPwd().isBlank())) {
                    setErrorMsg(msg = getString(R.string.tv_error_msg_pwd_check_1))
                } else if (hasFocus && (getUserPwdCheck().isEmpty()|| getUserPwdCheck().isBlank())) {
                    setErrorMsg(msg = getString(R.string.tv_error_msg_pwd_check_2))
                }else if (hasFocus && getUserPwd()!=getUserPwdCheck()) { setErrorMsg(msg = "비밀번호와 동일하지 않습니다.") }
                else setErrorMsg(msg = null)
            }
        }
    }

    private fun setTextChangeListener(){
        binding.run {
            etUserPwd.addTextChangedListener {
                if (it.toString().trim() != getUserPwdCheck()) setErrorMsg(msg = "비밀번호와 동일하지 않습니다.")
                else setErrorMsg(msg = null)
            }
            etUserPwdCheck.addTextChangedListener {
                if (it.toString().trim() != getUserPwd()) setErrorMsg(msg = "비밀번호와 동일하지 않습니다.")
                else setErrorMsg(msg = null)
            }
        }
    }

    private fun setErrorMsg(type : RegularExpression.Regex?=null, msg:String?){
        binding.run {
            when(type){
                RegularExpression.Regex.PWD -> layoutEtUserPwd.error = msg
                else -> {layoutEtUserPwdCheck.error = msg}
            }
        }
    }

    private fun getUserPwd() = binding.etUserPwd.text.toString().trim()
    private fun getUserPwdCheck() = binding.etUserPwdCheck.text.toString().trim()

    private fun showPopFragment() { findNavController().navigate(R.id.action_myPwdModifyFragment_pop) }
}