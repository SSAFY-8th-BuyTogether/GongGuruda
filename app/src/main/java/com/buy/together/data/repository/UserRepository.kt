package com.buy.together.data.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.buy.together.AppDatabase
import com.buy.together.data.dto.UserDto
import com.buy.together.data.dto.firestore.FireStoreInfo
import com.buy.together.data.dto.firestore.FireStoreResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

private const val DATABASE_NAME = "gong_gu_rumi.db"
class UserRepository private constructor(context: Context){

    private val database : AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        DATABASE_NAME
    ).build()

    companion object{
        private var INSTANCE : UserRepository? =null

        fun initialize(context: Context){
            if(INSTANCE == null) INSTANCE = UserRepository(context)
        }

        fun get() : UserRepository {
            return INSTANCE ?: throw IllegalStateException("NoteRepository must be initialized")
        }

        fun getInstance(context: Context) : UserRepository {
            return INSTANCE ?: synchronized(this){
                val instance = UserRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }

    private val userDao = database.userDao()
    private val fireStore = FirebaseFirestore.getInstance()

//    fun findUserId(name: String, birth : String, sms : String) = flow {
//        val query = fireStore.collection(FireStoreInfo.USER).whereEqualTo(FireStoreInfo.USER_NAME, name)
//            .whereEqualTo(FireStoreInfo.USER_BIRTH, birth).whereEqualTo(FireStoreInfo.USER_PHONE, sms)
//        emit(FireStoreResponse.Loading())
//        emit(
//            FireStoreResponse.Success(query.get().await().documents.mapNotNull { doc ->
//                    doc.toObject(UserDto::class.java)
//                })
//        )
//    }. catch { error ->
//        error.message?.let { errorMessage ->
//            emit(FireStoreResponse.Failure(errorMessage))
//        }
//    }

    fun findId(name: String, birth : String, sms : String) = flow {
        val query = fireStore.collection(FireStoreInfo.USER).whereEqualTo(FireStoreInfo.USER_NAME, name)
            .whereEqualTo(FireStoreInfo.USER_BIRTH, birth).whereEqualTo(FireStoreInfo.USER_PHONE, sms)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.get().await().documents[0][FireStoreInfo.USER_ID]))
    }. catch { error ->
        error.message?.let { errorMessage ->
            emit(FireStoreResponse.Failure(errorMessage))
        }
    }


    fun findPwd(name: String, id : String) = flow {
        val query = fireStore.collection(FireStoreInfo.USER)
            .whereEqualTo(FireStoreInfo.USER_NAME, name).whereEqualTo(FireStoreInfo.USER_ID, id)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.get().await().documents[0][FireStoreInfo.USER_PWD]))
    }. catch { error ->
        error.message?.let { errorMessage ->
            emit(FireStoreResponse.Failure(errorMessage))
        }
    }

    fun checkNickNameAvailable(nickname: String) = flow {
        val query = fireStore.collection(FireStoreInfo.USER)
            .whereEqualTo(FireStoreInfo.USER_NICKNAME, nickname)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.get().await().documents[0]))
    }.catch {
        emit(FireStoreResponse.Failure("사용할 수 있는 닉네임입니다."))
    }

    fun checkIdAvailable(id: String) = flow {
        val query = fireStore.collection(FireStoreInfo.USER)
            .whereEqualTo(FireStoreInfo.USER_ID, id)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.get().await().documents[0]))
    }.catch {
        emit(FireStoreResponse.Failure("사용할 수 있는 아이디입니다."))
    }


    fun join(user:UserDto) = flow{
        val query = fireStore.collection(FireStoreInfo.USER).document(user.id)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.set(user).await()))
    }.catch {
        emit(FireStoreResponse.Failure("회원가입 도중 에러가 발생했습니다.\n잠시 후 다시 시도해주세요."))
    }

    fun logIn(userId : String, userPwd : String, fcmToken : String) = flow {
        val loginQuery = fireStore.collection(FireStoreInfo.USER)
            .whereEqualTo(FireStoreInfo.USER_ID, userId).whereEqualTo(FireStoreInfo.USER_PWD, userPwd)
        emit(FireStoreResponse.Loading())
        val userInfo = loginQuery.get().await().documents[0]
        val fcmTokenList : ArrayList<String> = userInfo.data?.get(FireStoreInfo.USER_FCM_TOKENS) as ArrayList<String>
        fcmTokenList.add(fcmToken)
        val saveFCMQuery = fireStore.collection(FireStoreInfo.USER).document(userId).update(FireStoreInfo.USER_FCM_TOKENS, fcmTokenList)
        emit(FireStoreResponse.Success(saveFCMQuery.await()))
    }. catch { error ->
        error.message?.let { errorMessage ->
            emit(FireStoreResponse.Failure(errorMessage))
        }
    }


}