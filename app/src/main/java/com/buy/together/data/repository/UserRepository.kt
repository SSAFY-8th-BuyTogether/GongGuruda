package com.buy.together.data.repository

import android.content.Context
import androidx.room.Room
import com.buy.together.data.AppDatabase
import com.buy.together.data.model.network.MyComment
import com.buy.together.data.model.network.MyWrite
import com.buy.together.data.model.network.User
import com.buy.together.data.model.network.firestore.FireStoreInfo
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.data.model.network.firestore.observeCollection
import com.buy.together.data.model.network.firestore.observeDoc
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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


    fun findId(name: String, birth : String, sms : String) = flow {
        val query = fireStore.collection(FireStoreInfo.USER).whereEqualTo(FireStoreInfo.USER_NAME, name)
            .whereEqualTo(FireStoreInfo.USER_BIRTH, birth).whereEqualTo(FireStoreInfo.USER_PHONE, sms).get()
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.await().documents[0][FireStoreInfo.USER_ID]))
    }. catch { error ->
        error.message?.let { errorMessage ->
            emit(FireStoreResponse.Failure(errorMessage))
        }
    }


    fun findPwd(name: String, id : String) = flow {
        val query = fireStore.collection(FireStoreInfo.USER)
            .whereEqualTo(FireStoreInfo.USER_NAME, name).whereEqualTo(FireStoreInfo.USER_ID, id).get()
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.await().documents[0][FireStoreInfo.USER_PWD]))
    }. catch { error ->
        error.message?.let { errorMessage ->
            emit(FireStoreResponse.Failure(errorMessage))
        }
    }

    fun checkNickNameAvailable(nickname: String) = flow {
        val query = fireStore.collection(FireStoreInfo.USER)
            .whereEqualTo(FireStoreInfo.USER_NICKNAME, nickname).get()
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.await().documents[0]))
    }.catch {
        emit(FireStoreResponse.Failure("사용할 수 있는 닉네임입니다."))
    }

    fun checkIdAvailable(id: String) = flow {
        val query = fireStore.collection(FireStoreInfo.USER)
            .whereEqualTo(FireStoreInfo.USER_ID, id).get()
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.await().documents[0]))
    }.catch {
        emit(FireStoreResponse.Failure("사용할 수 있는 아이디입니다."))
    }

    fun modifyPwd(userId: String, userPwd: String) = flow {
        val query = fireStore.collection(FireStoreInfo.USER).document(userId).update(mapOf(FireStoreInfo.USER_PWD to userPwd))
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.await()))
    }.catch {
        emit(FireStoreResponse.Failure("비밀번호 변경 도중 에러가 발생했습니다.\n잠시 후 다시 시도해주세요."))
    }

    fun modify(user: User) = flow{
        val query = fireStore.collection(FireStoreInfo.USER).document(user.id).set(user)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.await()))
    }.catch {
        emit(FireStoreResponse.Failure("프로필 수정 도중 에러가 발생했습니다.\n잠시 후 다시 시도해주세요."))
    }

    fun join(user: User) = flow{
        val query = fireStore.collection(FireStoreInfo.USER).document(user.id).set(user)
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.await()))
    }.catch {
        emit(FireStoreResponse.Failure("회원가입 도중 에러가 발생했습니다.\n잠시 후 다시 시도해주세요."))
    }

    fun logIn(userId : String, userPwd : String, fcmToken : String) = flow {
        emit(FireStoreResponse.Loading())
        val loginQuery = fireStore.collection(FireStoreInfo.USER)
            .whereEqualTo(FireStoreInfo.USER_ID, userId).whereEqualTo(FireStoreInfo.USER_PWD, userPwd).get()
        val userInfo = loginQuery.await().documents[0]
        val fcmTokenList : ArrayList<String> = userInfo.data?.get(FireStoreInfo.USER_FCM_TOKENS) as ArrayList<String>
        fcmTokenList.add(fcmToken)
        val saveFCMQuery = fireStore.collection(FireStoreInfo.USER).document(userId).update(FireStoreInfo.USER_FCM_TOKENS, fcmTokenList)
        emit(FireStoreResponse.Success(saveFCMQuery.await()))
    }. catch { error ->
        error.message?.let { errorMessage ->
            emit(FireStoreResponse.Failure(errorMessage))
        }
    }

    fun logOut(userId: String, fcmToken: String) = flow{
        emit(FireStoreResponse.Loading())
        val getUserInfoQuery = fireStore.collection(FireStoreInfo.USER).whereEqualTo(FireStoreInfo.USER_ID, userId).get()
        val userInfo = getUserInfoQuery.await().documents[0]
        val fcmTokenList : ArrayList<String> = userInfo.data?.get(FireStoreInfo.USER_FCM_TOKENS) as ArrayList<String>
        fcmTokenList.removeIf { token -> token == fcmToken }
        val saveFCMQuery = fireStore.collection(FireStoreInfo.USER).document(userId)
            .update(FireStoreInfo.USER_FCM_TOKENS, fcmTokenList)
        emit(FireStoreResponse.Success(saveFCMQuery.await()))
    }.catch {
        emit(FireStoreResponse.Failure("로그아웃 도중 에러가 발생했습니다.\n잠시 후 다시 시도해주세요."))
    }

    fun withDraw(userId: String) = flow{
        val query = fireStore.collection(FireStoreInfo.USER).document(userId).delete()
        emit(FireStoreResponse.Loading())
        emit(FireStoreResponse.Success(query.await()))
    }.catch {
        emit(FireStoreResponse.Failure("계정 탈퇴 도중 에러가 발생했습니다.\n잠시 후 다시 시도해주세요."))
    }

    fun getUserInfo(userId : String) : Flow<User?> {
        return observeDoc(fireStore.collection(FireStoreInfo.USER).document(userId))
    }

    fun getMyWriteInfo(userId: String) : Flow<List<MyWrite?>?> {
        return observeCollection(fireStore.collection(FireStoreInfo.USER).document(userId)
            .collection(FireStoreInfo.USER_WRITE)
            .orderBy(FireStoreInfo.USER_WRITE_COMMENT_TIME, Query.Direction.DESCENDING)
        )
    }

    fun getMyCommentInfo(userId: String) : Flow<List<MyComment?>?> {
        return observeCollection(fireStore.collection(FireStoreInfo.USER).document(userId)
            .collection(FireStoreInfo.USER_COMMENT)
            .orderBy(FireStoreInfo.USER_WRITE_COMMENT_TIME, Query.Direction.DESCENDING)
        )
    }
}