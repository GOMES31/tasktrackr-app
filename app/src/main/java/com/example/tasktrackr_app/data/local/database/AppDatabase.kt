package com.example.tasktrackr_app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tasktrackr_app.data.local.dao.TokenDao
import com.example.tasktrackr_app.data.local.dao.UserDao
import com.example.tasktrackr_app.data.local.entity.TokenEntity
import com.example.tasktrackr_app.data.local.entity.UserEntity

@Database(entities = [TokenEntity::class, UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun userDao(): UserDao
}
