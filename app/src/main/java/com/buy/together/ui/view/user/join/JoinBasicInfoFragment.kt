package com.buy.together.ui.view.user.join

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.buy.together.R
import com.buy.together.data.dto.firestore.FireStoreInfo
import com.buy.together.databinding.FragmentJoinBasicInfoBinding
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.UserViewModel
import com.buy.together.util.CommonUtils.makeToast
import com.buy.together.util.RegularExpression


class JoinBasicInfoFragment : BaseFragment<FragmentJoinBasicInfoBinding>(FragmentJoinBasicInfoBinding::bind, R.layout.fragment_join_basic_info) {
    private val viewModel: UserViewModel by navGraphViewModels(R.id.joinGraph)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setFocusChangeListener()
        binding.apply {
            btnBack.setOnClickListener { showPopFragment() }
            btnNext.setOnClickListener {
                if (viewModel.checkJoinBasicInfo(getUserName(), getUserBirth(), getUserSms())) showJoinUserInfoFragment()
            }
        }
    }

    private fun setObservers(){
        viewModel.run {
            checkUserNameLiveData.observe(viewLifecycleOwner){
                when(it){
                    true -> setErrorMsg(RegularExpression.Regex.NAME, null)
                    else -> setErrorMsg(RegularExpression.Regex.NAME, it as String)
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
        }
    }

    private fun setErrorMsg(type : RegularExpression.Regex?=null, msg:String?){
        binding.run {
            when(type){
                RegularExpression.Regex.NAME -> layoutEtUserName.error = msg
                RegularExpression.Regex.BIRTH -> layoutEtUserBirth.error = msg
                RegularExpression.Regex.PHONE -> layoutEtUserSms.error = msg
                else -> {}
            }
        }
    }

    private fun getUserName() = binding.etUserName.text.toString().trim()
    private fun getUserBirth() = binding.etUserBirth.text.toString().trim()
    private fun getUserSms() = binding.etUserSms.text.toString().trim()

    private fun showPopFragment() { findNavController().navigate(R.id.action_joinBasicInfoFragment_pop) }
    private fun showJoinUserInfoFragment(){ findNavController().navigate(R.id.action_joinBasicInfoFragment_to_joinUserInfoFragment) }

}