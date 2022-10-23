package com.example.newsline.ui.favorite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsline.data.api.NewsRepository
import com.example.newsline.models.Article
import com.example.newsline.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    val favoriteNewsLiveData: MutableLiveData<Resource<List<Article>>> = MutableLiveData()

    private val exeptionHandler = CoroutineExceptionHandler { _, exeption ->
        favoriteNewsLiveData.postValue(Resource.Error(exeption.message))
    }
    init {
        getFavoriteNews()
    }

    fun getFavoriteNews() = viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
        favoriteNewsLiveData.postValue(Resource.Loading())
        val res = repository.getFavoriteArticles()
        println("DB size: ${res.size}")
        //  repository.getFavoriteArticles()
        favoriteNewsLiveData.postValue(Resource.Success(res))
    }

}