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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    val newsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val listUrlOfDbCheckedForAdapter: MutableLiveData<List<String>> = MutableLiveData()

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

    suspend fun urlFavoreitesFilterForAdapter(): List<String> {
        var idList = mutableListOf<String>()
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
            val dataDB = repository.getFavoriteNews()
            val dataApi = newsLiveData.value?.data?.articles
            Log.d("checkData", "MainViewModel dataDB: $dataDB")

            Log.d("checkData", "MainViewModel dataApi: $dataApi")

            dataApi?.forEach { article ->
                if (dataDB.filter { it.url == article.url }.size > 0) {
                    //checkFavoriteLiveData.postValue(Resource.Success(false))
                    article.url?.let { idList.add(it) }
                }
            }
        }.join()
        Log.d("checkData", "MainViewModel idListOfApiEqualsDb: $idList")

        return idList
    }

    suspend fun urlFilterForAdapter() {
        var idList = mutableListOf<String>()
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
            val dataDB = repository.getFavoriteNews()
            val dataApi = newsLiveData.value?.data?.articles
            Log.d("checkData", "MainViewModel dataDB: $dataDB")

            Log.d("checkData", "MainViewModel dataApi: $dataApi")

            dataApi?.forEach { article ->
                if (dataDB.filter { it.url == article.url }.size > 0) {
                    //checkFavoriteLiveData.postValue(Resource.Success(false))
                    article.url?.let { idList.add(it) }
                }
            }
            listUrlOfDbCheckedForAdapter.postValue(idList)
        }.join()
        Log.d("checkData", "MainViewModel idListOfApiEqualsDb: $idList")

    }

}