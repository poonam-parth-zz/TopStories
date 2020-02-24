package com.example.topstories.feature.topStoriesList

import com.example.topstories.db.entity.ArticleEntity
import com.example.topstories.mvibase.MviAction

sealed class TopStoriesListAction : MviAction {
    data class LoadStoriesListAction(val isRefreshing: Boolean = false, val filterType: FilterType = FilterType.Science, val offline : Boolean = false) : TopStoriesListAction()
    data class UpdateStoriesListAction(val articles: List<ArticleEntity> ) : TopStoriesListAction()
}