package com.buy.together.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.buy.together.data.model.domain.UserDto
import com.buy.together.data.model.network.firestore.FireStoreInfo
import com.buy.together.ui.base.BaseViewModel
import com.buy.together.util.RegularExpression
import kotlinx.coroutines.Dispatchers

class UserViewModel : BaseViewModel() {
    private val _checkUserNameLiveData = MutableLiveData<Any>()
    val checkUserNameLiveData: LiveData<Any> get() = _checkUserNameLiveData
    private val _checkUserBirthLiveData = MutableLiveData<Any>()
    val checkUserBirthLiveData: LiveData<Any> get() = _checkUserBirthLiveData
    private val _checkUserSmsLiveData = MutableLiveData<Any>()
    val checkUserSmsLiveData: LiveData<Any> get() = _checkUserSmsLiveData
    private val _checkUserNickNameLiveData = MutableLiveData<Any>()
    val checkUserNickNameLiveData: LiveData<Any> get() = _checkUserNickNameLiveData
    private val _checkUserIdLiveData = MutableLiveData<Any>()
    val checkUserIdLiveData: LiveData<Any> get() = _checkUserIdLiveData
    private val _checkUserPwdLiveData = MutableLiveData<Any>()
    val checkUserPwdLiveData: LiveData<Any> get() = _checkUserPwdLiveData
    private val _checkUserPwdCheckLiveData = MutableLiveData<Any>()
    val checkUserPwdCheckLiveData: LiveData<Any> get() = _checkUserPwdCheckLiveData


    private val user : UserDto = UserDto()
    
    private fun checkForUserName(userName: String) : Boolean{
        return if (userName.isBlank() || userName.isEmpty()) {
            _checkUserNameLiveData.postValue("이름을 입력해주세요.")
            false
        } else if (userName.length < 2) {
            _checkUserNameLiveData.postValue("이름은 2글자 이상이어야 합니다.")
            false
        } else if (!RegularExpression.validCheck(RegularExpression.Regex.NAME, userName)) {
            _checkUserNameLiveData.postValue("제대로 된 한글형식으로 입력해주세요.")
            false
        } else {
            _checkUserNameLiveData.postValue(true)
            true}
    }

    private fun checkForUserBirth(userBirth: String) : Boolean{
        return if (userBirth.isBlank() || userBirth.isEmpty()) {
            _checkUserBirthLiveData.postValue("생년월일을 입력해주세요.")
            false
        } else if (!RegularExpression.validCheck(RegularExpression.Regex.BIRTH, userBirth)){
            _checkUserBirthLiveData.postValue("생년월일은 8자리 숫자이어야 합니다.")
            false
        } else {
            _checkUserBirthLiveData.postValue(true)
            true
        }
    }

    private fun checkForUserSms(userSmsInfo: String) : Boolean {
        return if (userSmsInfo.isBlank() || userSmsInfo.isEmpty()){
            _checkUserSmsLiveData.postValue("전화번호를 입력해주세요.")
            false
        } else if (!RegularExpression.validCheck(RegularExpression.Regex.PHONE, userSmsInfo)){
            _checkUserSmsLiveData.postValue("전화번호를 확인해주세요.")
            false
        } else{
            _checkUserSmsLiveData.postValue(true)
            true
        }
    }

    fun checkNickNameAvailable(userNickname: String) = liveData(Dispatchers.IO) {
        if (userNickname.isBlank() || userNickname.isEmpty()) {
            _checkUserNickNameLiveData.postValue("닉네임을 입력해주세요.")
        } else if (userNickname.length < 2) {
            _checkUserNickNameLiveData.postValue("닉네임은 두글자 이상이어야 합니다.")
        } else if (!RegularExpression.validCheck(RegularExpression.Regex.NICKNAME, userNickname)) {
            _checkUserNickNameLiveData.postValue("닉네임은 8글자 이내의 한글만 입력가능합니다.")
        } else userRepository.checkNickNameAvailable(userNickname).collect(){emit(it)}
    }


    private fun checkForUserId(userId: String) : Boolean {
        return if (userId.isBlank() || userId.isEmpty()) {
            _checkUserIdLiveData.postValue("아이디를 입력해주세요.")
            false
        } else if (!RegularExpression.validCheck(RegularExpression.Regex.ID, userId)) {
            _checkUserIdLiveData.postValue("아이디는 4~12자의 영문, 숫자만 가능합니다.")
            false
        } else {
            _checkUserIdLiveData.postValue(true)
            true
        }
    }

    fun checkIdAvailable(userId: String) = liveData(Dispatchers.IO) {
        if (userId.isBlank() || userId.isEmpty()) {
            _checkUserIdLiveData.postValue("아이디를 입력해주세요.")
        }else if (!RegularExpression.validCheck(RegularExpression.Regex.ID, userId)) {
            _checkUserIdLiveData.postValue("아이디는 4~12자의 영문, 숫자만 가능합니다.")
        }else userRepository.checkIdAvailable(userId).collect(){emit(it)}
    }

    private fun checkForUserPwd(userPwd: String, userPwd2: String) : Boolean {
        return if (userPwd.isBlank() || userPwd.isEmpty() ) {
            _checkUserPwdLiveData.postValue("비밀번호를 입력해주세요.")
            false
        }else if (userPwd.length < 8 || userPwd.length > 22) {
            _checkUserPwdLiveData.postValue("비밀번호는 8~22자로 입력해야 합니다.")
            false
        }else if (!RegularExpression.validCheck(RegularExpression.Regex.PWD, userPwd)) {
            _checkUserPwdLiveData.postValue("비밀번호는 영문 또는 숫자만 가능합니다.")
            false
        }else if (userPwd2.isBlank() || userPwd2.isEmpty()){
            _checkUserPwdCheckLiveData.postValue("비밀번호 확인을 입력해주세요.")
            false
        }else if (userPwd != userPwd2){
            _checkUserPwdCheckLiveData.postValue("비밀번호와 동일하지 않습니다.")
            false
        } else {
            _checkUserPwdLiveData.postValue(true)
            _checkUserPwdCheckLiveData.postValue(true)
            true
        }
    }

    fun findInfo(type : String, userName : String, userBirth : String, userSms : String,
                         userName2 : String, userId : String) = liveData(Dispatchers.IO){
        when(type) {
            FireStoreInfo.USER_ID -> {
                val checkName = checkForUserName(userName)
                val checkBirthInfo = checkForUserBirth(userBirth)
                val checkSmsInfo = checkForUserSms(userSms)
                if (checkName && checkBirthInfo && checkSmsInfo) userRepository.findId(userName, userBirth, userSms).collect(){ emit(it) }
            }
            FireStoreInfo.USER_PWD -> {
                val checkName = checkForUserName(userName2)
                val checkId = checkForUserId(userId)
                if (checkName && checkId) userRepository.findPwd(userName2, userId).collect(){ emit(it) }
            }
            else -> handleError(Throwable("알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
        }
    }

    fun checkJoinBasicInfo(userName:String, userBirth: String, userSms: String) : Boolean {
        val checkName = checkForUserName(userName)
        val checkBirthInfo = checkForUserBirth(userBirth)
        val checkSmsInfo = checkForUserSms(userSms)
        if (checkName && checkBirthInfo && checkSmsInfo) {
            user.apply {
                name = userName
                birthday = userBirth
                phone = userSms
            }
            return true
        }
        return false
    }

    fun join(userNickName:String, userId: String, userPwd: String, userPwdCheck:String) = liveData(Dispatchers.IO){
        val checkPwdInfo = checkForUserPwd(userPwd, userPwdCheck)
        if (checkPwdInfo) {
            user.apply {
                nickName = userNickName
                id = userId
                password = userPwd
            }
            userRepository.join(user).collect(){ emit(it) }
        }
    }

    fun logIn(userId : String, userPwd : String, fcmToken:String) = liveData(Dispatchers.IO){
        if (userId.isNotBlank() && userPwd.isNotBlank()) userRepository.logIn(userId, userPwd, fcmToken).collect(){ emit(it) }
    }

}