package com.example.newsline.ui.details

import android.Manifest
import android.content.Intent
import android.content.Intent.getIntent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsline.data.api.NewsRepository
import com.example.newsline.models.Article
import com.example.newsline.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    val favoriteNews = MutableLiveData<List<Article>>()

    private val exeptionHandler = CoroutineExceptionHandler { _, exeption ->

    }

    init {
        favoriteNews.postValue(emptyList())
    }

    fun saveFavoriteNews(article: Article) =
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
            val data = repository.getFavoriteNews()

            Log.d("checkData", "DetailsFragment allFavorites add before: ${data.size}")
            Log.d("checkData", "DetailsFragment article: $article")

            if (data.none { it.url == article.url }) {
                repository.addToFavotriteNews(article)
            }

        }

    fun deleteFavoriteNews(article: Article) =
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
            val data = repository.getFavoriteNews()
            Log.d("checkData", "DetailsFragment allFavorites before delete: ${data.size}")

            repository.deleteFavoriteNews(data.last { it.url == article.url })
            Log.d("checkData",
                "DetailsFragment allFavorites after delete: ${repository.getFavoriteNews().size}")
        }

    suspend fun checkFavorite(article: Article): Article {
        var newArticle = article
        newArticle.favorite = false
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
            delay(250)
            var dataDb = repository.getFavoriteNews()
            var count = 0
            if (dataDb.isNotEmpty()) {
                dataDb.forEach {
                    count++
                    if (it.url.equals(newArticle.url)) {
                        newArticle.favorite = true
                        println("Совпадение с базой данных: id= ${it.id}")
                    } else {
                        println("Нет данных: ${newArticle.favorite}")
                    }
                }
            }
            println("count= $count")
        }.join()

        return newArticle
    }


}