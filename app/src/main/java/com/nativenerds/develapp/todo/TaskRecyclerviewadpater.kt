package com.nativenerds.develapp.todo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nativenerds.develapp.R
import kotlin.collections.ArrayList

class taskrecyclerviewadapter(private val thiscontext: Context, private val listener: taskchanged): RecyclerView.Adapter<taskviewholder>() {

    val Alltasks = ArrayList<Task12>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): taskviewholder {

        val v = LayoutInflater.from(thiscontext).inflate(R.layout.taskitem , parent ,false)
        val viewholder = taskviewholder(v)

        viewholder.progressBar.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener{
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewholder.progresspercentage.text = "${((viewholder.progressBar.progress.toInt())*10)}%"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?){
                if(viewholder.progressBar.progress == 10)
                {
                    listener.dleteitem(Alltasks[viewholder.adapterPosition])
                }else {
                    val newtask = Task12(
                        Alltasks[viewholder.adapterPosition].title,
                        Alltasks[viewholder.adapterPosition].submitdate,
                        ((viewholder.progressBar.progress.toInt()) * 10),
                        Alltasks[viewholder.adapterPosition].description
                    )
                    newtask.id = Alltasks[viewholder.adapterPosition].id
                    listener.progressupdate(newtask)
                }
            }
        })

        v.setOnClickListener {
            listener.OnItemClickededit( Alltasks[viewholder.adapterPosition] , Alltasks[viewholder.adapterPosition].id)
        }
        return viewholder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: taskviewholder, position: Int)
    {
        val currenttask = Alltasks[position]


        holder.duedate.text = "${currenttask.submitdate}"
        holder.title.text = currenttask.title
        holder.descreption.text = currenttask.description
        holder.progresspercentage.text = "${currenttask.progress}%"
        holder.progressBar.progress = (currenttask.progress?.toInt())?.div(10) ?: 1


    }

    override fun getItemCount(): Int {

        return Alltasks.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatelist(newlist: List<Task12>)
    {
        Alltasks.clear()
        Alltasks.addAll(newlist)
        notifyDataSetChanged()
    }

}

class taskviewholder(itemView: View) : RecyclerView.ViewHolder(itemView){

    val title = itemView.findViewById<TextView>(R.id.tasktitle)
    val descreption = itemView.findViewById<TextView>(R.id.taskdescription)
    val duedate = itemView.findViewById<TextView>(R.id.taskduedate)
    val progressBar = itemView.findViewById<SeekBar>(R.id.taskseekbar)
    val progresspercentage = itemView.findViewById<TextView>(R.id.taskentryprogress123)

}

interface taskchanged{
    fun OnItemClickededit(task : Task12, id : Int)
    fun dleteitem(task: Task12)
    fun progressupdate(task: Task12)
}

