package com.example.topstories.feature

import androidx.lifecycle.ViewModel
import com.example.topstories.feature.repo.TopStoriesRepo
import javax.inject.Inject

class TopStoriesListViewModel @Inject constructor(val repo: TopStoriesRepo) : ViewModel(){

    fun getTopStoriesNY() = repo.getTopStoriesNY()
}