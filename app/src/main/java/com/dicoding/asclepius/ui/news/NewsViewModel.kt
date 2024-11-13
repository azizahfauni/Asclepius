package com.dicoding.asclepius.ui.news

import androidx.lifecycle.*
import com.dicoding.asclepius.NewsRepository
import com.dicoding.asclepius.model.Article
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _newsArticles = MutableLiveData<List<Article>>()
    val newsArticles: LiveData<List<Article>> = _newsArticles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchCancerNews(apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val articles = repository.getCancerNews(apiKey)
                _newsArticles.value = articles
            } catch (e: Exception) {
                // Handle error, e.g., show a Toast
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class NewsViewModelFactory(private val repository: NewsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
