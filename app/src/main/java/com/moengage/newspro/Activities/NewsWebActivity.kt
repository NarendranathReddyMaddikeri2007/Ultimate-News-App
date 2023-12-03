package com.moengage.newspro.Activities

import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.moengage.newspro.databinding.ActivityNewsWebBinding

class NewsWebActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityNewsWebBinding

    companion object {
        const val TAG = "NewsWebActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNewsWebBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val url = intent.getStringExtra("URL")
        Log.d(TAG, "Received intent data is $url")

        _binding.webViewActivityNewsWeb.webViewClient = WebViewClient()


        //Initialize url to webview
        if (url != null && url.isNotBlank() && url.length > 10) _binding.webViewActivityNewsWeb.loadUrl(convertUrl(url))
         else _binding.webViewActivityNewsWeb.loadUrl("https://www.google.com/")

        _binding.webViewActivityNewsWeb.settings.javaScriptEnabled = true
        _binding.webViewActivityNewsWeb.settings.setSupportZoom(true)
    }

    //Convert 'http://' url form to 'https://' form since http:// cannot work.
    fun convertUrl(url: String): String {
        if (url.isNotBlank() && url.startsWith("http://")) {
            return "https://" + url.substring(7)
        }
        return url
    }

    /*
    Manage when user Back pressed.
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (_binding.webViewActivityNewsWeb.canGoBack()) {
            _binding.webViewActivityNewsWeb.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
