package com.example.newsline.data.db

import androidx.room.*
import com.example.newsline.models.Article

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles")
    suspend fun getAllArticles(): List<Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Delete
    suspend fun delete(article: Article)

    @Query("DELETE  FROM articles")
    suspend fun deleteAll()
}