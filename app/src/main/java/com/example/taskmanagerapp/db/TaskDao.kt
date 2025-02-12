package com.example.taskmanagerapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    @Insert
    fun insertTask(task: TaskEntity)

    @Update
    fun updateTask(task: TaskEntity)

    @Delete
    fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    fun deleteById(taskId: Int): Int

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): TaskEntity?

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<TaskEntity>
}
