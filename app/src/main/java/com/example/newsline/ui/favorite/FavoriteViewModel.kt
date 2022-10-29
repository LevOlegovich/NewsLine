package com.example.newsline.ui.favorite

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
class FavoriteViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    val favoriteNewsLiveData: MutableLiveData<Resource<List<Article>>> = MutableLiveData()

    private val exeptionHandler = CoroutineExceptionHandler { _, exeption ->
        favoriteNewsLiveData.postValue(Resource.Error(exeption.message))
    }


    fun getFavoriteNews() = viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
        favoriteNewsLiveData.postValue(Resource.Loading())
        val data = repository.getFavoriteNews() as ArrayList

        data.reverse()
        favoriteNewsLiveData.postValue(Resource.Success(data))
        Log.d("checkData", "FavoriteViewModel data: ${data.size}")
    }

    fun deleteFavoriteNews(article: Article) =
        viewModelScope.launch(Dispatchers.IO + exeptionHandler) {
            repository.deleteFavoriteNews(article)
            getFavoriteNews()
        }


}