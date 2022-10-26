package com.example.newsline.ui.main

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
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    val newsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

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
                urlFavoreitesFilterForAdapter(response)
                response.body().let { res ->
                    newsLiveData.postValue(Resource.Success(res))
                }
            } else {
                newsLiveData.postValue(Resource.Error(message = "Error!!! Code: ${response.code()}"))
            }
        }
    }

    private suspend fun urlFavoreitesFilterForAdapter(response: Response<NewsResponse>) {
        val dataDB = repository.getFavoriteNews()
        response.body()?.articles?.forEach { article ->
            if (dataDB.any { it.url == article.url }) {
                article.favorite = true
            }
        }

    }

    fun deleteFavoriteNews(article: Article) =
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
            val data = repository.getFavoriteNews()
            repository.deleteFavoriteNews(data.filter { it.url == article.url }.last())

        }

    fun saveFavoriteNews(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.getFavoriteNews()
        if (data.none { it.url == article.url }) {
            repository.addToFavotriteNews(article)
        }

    }


}