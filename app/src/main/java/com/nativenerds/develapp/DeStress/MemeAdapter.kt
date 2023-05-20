package com.nativenerds.develapp.DeStress

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nativenerds.develapp.R


class Memeadapter( private val listener:DeStreessFragment): RecyclerView.Adapter<Memeviewholder>()
{
    val items: ArrayList<Memes> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): Memeviewholder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memeitem , parent , false )
        val viewholder = Memeviewholder(view)
        return viewholder
    }

    override fun getItemCount(): Int {

        return  items.size
    }

    override fun onBindViewHolder(holder: Memeviewholder, position: Int)
    {
        var issaved = false
        val currentitem = items[position].url.toString()
        holder.title.text = items[position].title
        holder.description.text = items[position].description.toString()

        Glide.with(holder.itemView.context).load(currentitem).into(holder.image)


        holder.sharebutton.setOnClickListener {
            listener.shareMeme(currentitem)
        }

        holder.download.setOnClickListener {
            listener.downloadmeme( holder.image ,currentitem)
        }

    }

    fun clearitems()
    {
        items.clear()
    }
    fun update(updatedNews: ArrayList<Memes>) {
        items.clear()
        items.addAll(updatedNews)

        notifyDataSetChanged()
    }
}


class Memeviewholder(itemView : View): RecyclerView.ViewHolder(itemView)
{
    val image: ImageView = itemView.findViewById(R.id.memeimage)
    val sharebutton : Button = itemView.findViewById(R.id.memesharebutton)
    val download : Button = itemView.findViewById(R.id.memedownloadbutton)
    val description : TextView = itemView.findViewById(R.id.memeDescription)
    val title : TextView = itemView.findViewById(R.id.memetitle)
}

interface shareclick{

    fun shareMeme( view : String)
}
interface downloadclick{

    fun downloadmeme( view : String)
}