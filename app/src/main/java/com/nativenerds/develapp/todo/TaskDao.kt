package com.nativenerds.develapp.todo

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nativenerds.develapp.todo.Task12

@Dao
interface taskDAO
{
    @Insert
    fun insert(task: Task12)

    @Delete
    fun delete(task: Task12)

    @Query("Select * from task_table order by id ASC")
    fun getAllTasks() : LiveData<List<Task12>>

    @Update
    fun updateuser(task: Task12)

}