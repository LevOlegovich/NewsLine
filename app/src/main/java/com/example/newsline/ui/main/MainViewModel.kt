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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    val newsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val checkFavoriteLiveData: MutableLiveData<Resource<Boolean>> =
        MutableLiveData(Resource.Success(false))

    var newsPage = 1

    private val exeptionHandler = CoroutineExceptionHandler { _, exeption ->
        newsLiveData.postValue(Resource.Error(exeption.message))
    }

    init {
        getNews(Constants.RU)
    }

    fun getNews(countryCode: String) {
        newsLiveData.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {

            val response = repository.getNews(countryCode = countryCode, pageNumber = newsPage)
            if (response.isSuccessful) {
                response.body().let { res ->
                    newsLiveData.postValue(Resource.Success(res))
                }
            } else {
                newsLiveData.postValue(Resource.Error(message = "Error!!! Code: ${response.code()}"))
            }
        }
    }

    fun getFavoriteNews() {
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
            Resource.Success(repository.getFavoriteNews())
        }
    }

    suspend fun getUrlFavoriteEqualsApi(): List<String> {
        var idList = mutableListOf<String>()
        viewModelScope.launch(Dispatchers.IO) {
            val dataDB = repository.getFavoriteNews()
            val dataApi = newsLiveData.value?.data?.articles
            Log.d("checkData", "MainViewModel dataDB: $dataDB")

            Log.d("checkData", "MainViewModel dataApi: $dataApi")

            dataApi?.forEach { i ->
                if (dataDB.filter { it.url == i.url }.size > 0) {
                    checkFavoriteLiveData.postValue(Resource.Success(false))
                    i.url?.let { idList.add(it) }
                }
            }
        }.join()
        Log.d("checkData", "MainViewModel idListOfApiEqualsDb: $idList")

        return idList
    }


}