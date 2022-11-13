package com.buy.together.data.repository

import android.content.Context
import androidx.room.Room
import com.buy.together.AppDatabase

private const val DATABASE_NAME = "gong_gu_rumi.db"
class UserRepository private constructor(context: Context){

    private val database : AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val userDao = database.userDao()

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



}