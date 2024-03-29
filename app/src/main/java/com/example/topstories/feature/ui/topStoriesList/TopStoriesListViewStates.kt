package com.example.topstories.feature.ui.topStoriesList

import com.example.topstories.db.entity.ArticleEntity
import com.example.topstories.mvibase.MviViewState


data class TopStoriesListViewStates(
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val articles: List<ArticleEntity>,
    val filterType: FilterType,
    val error: Throwable?,
    val initial: Boolean,
    val isOffline : Boolean
) : MviViewState {
    companion object {
        fun idle(): TopStoriesListViewStates {
            return TopStoriesListViewStates(
                isLoading = false,
                isRefreshing = false,
                articles = emptyList(),
                filterType = FilterType.Science,
                error = null,
                initial = true,
                isOffline = false
            )
        }
    }

    override fun toString(): String {
        return "isLoadint : ${isLoading} , isRefreshing : $isRefreshing article : ${articles.isNullOrEmpty()} filter : ${filterType}"
    }
}