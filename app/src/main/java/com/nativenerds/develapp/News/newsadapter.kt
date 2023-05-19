package com.example.scrollo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nativenerds.develapp.News.NewsFragment
import com.nativenerds.develapp.R

class Newsadapter(private val listener: NewsFragment): RecyclerView.Adapter<NewsViewHolder>() {

    private val items: ArrayList<News> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.newsitem, parent, false)
        val viewHolder = NewsViewHolder(view)
        view.setOnClickListener{
            listener.onItemClicked(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        var issaved = false
        val currentItem = items[position]
        holder.titleView.text = currentItem.description
        holder.author.text = currentItem.title
      //  holder.timetext.text = currentItem.Time.substringBefore("T")
        Glide.with(holder.itemView.context).load(currentItem.cover_image).into(holder.image)

        holder.sharebutton.setOnClickListener {
            listener.shareNews(currentItem.url)
        }
    }



    fun update(updatedNews: ArrayList<News>) {
        items.clear()
        items.addAll(updatedNews)

        notifyDataSetChanged()
    }
}

class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleView: TextView = itemView.findViewById(R.id.newstitle)
    val image: ImageView = itemView.findViewById(R.id.newsimage)
    val author: TextView = itemView.findViewById(R.id.newsdescription)
    val sharebutton : Button = itemView.findViewById(R.id.newssharebutton)
}

interface NewsItemClicked {
    fun onItemClicked(item: News)
}
interface saced{
     fun savedbtnclicked()
}
interface share{
    fun shareNews(view :String)
}


