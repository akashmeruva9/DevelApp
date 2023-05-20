package com.nativenerds.develapp.todo


import android.annotation.SuppressLint
import android.app.*
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nativenerds.develapp.R
import java.util.*


class Tasksfragment : Fragment(R.layout.fragment_taskfragment), taskchanged {

    lateinit var viewmodel : Taskviewmodel
    var taskentryrequestcode = 0
    var taskeditrequestcode = 1
    lateinit var  adapter12 : taskrecyclerviewadapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        return inflater.inflate(R.layout.fragment_taskfragment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskrecyclerview.layoutManager = LinearLayoutManager(this.context)
        adapter12 = context?.let { taskrecyclerviewadapter(it, this) }!!
        taskrecyclerview.adapter = adapter12

        addtaskButton.setOnClickListener {
            val newtaskintent = Intent(context, taskentry::class.java)
            newtaskintent.putExtra("classifier" , "new")
            startActivityForResult(newtaskintent, taskentryrequestcode)
        }

        viewmodel = ViewModelProvider(this)[Taskviewmodel::class.java]
        viewmodel.alltasks.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                adapter12.updatelist(it)
            }
        })
        createNotificationChannel()
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( requestCode == taskentryrequestcode )
        {
            if (resultCode == RESULT_OK){

                val finaltitle = data?.getStringExtra("title")
                val finaldescreption = data?.getStringExtra("description")
                val finaldate = data?.getStringExtra("date")
                val finalprogress = data?.getIntExtra("progress", 0)

                val finaltask = Task12(finaltitle, finaldate, finalprogress, finaldescreption)

                viewmodel.InsertTask(finaltask)
            }
        }

        if( requestCode == taskeditrequestcode)
        {
            if(resultCode == RESULT_OK){

                val finalid = data?.getIntExtra("id" , 0)
                val finaltitle = data?.getStringExtra("title")
                val finaldescreption = data?.getStringExtra("description")
                val finaldate = data?.getStringExtra("date")
                val finalprogress = data?.getIntExtra("progress", 0)

                val finaltask = Task12(finaltitle, finaldate, finalprogress, finaldescreption)

                finaltask.id = finalid!!
                viewmodel.Updatetas(finaltask)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            val name = "Task Notifications"
            val description = "notifies about pending tasks"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("Task Notifications", name, importance)
            channel.description = description

            val notificationManager = this.context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun OnItemClickededit(task: Task12, id: Int)
    {
        val edittaskintent = Intent(context, updatetask::class.java)
        edittaskintent.putExtra("id" , id)
        edittaskintent.putExtra("title" , task.title)
        edittaskintent.putExtra("description" , task.description)
        edittaskintent.putExtra("duedate" , task.submitdate)
        edittaskintent.putExtra("progress" , task.progress)

        startActivityForResult(edittaskintent , taskeditrequestcode)

    }

    override fun dleteitem(task: Task12) {
        viewmodel.dletetask(task)
        Toast.makeText(context , "Task Completed" , Toast.LENGTH_LONG).show()
    }

    override fun progressupdate(task: Task12) {
        viewmodel.Updatetas(task)
    }

}