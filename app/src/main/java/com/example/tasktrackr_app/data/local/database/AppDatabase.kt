package com.example.tasktrackr_app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tasktrackr_app.data.local.dao.TeamDao
import com.example.tasktrackr_app.data.local.dao.TeamMemberDao
import com.example.tasktrackr_app.data.local.dao.TokenDao
import com.example.tasktrackr_app.data.local.dao.UserDao
import com.example.tasktrackr_app.data.local.entity.TeamEntity
import com.example.tasktrackr_app.data.local.entity.TeamMemberEntity
import com.example.tasktrackr_app.data.local.entity.TokenEntity
import com.example.tasktrackr_app.data.local.entity.UserEntity

@Database(entities = [
    TokenEntity::class,
    UserEntity::class,
    TeamEntity::class,
    TeamMemberEntity::class
 ], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun userDao(): UserDao
    abstract fun teamDao(): TeamDao
    abstract fun teamMemberDao(): TeamMemberDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tasktrackr_db"
                )
                    .fallbackToDestructiveMigration(false)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
