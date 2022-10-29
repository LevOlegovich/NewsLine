package com.example.newsline.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsline.data.api.NewsRepository
import com.example.newsline.models.Article
import com.example.newsline.models.NewsResponse
import com.example.newsline.utils.Constants
import com.example.newsline.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    val newsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val favoritesNewsLiveData: MutableLiveData<List<Article>> = MutableLiveData()
    var newsPage = 1
    var pageSize = 50


    private val exeptionHandler = CoroutineExceptionHandler { _, exeption ->
        newsLiveData.postValue(Resource.Error(exeption.message))
    }

    init {
        getNews(Constants.RU)
    }


    fun getNews(countryCode: String) {
        newsLiveData.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {

            val response =
                repository.getNews(
                    countryCode = countryCode,
                    pageSize = pageSize,
                    pageNumber = newsPage,
                )

            if (response.isSuccessful) {
                response.body().let { res ->
                    newsLiveData.postValue(Resource.Success(res))

                }

            } else {
                newsLiveData.postValue(Resource.Error(message = "Error!!! Code: ${response.code()}"))
            }
        }

    }

    suspend fun favoreitesFilterForAdapter(response: NewsResponse): NewsResponse {
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
            delay(500)
            val dataDB = repository.getFavoriteNews()
            if (dataDB.isNotEmpty()) {
                response.articles.forEach { articleApi ->
                    articleApi.favorite = dataDB.any { it.url.equals(articleApi.url) }
                }
            } else {
                response.articles.forEach { articleApi ->
                    articleApi.favorite = false
                }
            }
        }.join()

        return response
    }


    fun deleteFavoriteNews(article: Article) =
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
            val data = repository.getFavoriteNews()
            repository.deleteFavoriteNews(data.filter { it.url.equals(article.url) }.last())

        }

    fun saveFavoriteNews(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.getFavoriteNews()
        if (data.none { it.url.equals(article.url) }) {
            repository.addToFavotriteNews(article)
        }

    }

}