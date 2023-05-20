package com.nativenerds.develapp.todo

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nativenerds.develapp.R
import java.util.*


class taskentry : AppCompatActivity() {

    var selecteddate: Int = 0
    var selectedmonth: Int = 0
    var selectedyear: Int = 0
    var todaydate: Int = 0
    var todaymonth: Int = 0
    var todayyear: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_entry)

        val taskentrysave= findViewById<TextView>(R.id.taskentrysave)
        taskentrysave.setOnClickListener {
            check()
        }
        val taskentrycancel= findViewById<TextView>(R.id.taskentrycancel)

        taskentrycancel.setOnClickListener {
            val resultIntent = Intent()
            setResult(RESULT_CANCELED, resultIntent)
            finish()
        }
        val taskdateofsubmission= findViewById<TextView>(R.id.taskdateofsubmission)

        taskdateofsubmission.setOnClickListener{
            val c = Calendar.getInstance()
            val cyear = c.get(Calendar.YEAR)
            val cmonth = c.get(Calendar.MONTH)
            val cday = c.get(Calendar.DAY_OF_MONTH)

            todaydate = cday
            todaymonth = cmonth + 1
            todayyear = cyear


            DatePickerDialog(this, DatePickerDialog.OnDateSetListener
            { view, year, month, day ->

                taskdateofsubmission.text = null
                taskdateofsubmission.append("$day-${month + 1}-$year")
                selecteddate = day
                selectedmonth = month + 1
                selectedyear = year

            }, cyear, cmonth, cday
            ).show()
        }
        val taskentryseekBar= findViewById<SeekBar>(R.id.taskentryseekBar)
        val taskentryprogress= findViewById<TextView>(R.id.taskentryprogress)

        taskentryseekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                taskentryprogress.text = "${(taskentryseekBar.progress * 10)}%"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

    }

    private fun check() {

        var a = true
        val Tasktitledittext= findViewById<EditText>(R.id.Tasktitledittext)
        val taskdateofsubmission= findViewById<TextView>(R.id.taskdateofsubmission)

        if (Tasktitledittext.text.isNullOrEmpty()) {
            Tasktitledittext.error = "Required"
            a = false
        }

        if (taskdateofsubmission.text.isNullOrEmpty()){
            taskdateofsubmission.error = "Required"
            a = false
        }else
        {
            val greaterdate =  comparedate()
            if(greaterdate!! < 0)
            {
                taskdateofsubmission.error = "DueDate Should be greater than Today's Date"
                Toast.makeText(this , "DueDate Should be greater than Today's Date" , Toast.LENGTH_LONG).show()
                a = false
            }
        }


        if (a == true) {
            val taskentryprogress= findViewById<TextView>(R.id.taskentryprogress)
            var progress: Int = taskentryprogress.text.toString().substringBefore("%").toInt()
            val taskdescriptionedittext= findViewById<EditText>(R.id.taskdescriptionedittext)
            val resultIntent = Intent()
            resultIntent.putExtra("title", Tasktitledittext.text.toString())
            resultIntent.putExtra("description", taskdescriptionedittext.text.toString())
            resultIntent.putExtra("date", taskdateofsubmission.text.toString())
            resultIntent.putExtra("progress", progress)

            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }


    fun comparedate(): Int? {
        val sdf = java.text.SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val date1: Date? = sdf.parse("$todaydate-$todaymonth-$todayyear")
        val date2: Date? = sdf.parse("$selecteddate-$selectedmonth-$selectedyear")
        val result = date2?.compareTo(date1)

        return result
    }
}