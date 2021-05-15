package com.azatberdimyradov.newsapp.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.azatberdimyradov.newsapp.api.NewsAPI
import com.azatberdimyradov.newsapp.db.ArticleDatabase
import com.azatberdimyradov.newsapp.model.Article
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    val db: ArticleDatabase,
    private val newsApi: NewsAPI
) {
    /*suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        newsApi.getBreakingNews(countryCode, pageNumber)*/

    /*suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        newsApi.searchForNews(searchQuery, pageNumber)*/

    fun getBreakingNews(countryCode: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {BreakingNewsPagingSource(newsApi, countryCode)}
        ).flow

    fun searchNews(searchQuery: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NewsPagingSource(newsApi, searchQuery) }
        ).flow

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}