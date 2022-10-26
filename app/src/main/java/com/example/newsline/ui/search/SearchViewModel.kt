package com.example.newsline.ui.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsline.data.api.NewsRepository
import com.example.newsline.models.NewsResponse
import com.example.newsline.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    val searchNewsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    private val exeptionHandler = CoroutineExceptionHandler { _, exeption ->
        searchNewsLiveData.postValue(Resource.Error(exeption.message))
    }


    fun getSearchNews(query: String) =
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
            searchNewsLiveData.postValue(Resource.Loading())
            val response = repository.getSearchNews(query = query, pageNumber = searchNewsPage)
            if (response.isSuccessful) {
                response.body()?.let { res ->
                    searchNewsLiveData.postValue(Resource.Success(res))
                    Log.d("checkData", "SearchFragment text: ${res.toString()}")
                }
            } else {
                searchNewsLiveData.postValue(Resource.Error(message = "Error!!! Code: ${response.code()}"))
            }
        }
}