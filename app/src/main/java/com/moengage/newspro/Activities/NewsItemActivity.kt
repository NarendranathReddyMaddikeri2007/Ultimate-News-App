package com.moengage.newspro.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.moengage.newspro.R
import com.moengage.newspro.databinding.ActivityNewsItemBinding

class NewsItemActivity : AppCompatActivity() {

    private lateinit var _binding : ActivityNewsItemBinding

    companion object{
        const val TAG = "NewsItemActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNewsItemBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        _binding.newsAuthorActivityNewsItem.text = "Author : ${intent.getStringExtra("AUTHOR")}"
        _binding.newsTitleActivityNewsItem.text = intent.getStringExtra("TITLE")
        _binding.descriptionActivityNewsItem.text = intent.getStringExtra("DESCRIPTION")
        val imageUrl = intent.getStringExtra("IMAGEURL")
        val url = intent.getStringExtra("URL")


        //Used to load image from url. Url came from RecyclerView Adapter through Intent.
        Glide.with(this@NewsItemActivity)
            .load(imageUrl)
            .override(_binding.imageViewActivityNewsItem.width,_binding.imageViewActivityNewsItem.height)
            .error(R.drawable.placeholder_image)
            .placeholder(R.drawable.placeholder_image)
            .into(_binding.imageViewActivityNewsItem)
        _binding.dateCreatedActivityNewsItem.text = "Date posted : ${intent.getStringExtra("DATE")}"


        //User will share webpage url to other apps.
        _binding.shareButtonActivityNewsItem.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        //Opens new window with webview of current news using url.
        _binding.webpageButtonActivityNewsItem.setOnClickListener {
            val inten = Intent(this@NewsItemActivity, NewsWebActivity::class.java)
            inten.putExtra("URL",url)
            Log.d(TAG,"starting intent with data ${url}")
            startActivity(inten)
        }

    }
}