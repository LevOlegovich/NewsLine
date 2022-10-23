package com.example.newsline.data.api

import com.example.newsline.data.db.ArticleDao
import com.example.newsline.models.Article
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsService: NewsService,
    private val articleDao: ArticleDao
) {
    suspend fun getNews(countryCode: String, pageNumber: Int) =
        newsService.getHeadlines(countryCode = countryCode, page = pageNumber)

    suspend fun getSearchNews(query: String, pageNumber: Int) =
        newsService.getEverything(query = query, page = pageNumber)

    suspend fun getFavoriteArticles() = articleDao.getAllArticles()
    suspend fun addToFavotrite(article: Article) = articleDao.insert(article = article)
    suspend fun deleteFromFavotrite(article: Article) = articleDao.delete(article = article)
}