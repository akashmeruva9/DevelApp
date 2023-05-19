package com.nativenerds.develapp.News

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.scrollo.News
import com.example.scrollo.Newsadapter
import com.nativenerds.develapp.R
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors

class NewsFragment : Fragment(R.layout.fragment_news) {

    private lateinit var mAdapter: Newsadapter
    var url = "https://dev.to/api/articles"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsrecyclerview = requireActivity().findViewById<RecyclerView>(R.id.Memerecyclerview)
        newsrecyclerview.layoutManager = LinearLayoutManager(activity)
        fetchData()
        mAdapter = Newsadapter(this)
        newsrecyclerview.adapter = mAdapter
        val refreshnews = requireActivity().findViewById<SwipeRefreshLayout>(R.id.refreshmeme)

        refreshnews.setOnRefreshListener {

            fetchData()
            mAdapter = Newsadapter(this)
            newsrecyclerview.adapter = mAdapter
            refreshnews.isRefreshing = false
            newsrecyclerview.visibility = View.VISIBLE

        }
    }

        private fun fetchData() {

            val jsonObjectRequest = object : JsonArrayRequest(
                Request.Method.GET, url, null,
                { response ->

                    val newsArray = ArrayList<News>()
                    for (i in 0 until response.length()) {
                        val newsJsonObject = response.getJSONObject(i)
                        val news = News(
                            newsJsonObject.getString("title"),
                            newsJsonObject.getString("description"),
                            newsJsonObject.getString("url"),
                            newsJsonObject.getString("cover_image")
                            )
                        newsArray.add(news)
                    }

                    mAdapter.update(newsArray)

                },
                {
                    Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                    url = "https://dev.to/api/articles"

                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers = HashMap<String, String>()
                    headers["User-Agent"] = "Mozilla/5.0"
                    return headers
                }
            }
            activity?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
        }

        fun onItemClicked(item: News) {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            activity?.let { customTabsIntent.launchUrl(it, Uri.parse(item.url)) }
        }


    fun shareNews(view: String) {
        val memeIntent = Intent(Intent.ACTION_SEND)
        memeIntent.type = "text/plain"
        memeIntent.putExtra(Intent.EXTRA_TEXT, "$view")
        startActivity(Intent.createChooser(memeIntent, "Share this meme with"))
    }

    }
