package com.dicoding.asclepius

import com.dicoding.asclepius.api.NewsService
import com.dicoding.asclepius.model.Article

class NewsRepository(private val service: NewsService) {
    suspend fun getCancerNews(apiKey: String): List<Article> {
        return service.getCancerNews(apiKey = apiKey).articles
    }
}
