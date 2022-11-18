package com.buy.together.ui.view.user.join

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.buy.together.R
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentJoinUserInfoBinding
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.UserViewModel
import com.buy.together.util.RegularExpression
import com.buy.together.util.hideKeyboard

class JoinUserInfoFragment : BaseFragment<FragmentJoinUserInfoBinding>(FragmentJoinUserInfoBinding::bind, R.layout.fragment_join_user_info) {
    private val viewModel: UserViewModel by navGraphViewModels(R.id.joinGraph)
    private var isNickNameChecked : Boolean = false
    private var isIdChecked : Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setFocusChangeListener()
        setTextChangeListener()

        binding.fragmentContent.setOnClickListener { hideKeyboard(it) }
        binding.apply {
            btnBack.setOnClickListener { showPopFragment() }
            btnNickNameCheck.setOnClickListener {
                viewModel.checkNickNameAvailable(getUserNickName()).observe(viewLifecycleOwner){ response ->
                    when(response){
                        is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                        is FireStoreResponse.Success -> {
                            setErrorMsg(RegularExpression.Regex.NICKNAME, "이미 사용중인 닉네임입니다.")
                            dismissLoadingDialog()
                        }
                        is FireStoreResponse.Failure -> {
                            setIsNickNameChecked(true)
                            setNickNameCheckedView(true, response.errorMessage)
                            setErrorMsg(RegularExpression.Regex.NICKNAME, null)
                            dismissLoadingDialog()
                        }
                    }
                }
            }
            btnIdCheck.setOnClickListener {
                viewModel.checkIdAvailable(getUserId()).observe(viewLifecycleOwner){ response ->
                    when(response){
                        is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                        is FireStoreResponse.Success -> {
                            setErrorMsg(RegularExpression.Regex.ID, "이미 사용중인 아이디입니다.")
                            dismissLoadingDialog()
                        }
                        is FireStoreResponse.Failure -> {
                            setIsIdChecked(true)
                            seIdCheckedView(true, response.errorMessage)
                            setErrorMsg(RegularExpression.Regex.ID, null)
                            dismissLoadingDialog()
                        }
                    }
                }
            }
            btnSubmit.setOnClickListener {
                if (getIsNickNameChecked()&&getIsIdChecked()){
                    viewModel.join(getUserNickName(), getUserId(), getUserPwd(), getUserPwdCheck())
                        .observe(viewLifecycleOwner){ response ->
                            when(response){
                                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                                is FireStoreResponse.Success -> {
                                    showJoinResultFragment()
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
    }
    private fun setObservers(){
        viewModel.run {
            checkUserNickNameLiveData.observe(viewLifecycleOwner){
                setErrorMsg(RegularExpression.Regex.NICKNAME, it as String)
            }
            checkUserIdLiveData.observe(viewLifecycleOwner){
                setErrorMsg(RegularExpression.Regex.ID, it as String)
            }
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
            etUserNickName.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && (getUserNickName().isEmpty()|| getUserNickName().isBlank())) {
                    setErrorMsg(RegularExpression.Regex.NICKNAME, getString(R.string.tv_error_msg_nickname))
                }else setErrorMsg(RegularExpression.Regex.NICKNAME, null)
            }
            etUserId.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && (getUserId().isEmpty()|| getUserId().isBlank())) {
                    setErrorMsg(RegularExpression.Regex.ID, getString(R.string.tv_error_msg_id))
                }else setErrorMsg(RegularExpression.Regex.ID, null)
            }
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
            etUserNickName.addTextChangedListener {
                setIsNickNameChecked(false)
                setNickNameCheckedView(false, null)
            }
            etUserId.addTextChangedListener {
                setIsIdChecked(false)
                seIdCheckedView(false, null)
            }
            etUserPwd.addTextChangedListener {
                if (it.toString().trim() != getUserPwdCheck()) setErrorMsg(msg = "비밀번호와 동일하지 않습니다.")
                else setErrorMsg(msg = null)
            }
            etUserPwdCheck.addTextChangedListener {
                if (it.toString().trim() != getUserPwd()) setErrorMsg(msg = "비밀번호와 동일하지 않습니다.")
                else {
                    Log.d("체크", "setTextChangeListener: 체크 왔나요")
                    setErrorMsg(msg = null)
                }
            }
        }
    }

    private fun setErrorMsg(type : RegularExpression.Regex?=null, msg:String?){
        binding.run {
            when(type){
                RegularExpression.Regex.NICKNAME -> layoutEtUserNickName.error = msg
                RegularExpression.Regex.ID -> layoutEtUserId.error = msg
                RegularExpression.Regex.PWD -> layoutEtUserPwd.error = msg
                else -> {layoutEtUserPwdCheck.error = msg}
            }
        }
    }

    private fun setHelperMsg(type : RegularExpression.Regex, msg:String?){
        binding.run {
            when(type){
                RegularExpression.Regex.NICKNAME -> layoutEtUserNickName.helperText = msg
                else -> { layoutEtUserId.helperText = msg }
            }
        }
    }

    private fun setIsNickNameChecked(result : Boolean){ isNickNameChecked = result }
    private fun setIsIdChecked(result : Boolean){ isIdChecked = result }
    private fun setNickNameCheckedView(result : Boolean, msg: String?){
        if (result){
            binding.btnNickNameCheck.setTextColor(R.color.black_10)
            setHelperMsg(RegularExpression.Regex.NICKNAME, msg)
        }else{
            binding.btnNickNameCheck.setTextColor(R.color.mainColor)
            setHelperMsg(RegularExpression.Regex.NICKNAME, msg)
        }
    }
    private fun seIdCheckedView(result : Boolean, msg: String?){
        if (result){
            binding.btnIdCheck.setTextColor(R.color.black_10)
            setHelperMsg(RegularExpression.Regex.ID, msg)
        }else{
            binding.btnIdCheck.setTextColor(R.color.mainColor)
            setHelperMsg(RegularExpression.Regex.ID, msg)
        }
    }
    private fun getIsNickNameChecked() : Boolean{
        return if (isNickNameChecked) true
        else { setErrorMsg(RegularExpression.Regex.NICKNAME, "닉네임 중복확인을 해주세요.")
            false
        }
    }
    private fun getIsIdChecked() : Boolean{
        return if (isIdChecked) true
        else{ setErrorMsg(RegularExpression.Regex.ID, "아이디 중복확인을 해주세요.")
            false
        }
    }
    private fun getUserNickName() = binding.etUserNickName.text.toString().trim()
    private fun getUserId() = binding.etUserId.text.toString().trim()
    private fun getUserPwd() = binding.etUserPwd.text.toString().trim()
    private fun getUserPwdCheck() = binding.etUserPwdCheck.text.toString().trim()

    private fun showPopFragment() { findNavController().navigate(R.id.action_joinUserInfoFragment_pop) }
    private fun showJoinResultFragment() { findNavController().navigate(R.id.action_joinUserInfoFragment_to_joinResultFragment) }

}