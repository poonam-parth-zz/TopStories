package com.example.topstories.feature.ui.topStoriesDetails

import com.example.topstories.mvibase.MviIntent


sealed class TopStoriesDetailsIntent : MviIntent {
    data class IntialIntent(val name: String?) : TopStoriesDetailsIntent()

}