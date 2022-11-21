package com.buy.together.ui.view.mypage

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.buy.together.R
import com.buy.together.data.model.domain.UserDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentMyInfoModifyBinding
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.MyPageViewModel
import com.buy.together.util.GalleryUtils
import com.buy.together.util.RegularExpression

class MyInfoModifyFragment : BaseFragment<FragmentMyInfoModifyBinding>(FragmentMyInfoModifyBinding::bind, R.layout.fragment_my_info_modify) {
    private val viewModel : MyPageViewModel by viewModels()
    private var isNickNameChecked : Boolean = false
    private lateinit var userDto : UserDto
    private var profileImage : Uri = Uri.parse(GalleryUtils.baseProfile)

    private val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                profileImage = imageUri
                Glide.with(requireActivity())
                    .load(imageUri)
                    .into(binding.imgUserProfile)
            }else{
                Toast.makeText(requireContext(), "이미지를 가져오는데 실패했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setFocusChangeListener()
        setTextChangeListener()
        initData()
        binding.apply {
            btnBack.setOnClickListener{ showPopFragment() }
            imgUserProfile.setOnClickListener{
                GalleryUtils.getGallery(requireContext(), imageLauncher)
            }
            btnNickNameCheck.setOnClickListener {
                if (userDto.nickName==getUserNickName()){
                    setIsNickNameChecked(true)
                    setNickNameCheckedView(true, "유저님의 현재 닉네임입니다.")
                    setErrorMsg(RegularExpression.Regex.NICKNAME, null)
                }else {
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
            }
            btnSubmit.setOnClickListener{
                if (getIsNickNameChecked()&& viewModel.checkJoinBasicInfo(getUserName(), getUserBirth(), getUserSms())){
                    showLoadingDialog(requireContext())
                    GalleryUtils.changeProfileImg(viewModel.authToken, profileImage).observe(viewLifecycleOwner){ img->
                        if(img == null){ return@observe }
                        else{
                            userDto.apply {
                                nickName = getUserNickName()
                                name = getUserName()
                                birthday = getUserBirth()
                                phone = getUserSms()
                                profile = img
                            }
                            viewModel.modify(userDto).observe(viewLifecycleOwner){ response ->
                                when(response){
                                    is FireStoreResponse.Loading -> {  }
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
            }
        }
    }

    private fun initData(){
        binding.apply {
            viewModel.getUserInfo().observe(viewLifecycleOwner){
                it?.let { userDto ->
                    this@MyInfoModifyFragment.userDto = userDto
                    userDto.makeProfileSrc(requireContext())?.let { src ->
                        Glide.with(imgUserProfile)
                            .load(src)
                            .into(imgUserProfile)
                    }
                    etUserNickName.setText(userDto.nickName)
                    etUserName.setText(userDto.name)
                    etUserBirth.setText(userDto.birthday)
                    etUserSms.setText(userDto.phone)
                    isNickNameChecked = true
                }
            }
        }
    }

    private fun setObservers(){
        viewModel.run {
            checkUserNickNameLiveData.observe(viewLifecycleOwner){
                setErrorMsg(RegularExpression.Regex.NICKNAME, it as String)
            }
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
            etUserNickName.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && (getUserNickName().isEmpty()|| getUserNickName().isBlank())) {
                    setErrorMsg(RegularExpression.Regex.NICKNAME, getString(R.string.tv_error_msg_nickname))
                }else setErrorMsg(RegularExpression.Regex.NICKNAME, null)
            }
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

    private fun setTextChangeListener(){
        binding.etUserNickName.addTextChangedListener {
            setIsNickNameChecked(false)
            setNickNameCheckedView(false, null)
        }
    }

    private fun setErrorMsg(type : RegularExpression.Regex?=null, msg:String?){
        binding.run {
            when(type){
                RegularExpression.Regex.NICKNAME -> layoutEtUserNickName.error = msg
                RegularExpression.Regex.NAME -> layoutEtUserName.error = msg
                RegularExpression.Regex.BIRTH -> layoutEtUserBirth.error = msg
                RegularExpression.Regex.PHONE -> layoutEtUserSms.error = msg
                else -> {}
            }
        }
    }

    private fun setHelperMsg(msg:String?){
        binding.layoutEtUserNickName.helperText = msg
    }

    private fun setIsNickNameChecked(result : Boolean){ isNickNameChecked = result }

    private fun setNickNameCheckedView(result : Boolean, msg: String?){
        if (result){
            binding.btnNickNameCheck.setTextColor(R.color.black_10)
            setHelperMsg(msg)
        }else{
            binding.btnNickNameCheck.setTextColor(R.color.mainColor)
            setHelperMsg(msg)
        }
    }

    private fun getIsNickNameChecked() : Boolean{
        return if (isNickNameChecked) true
        else { setErrorMsg(RegularExpression.Regex.NICKNAME, "닉네임 중복확인을 해주세요.")
            false
        }
    }

    private fun getUserNickName() = binding.etUserNickName.text.toString().trim()
    private fun getUserName() = binding.etUserName.text.toString().trim()
    private fun getUserBirth() = binding.etUserBirth.text.toString().trim()
    private fun getUserSms() = binding.etUserSms.text.toString().trim()

    private fun showPopFragment() { findNavController().navigate(R.id.action_myInfoModifyFragment_pop) }
}