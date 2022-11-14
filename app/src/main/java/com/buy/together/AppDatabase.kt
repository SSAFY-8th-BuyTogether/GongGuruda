package com.buy.together

import androidx.room.Database
import androidx.room.RoomDatabase
import com.buy.together.data.dao.UserDao
import com.buy.together.data.entity.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
}
