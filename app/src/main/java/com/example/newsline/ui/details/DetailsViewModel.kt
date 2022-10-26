package com.example.newsline.ui.details

import android.util.Log
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
class DetailsViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    val favoriteIconCheckLiveData: MutableLiveData<Resource<Boolean>> =
        MutableLiveData(Resource.Success(false))

    private val exeptionHandler = CoroutineExceptionHandler { _, exeption ->
        favoriteIconCheckLiveData.postValue(Resource.Error(exeption.message))
    }

    init {
        // favoriteIconCheck()
    }

    fun getSavedArticles() = viewModelScope.launch(Dispatchers.IO) {
        repository.getFavoriteNews()
    }


    fun favoriteIconCheck(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.getFavoriteNews()
        if (data.filter { it.url == article.url }.size > 0) {
            favoriteIconCheckLiveData.postValue(Resource.Success(true))
        }
    }

    fun saveFavoriteNews(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.getFavoriteNews()

        Log.d("checkData", "DetailsFragment allFavorites: ${data.size}")
        Log.d("checkData", "DetailsFragment article: $article")

        val countEquals = data.filter { it.url == article.url }.size
        if (countEquals == 0) {
            repository.addToFavotriteNews(article)
            favoriteIconCheckLiveData.postValue(Resource.Success(true))
        }

    }

    fun deleteFavoriteNews(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.getFavoriteNews()

        Log.d("checkData", "DetailsFragment allFavorites before delete: ${data.size}")
        repository.deleteFavoriteNews(data.filter { it.url == article.url }.last())


        Log.d("checkData",
            "DetailsFragment allFavorites after delete: ${repository.getFavoriteNews().size}")
        favoriteIconCheckLiveData.postValue(Resource.Success(false))
    }


}