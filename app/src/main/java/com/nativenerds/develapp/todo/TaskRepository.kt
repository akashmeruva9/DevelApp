package com.nativenerds.develapp.todo


import androidx.lifecycle.LiveData
import com.nativenerds.develapp.todo.Task12
import com.nativenerds.develapp.todo.taskDAO

class TaskRepository(private val Taskdao : taskDAO){

    val alltasks : LiveData<List<Task12>> = Taskdao.getAllTasks()

    suspend fun insert(task : Task12)
    {
        Taskdao.insert(task)
    }

    suspend fun delete(task : Task12)
    {
        Taskdao.delete(task)
    }

    suspend fun update(task: Task12)
    {
        Taskdao.updateuser(task)
    }

}