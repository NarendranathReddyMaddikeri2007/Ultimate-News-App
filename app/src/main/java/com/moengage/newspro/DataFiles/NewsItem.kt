package com.moengage.newspro.DataFiles

import java.io.Serializable


data class NewsItem(
    private val author: String,
    private val title: String,
    private val description : String,
    private val url : String,
    private val urlToImage : String,
    private val publishedAt: String,
    private val content : String
)  : Serializable {

    public fun getAuthor() : String{
        return this.author
    }

    public fun getTitle() : String{
        return this.title
    }

    public fun getDescription() : String{
        return this.description
    }

    public fun getUrl() : String{
        return this.url
    }

    public fun getImageUrl() : String{
        return this.urlToImage
    }

    public fun getPDate() : String{
        return this.publishedAt
    }

    public fun getContent() : String{
        return this.content
    }

}
