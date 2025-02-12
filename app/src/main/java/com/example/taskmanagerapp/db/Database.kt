package com.example.taskmanagerapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {

    abstract fun taskDao(): TaskDao


}
