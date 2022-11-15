package com.buy.together.ui.view.user.findinfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.buy.together.R
import com.buy.together.data.dto.firestore.FireStoreInfo
import com.buy.together.data.dto.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentLoginFindInfoBinding
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.UserViewModel
import com.buy.together.util.RegularExpression
import com.buy.together.util.hideKeyboard

class LoginFindInfoFragment : BaseFragment<FragmentLoginFindInfoBinding>(FragmentLoginFindInfoBinding::bind, R.layout.fragment_login_find_info) {
    private val viewModel: UserViewModel by viewModels()
    private val navArgs : LoginFindInfoFragmentArgs by navArgs()
    private var type : String = FireStoreInfo.USER_ID

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObservers()
        setFocusChangeListener()

        binding.fragmentContent.setOnClickListener { hideKeyboard(it) }
        binding.btnFindInfo.setOnClickListener {
            viewModel.findInfo(getFindInfoType(), getUserName(), getUserBirth(), getUserSms(),
                getUserName2(), getUserId()).observe(viewLifecycleOwner){ response ->
                when(response){
                    is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                    is FireStoreResponse.Success -> {
                        showFindInfoResultFragment(type, response.data as String)
                        dismissLoadingDialog()
                    }
                    is FireStoreResponse.Failure -> {
                        val msg = if (type==FireStoreInfo.USER_ID) "아이디" else "비밀번호"
                        showCustomDialogBasicOneButton("$msg 찾기에 실패했습니다.\n입력 내용을 확인해주세요.")
                        dismissLoadingDialog()
                    }
                }
            }
        }

    }

    private fun initView(){
        when (navArgs.findInfoType){
            null -> requireActivity().supportFragmentManager.popBackStack()
            FireStoreInfo.USER_PWD -> setPwdView()
            else -> {}
        }
    }

    private fun setPwdView(){
        type = FireStoreInfo.USER_PWD
        binding.run {
            tvTitle.text = "비밀번호 찾기"
            btnFindInfo.text = "비밀번호 찾기"
            layoutFindId.visibility = View.GONE
            layoutFindPwd.visibility = View.VISIBLE
        }
    }

    private fun setObservers(){
        viewModel.run {
            checkUserNameLiveData.observe(viewLifecycleOwner){
                when (it){
                    true -> {
                        if (type == FireStoreInfo.USER_ID) setErrorMsg(RegularExpression.Regex.NAME, null)
                        else setErrorMsg(msg = null)
                    }
                    else -> {
                        if (type == FireStoreInfo.USER_ID) setErrorMsg(RegularExpression.Regex.NAME, it as String)
                        else setErrorMsg(msg = it as String)
                    }
                }
            }
            checkUserBirthLiveData.observe(viewLifecycleOwner){
                when(it){
                    true -> setErrorMsg(RegularExpression.Regex.BIRTH, null)
                    else -> setErrorMsg(RegularExpression.Regex.BIRTH, it as String)
                }
            }
            checkUserSmsLiveData.observe(viewLifecycleOwner){
                when(it){
                    true -> setErrorMsg(RegularExpression.Regex.PHONE, null)
                    else -> setErrorMsg(RegularExpression.Regex.PHONE, it as String)
                }
            }
            checkUserIdLiveData.observe(viewLifecycleOwner){
                when(it){
                    true -> setErrorMsg(RegularExpression.Regex.ID, null)
                    else -> setErrorMsg(RegularExpression.Regex.ID, it as String)
                }
            }
        }
    }

    private fun setFocusChangeListener(){
        binding.run {
            etUserName.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && (getUserName().isEmpty()|| getUserName().isBlank())) {
                    setErrorMsg(RegularExpression.Regex.NAME, getString(R.string.tv_error_msg_name))
                }else setErrorMsg(RegularExpression.Regex.NAME, null)
            }
            etUserBirth.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && (getUserBirth().isEmpty()|| getUserBirth().isBlank())) {
                    setErrorMsg(RegularExpression.Regex.BIRTH, getString(R.string.tv_error_msg_birth))
                }else setErrorMsg(RegularExpression.Regex.BIRTH, null)
            }
            etUserSms.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && (getUserSms().isEmpty()|| getUserSms().isBlank())) {
                    setErrorMsg(RegularExpression.Regex.PHONE, getString(R.string.tv_error_msg_sms))
                }else setErrorMsg(RegularExpression.Regex.PHONE, null)
            }
            etUserName2.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && (getUserName2().isEmpty()|| getUserName2().isBlank())) {
                    setErrorMsg(msg = getString(R.string.tv_error_msg_name))
                }else setErrorMsg(msg = null)
            }
            etUserId.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && (getUserId().isEmpty()|| getUserId().isBlank())) {
                    setErrorMsg(RegularExpression.Regex.ID, getString(R.string.tv_error_msg_id))
                }else setErrorMsg(RegularExpression.Regex.ID, null)
            }
        }
    }

    private fun setErrorMsg(type : RegularExpression.Regex?=null, msg:String?){
        binding.run {
            when(type){
                RegularExpression.Regex.NAME -> layoutEtUserName.error = msg
                RegularExpression.Regex.BIRTH -> layoutEtUserBirth.error = msg
                RegularExpression.Regex.PHONE -> layoutEtUserSms.error = msg
                RegularExpression.Regex.ID -> layoutEtUserId.error = msg
                else -> { layoutEtUserName2.error = msg }
            }
        }
    }

    private fun getFindInfoType() = type
    private fun getUserName() = binding.etUserName.text.toString().trim()
    private fun getUserBirth() = binding.etUserBirth.text.toString().trim()
    private fun getUserSms() = binding.etUserSms.text.toString().trim()
    private fun getUserName2() = binding.etUserName2.text.toString().trim()
    private fun getUserId() = binding.etUserId.text.toString().trim()


    private fun showFindInfoResultFragment(type : String, info : String) {
        val userName = if (getUserName().isNotBlank() && getUserName().isNotEmpty()) getUserName() else getUserName2()
        findNavController().navigate(LoginFindInfoFragmentDirections.actionLoginFindInfoFragmentToLoginFindInfoResultFragment(arrayOf(userName, info), type))
    }
}
