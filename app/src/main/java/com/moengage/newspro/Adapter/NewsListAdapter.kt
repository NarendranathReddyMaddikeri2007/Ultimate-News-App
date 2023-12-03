package com.moengage.newspro.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.moengage.newspro.Activities.NewsItemActivity
import com.moengage.newspro.DataFiles.NewsItem
import com.moengage.newspro.R

class NewsListAdapter(
    private val newsList: List<NewsItem>,
    private val context : Context?
) : RecyclerView.Adapter<NewsListAdapter.ViewHolder>() {

    companion object{
        private val TAG = "NewsListAdapter"
    }

    private val list = mutableListOf<NewsItem>()

    init {
        list.addAll(newsList)
    }

    fun updateData(newList : List<NewsItem>){
        list.clear()
        list.addAll(newList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(this.context).inflate(R.layout.news_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //Initialize recyclcerview item view with data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG,"onBindViewHolder() started")

        holder.author_tv.text = getValidAuther(list.get(holder.adapterPosition).getAuthor())
        holder.title_tv.text = list.get(holder.adapterPosition).getTitle()
        holder.publishedDate_tv.text = list.get(holder.adapterPosition).getPDate().split("T").get(0)
        Log.d(TAG,"author_tv, title_tv &  publishedDate_tv are assigned")

        //Load image using url with help of Glide
        if(context!=null){
            Log.d(TAG,"Image Url parsing started")
            Glide.with(context)
                .load(list.get(holder.adapterPosition).getImageUrl())
                .override(holder.imageView.width,holder.imageView.height)
                .error(R.drawable.placeholder_image)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageView)
            Log.d(TAG,"Image Url parsing completed")
        }


        //User clicks on CardView, transfer data to NewsItemActivity through intent
        holder.cardView.setOnClickListener {
            Log.d(TAG,"CardView is clicked")
            val intent = Intent(context, NewsItemActivity::class.java)
            intent.putExtra("AUTHOR",list[holder.adapterPosition].getAuthor())
            intent.putExtra("TITLE",list[holder.adapterPosition].getTitle())
            intent.putExtra("DESCRIPTION",list[holder.adapterPosition].getDescription())
            intent.putExtra("URL",list[holder.adapterPosition].getUrl())
            intent.putExtra("IMAGEURL",list[holder.adapterPosition].getImageUrl())
            intent.putExtra("DATE",list[holder.adapterPosition].getPDate())
            Log.d(TAG,"Going to start activity")
            context?.startActivity(intent)
        }

    }



    //Minimize the author name.
    fun getValidAuther(auther : String) : String{
        var result = auther
        if(result.contains("@") || result==null || result=="null" || result.contains("/")){
            result = "author"
        }
        else if(result.length>15){
            result = result.split(" ").get(0)
        }
        else if(result.contains(",")){
            result = result.split(",").get(0)
        }
        return result
    }



    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
          val author_tv : TextView = itemView?.findViewById(R.id.newsAuthor_news_item_layout) as TextView
          val publishedDate_tv : MaterialButton = itemView?.findViewById(R.id.newsPublishedDate_news_item_layout)  as MaterialButton
          val title_tv : TextView = itemView?.findViewById(R.id.newsTitle_news_item_layout) as TextView
          val imageView :   ImageView = itemView?.findViewById(R.id.imageView_news_item_layout) as ImageView
          val cardView : MaterialCardView = itemView?.findViewById(R.id.materialCardView_news_item_layout) as MaterialCardView
    }

}