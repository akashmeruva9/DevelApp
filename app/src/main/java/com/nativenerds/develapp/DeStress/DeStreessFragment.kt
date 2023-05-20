package com.nativenerds.develapp.DeStress

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
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.scrollo.News
import com.nativenerds.develapp.News.MySingleton
import com.nativenerds.develapp.R
import com.nativenerds.develapp.ide.destress
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

class DeStreessFragment : Fragment(R.layout.fragment_de_streess) {
    lateinit var myadapter: Memeadapter

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = LinearLayoutManager(activity)
            loadmeme()


        val Memerecyclerview = requireActivity().findViewById<RecyclerView>(R.id.Memerecyclerview)
        Memerecyclerview.layoutManager = manager
        myadapter = Memeadapter(this)
        Memerecyclerview.adapter = myadapter

        Memerecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {

                        loadmeme()


                }
            }
        })
        val refreshmeme = requireActivity().findViewById<SwipeRefreshLayout>(R.id.refreshmeme)

        refreshmeme.setOnRefreshListener {

            myadapter.clearitems()
                loadmeme()


            Memerecyclerview.adapter = myadapter
            refreshmeme.isRefreshing = false
        }

    }
    fun loadmeme() {

        val url = "https://meme-api.com/gimme/wholesomememes/50"

        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                val newsJsonArray = response.getJSONArray("memes")
                val newsArray = ArrayList<Memes>()
                for (i in 0 until 20) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = Memes(
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("subreddit"),
                        newsJsonObject.getString("title")
                    )
                    newsArray.add(news)
                }

                myadapter.update(newsArray)


            },
            {

                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show()
            })

        activity?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest1) }
    }

    fun shareMeme(view: String) {
        val memeIntent = Intent(Intent.ACTION_SEND)
        memeIntent.type = "text/plain"
        memeIntent.putExtra(Intent.EXTRA_TEXT, "$view")
        startActivity(Intent.createChooser(memeIntent, "Share this meme with"))
    }

    fun downloadmeme(mImageView: ImageView, mWebPath: String) {

        var mImage: Bitmap?
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())

        myExecutor.execute {
            mImage = mLoad(mWebPath)
            myHandler.post {
                mImageView.setImageBitmap(mImage)
                if (mImage != null) {
                    mSaveMediaToStorage(mImage)
                }
            }
        }
    }

    private fun mLoad(string: String): Bitmap? {
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
        }
        return null
    }

    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity?.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(activity, "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }
}