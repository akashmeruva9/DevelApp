package com.nativenerds.develapp.todo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Task12::class), version = 1, exportSchema = false)
abstract  class TaskDatabase : RoomDatabase()
{
    abstract fun getTaskDao() : taskDAO

    companion object{
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "Task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}