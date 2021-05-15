package com.azatberdimyradov.newsapp.repository

import androidx.paging.PagingSource
import com.azatberdimyradov.newsapp.api.NewsAPI
import com.azatberdimyradov.newsapp.model.Article
import com.azatberdimyradov.newsapp.model.NewsResponse
import retrofit2.HttpException
import java.io.IOException

private const val SEARCH_NEWS_PAGE_INDEX = 1

class NewsPagingSource(
    private val newsApi: NewsAPI,
    private val query: String
    ): PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: SEARCH_NEWS_PAGE_INDEX

        return try {
            val response = newsApi.searchForNews(query, position)
            val articles = response.articles
            LoadResult.Page(
                data = articles,
                prevKey = if (position == SEARCH_NEWS_PAGE_INDEX) null else position - 1,
                nextKey = if (articles.isEmpty()) null else position + 1
            )
        }catch (exception: IOException){
            LoadResult.Error(exception)
        }catch (exception: HttpException){
            LoadResult.Error(exception)
        }
    }
}