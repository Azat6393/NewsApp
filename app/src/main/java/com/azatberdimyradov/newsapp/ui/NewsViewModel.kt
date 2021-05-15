package com.azatberdimyradov.newsapp.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.azatberdimyradov.newsapp.model.Article
import com.azatberdimyradov.newsapp.model.NewsResponse
import com.azatberdimyradov.newsapp.repository.NewsRepository
import com.azatberdimyradov.newsapp.util.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel @ViewModelInject constructor(
        val newsRepository: NewsRepository
) : ViewModel() {

    private val breakingNewsEventChannel = Channel<NewsState>()
    val breakingNewsEvent = breakingNewsEventChannel.receiveAsFlow()

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        newsRepository.getBreakingNews(countryCode).collectLatest {
            breakingNewsEventChannel.send(NewsState.BreakingNewsResponse(it))
        }
    }

    fun getSearchNews(searchQuery: String) = newsRepository.searchNews(searchQuery).cachedIn(viewModelScope)

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedArticle() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    sealed class NewsState{
        data class BreakingNewsResponse(val resource: PagingData<Article>): NewsState()
    }
}