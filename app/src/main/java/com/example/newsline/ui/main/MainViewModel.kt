package com.example.newsline.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsline.data.api.NewsRepository
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
            }
            else {
                newsLiveData.postValue(Resource.Error(message = "Error!!! Code: ${response.code()}"))
            }
        }
    }


}