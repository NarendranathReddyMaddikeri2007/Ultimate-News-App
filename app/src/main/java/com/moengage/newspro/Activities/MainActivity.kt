package com.moengage.newspro.Activities


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.moengage.newspro.Adapter.NewsListAdapter
import com.moengage.newspro.DataFiles.NewsItem
import com.moengage.newspro.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale


class MainActivity : AppCompatActivity() {


    private lateinit var _binding : ActivityMainBinding
    private var adapter: NewsListAdapter? = null
    companion object{
        const val TAG = "MainActivity"
        const val NOTIFICATION_ID = 1
    }
    private var _ORDER_BY : String = "Relevant"

    //to store small amount of key-value based data
    private lateinit var sharedpreferences : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        try{
            //initialize sharedpreferences
            updateOrderBy()
            _binding.recyclerviewActivityMain.layoutManager = LinearLayoutManager(this@MainActivity)
            Log.d(TAG,"Sets Layouts & Binding")

            //Won't run on MainThread
            GlobalScope.launch(Dispatchers.Main) {
                val result = getNewsData(true)
                if (result != null) {
                    adapter = NewsListAdapter(result,this@MainActivity)
                    _binding.recyclerviewActivityMain.adapter = adapter
                    _binding.recyclerviewActivityMain.setHasFixedSize(true)
                    Log.d(TAG,"Recyclerview is initialized with Adapter")
                }
            }
            _binding.sortOrderActivityMain.setOnClickListener {
                //Sort order dialog will open
                showSortOrderDialog()
            }

        }
        catch (e : Exception){
            e.printStackTrace()
        }

    }

    /*
    When user clicked on sort order button and selects particular order.
    Below method will sort the recyclerview based on user selected order.
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun updateRecyclerView(order : String) {
       try{
           val newList = mutableListOf<NewsItem>()
           val tempList = mutableListOf<NewsItem>()
           newList.clear()
           GlobalScope.launch(Dispatchers.Main) {
               getNewsData(false)?.let { tempList.addAll(it) }
               if (tempList != null) {
                   newList.clear()
                   when (order) {
                       "o3" -> {
                           newList.addAll(tempList.sortedByDescending { it.getPDate() })
                       }
                       "o2" -> {
                           newList.addAll(tempList.sortedBy { it.getPDate() })
                       }
                       "o4" -> {
                           newList.addAll(tempList.sortedBy { it.getAuthor() })
                       }
                       else -> {
                           Toast.makeText(this@MainActivity,"Invalid Order", Toast.LENGTH_SHORT).show()
                       }
                   }
                   Toast.makeText(this@MainActivity,"Recyclerview is ordered",Toast.LENGTH_SHORT).show()
                   adapter?.updateData(newList)
                   adapter?.notifyDataSetChanged()
               }
           }
       }
       catch (e : Exception){
           e.printStackTrace()
       }
    }

    private fun updateOrderBy() {
        sharedpreferences = this@MainActivity.getSharedPreferences("ORDER_BY", Context.MODE_PRIVATE)
        _ORDER_BY =  sharedpreferences.getString("ORDER_BY", "o1").toString()
    }


    /*
    This method is for SliderImage View. Items in news as parameter will be assigned to
    SliderImage view.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun loadImageSlider(news : List<NewsItem>){
         val list = mutableListOf<SlideModel>()
         for(img in news){
             val title = img.getTitle()
             if(title.length>20) "${img.getTitle().substring(0,20)}..."
             list.add(SlideModel(imageUrl = img.getImageUrl(),title = title, scaleType =  ScaleTypes.CENTER_CROP))
         }
        GlobalScope.launch(Dispatchers.Main) {
            _binding.imageSliderActivityMain.setImageList(list)
            _binding.imageSliderActivityMain.startSliding(1000)
            _binding.imageSliderActivityMain.setSlideAnimation(AnimationTypes.BACKGROUND_TO_FOREGROUND)
        }

    }


    /*
    Establishing connection with given weblink and convert API data into JSON string.
    Uses only System APIs.
     */
    private suspend fun getNewsData(isSlider : Boolean): List<NewsItem> = withContext(Dispatchers.IO) {
        Log.d(TAG,"getNewsData() started")
        val apiUrl = "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"
        val url = URL(apiUrl)

        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val inputStream = connection.inputStream
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val response = StringBuilder()
        bufferedReader.forEachLine {
            response.append(it)
        }
        bufferedReader.close()
        connection.disconnect()
        Log.d(TAG,"getNewsData() completed")
        return@withContext parseJson(response.toString(),isSlider)
    }


    /*
    Will parse JSON data and required data are extracted from JSON string and then stored in NewsItem Object.
     */
    private fun parseJson(jsonString: String, isSlider : Boolean): List<NewsItem> {
        Log.d(TAG,"parseJson() started")
        val newsList = mutableListOf<NewsItem>()
        val jsonObject = JSONObject(jsonString)
        val articlesArray = jsonObject.getJSONArray("articles")

        for (i in 0 until articlesArray.length()) {
            val article = articlesArray.getJSONObject(i)
            //---------------------------------------
            val author = article.getString("author")
            val title = article.getString("title")
            val description = article.getString("description")
            val url = article.getString("url")
            val urlToImage = article.getString("urlToImage")
            val publishedAt = article.getString("publishedAt")
            val content = article.getString("content")
            //-------------
            Log.d(TAG,"Updated time is ${getUpdatedTime(publishedAt)}")
            val newsItem = NewsItem(
                author = author,
                title = title,
                description = description,
                url = url,
                urlToImage = urlToImage,
                publishedAt = getUpdatedTime(publishedAt),
                content = content
            )
            newsList.add(newsItem)
        }

        Log.d(TAG,"parseJson() completed")

        if(!isSlider){
            val tempList = mutableListOf<NewsItem>()
            when (_ORDER_BY) {
                "o3" -> {
                    tempList.addAll(newsList.sortedByDescending { it.getPDate() })
                }
                "o2" -> {
                    tempList.addAll(newsList.sortedBy { it.getPDate() })
                }
                "o4" -> {
                    tempList.addAll(newsList.sortedBy { it.getAuthor() })
                }
                else -> {
                    // You can customize this based on your specific requirements
                }
            }
            return tempList
        }

        val indexes = (newsList.size-7..<newsList.size-1).toList()
        loadImageSlider(newsList.slice(indexes))
        return newsList
    }


    /*
    This dialog will show choices to user for sorting the recyclerview in certain order.
     */
    private fun showSortOrderDialog() {
        val orderingList = arrayOf("Relevant", "Date added (newest)", "Date added (oldest)", "Author", "Most Popular")
        val codes = arrayOf("o1","o2","o3","o4","o5")
        val currentOrder = getCurrentOrder()
        var checkedItem = 0
        if(codes.contains(currentOrder)){
            checkedItem = codes.indexOfFirst { it == currentOrder }
        }
        MaterialAlertDialogBuilder(this)
            .setCancelable(true)
            .setTitle("Select Sort Order")
            .setSingleChoiceItems(orderingList, checkedItem) { dialog, which ->
                val selectedOrder : String = when (which) {
                    0 -> "o1"
                    1 -> "o2"
                    2 -> "o3"
                    3 -> "o4"
                    4 -> "o5"
                    else -> ""
                }

                //Update SharedPreferences, RecyclerView and _ORDER_BY variable
                updateSharedPreferences(selectedOrder)
                _ORDER_BY = selectedOrder
                updateRecyclerView(order = selectedOrder)
                dialog.dismiss()
            }
            .setPositiveButton("Ok",null)
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun getCurrentOrder(): String {
        return sharedpreferences.getString("ORDER_BY","o1").toString()
    }

    private fun updateSharedPreferences(selectedOrder: String) {
        val editor : SharedPreferences.Editor = sharedpreferences.edit()
        editor.putString("ORDER_BY",selectedOrder)
        editor.apply()
    }


    //Convert data-time given in API into minimized form.
    fun getUpdatedTime(time: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())

        try {
            val date = inputFormat.parse(time)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }


}