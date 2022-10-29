package com.example.newsline.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val title: String?,
    val url: String?,
    val urlToImage: String?,

    var favorite: Boolean = false,
) : Serializable


//@Entity(tableName = "articles")
//data class Article(
//    @PrimaryKey(autoGenerate = true)
//    val id: Int? = null,
//    val author: String,
//    val content: String,
//    val description: String,
//    val publishedAt: String,
//    val source: Source,
//    val title: String,
//    val url: String,
//    val urlToImage: String
//)


