package com.example.topstories.feature.topStoriesList

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Filter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.topstories.R
import com.example.topstories.base.BaseActivity
import com.example.topstories.feature.adapter.TopStoriesAdapter
import com.example.topstories.feature.listener.TopStoryListener
import com.example.topstories.feature.topStoriesDetails.TopStoriesDetailsActivity
import com.example.topstories.utils.isNetworkConnected
import com.example.topstories.utils.makeInVisible
import com.example.topstories.utils.makeVisible
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject.create
import kotlinx.android.synthetic.main.activity_top_stories_list.*
import com.google.android.material.snackbar.Snackbar



class TopStoriesListActivity :
    BaseActivity<TopStoriesListViewModel, TopStoriesListIntent, TopStoriesListViewStates>(),
    View.OnClickListener,TopStoryListener {

    private var storiesAdapter: TopStoriesAdapter = TopStoriesAdapter()
    private val initialIntentPublisher = create<TopStoriesListIntent.InitialIntent>()
    private val swipeToRefreshIntent = create<TopStoriesListIntent.SwipeToRefresh>()
    private val loadFilteredStories = create<TopStoriesListIntent.LoadFilteredStories>()
    private val updateStoriesList = create<TopStoriesListIntent.UpdateFilteredStories>()
    private var currentSection: FilterType = FilterType.Science

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewModel().statesLiveData.observe(this, Observer { state ->
            render(state!!)
        })

        swipeRefresh.setOnRefreshListener {
            swipeToRefreshIntent.onNext(TopStoriesListIntent.SwipeToRefresh(currentSection))
        }
    }

    override fun provideLayout(): Int = R.layout.activity_top_stories_list

    override fun provideViewModelClass(): Class<TopStoriesListViewModel> =
        TopStoriesListViewModel::class.java

    override fun startStream() {
        getViewModel().processIntents(intents())
    }

    override fun initViews() {
        setRecyclerView()
        setClickListeners()
    }

    override fun render(state: TopStoriesListViewStates) {
        showProperState(state)
        if (state.initial) {

            if (isNetworkConnected()) {
                initialIntentPublisher.onNext(TopStoriesListIntent.InitialIntent)
                updateStoriesList.onNext(TopStoriesListIntent.UpdateFilteredStories(FilterType.Science))
            } else
                loadFilteredStories.onNext(
                    TopStoriesListIntent.LoadFilteredStories(
                        filterType = FilterType.Science,
                        offline = true
                    )
                )

        }
    }

    private fun setClickListeners() {
        clBtmSectionBusiness.setOnClickListener(this)
        clBtmSectionMovie.setOnClickListener(this)
        clBtmSectionScience.setOnClickListener(this)
        clBtmSectionWorld.setOnClickListener(this)
    }

    private fun showProperState(state: TopStoriesListViewStates) {

        Log.d("stttaa", state.toString())
        if (state.isLoading) {
            progressBar.makeVisible()
        } else {
            progressBar.makeInVisible()
        }

        if (state.articles.isNotEmpty()) {
            storiesAdapter.updateList(state.articles)
            rvStories.makeVisible()
        } else {

            rvStories.makeInVisible()
        }

        if (state.isOffline) {
            if (state.articles.isEmpty()) {
                state.error?.let {
                    Snackbar.make(clParent, "No internet connection", Snackbar.LENGTH_LONG)
                        .setAction("CLOSE") { }
                        .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
                        .show()
                }
            }

        }

        swipeRefresh.isRefreshing = state.isRefreshing
    }

    override fun intents() = Observable.merge(
        initialIntentPublisher,
        swipeToRefreshIntent,
        loadFilteredStories,
        updateStoriesList
    )


    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvStories.layoutManager = linearLayoutManager
        rvStories.adapter = storiesAdapter
        storiesAdapter.setTopStoryClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            clBtmSectionBusiness.id -> {
                currentSection = FilterType.Business
                if (isNetworkConnected()) {
                    updateStoriesList.onNext(TopStoriesListIntent.UpdateFilteredStories(FilterType.Business))
                    loadFilteredStories.onNext(TopStoriesListIntent.LoadFilteredStories(FilterType.Business))
                } else
                    loadFilteredStories.onNext(
                        TopStoriesListIntent.LoadFilteredStories(
                            filterType = FilterType.Business,
                            offline = true
                        )
                    )
            }
            clBtmSectionMovie.id -> {
                currentSection = FilterType.Movies
                if (isNetworkConnected()) {
                    updateStoriesList.onNext(TopStoriesListIntent.UpdateFilteredStories(FilterType.Movies))
                    loadFilteredStories.onNext(TopStoriesListIntent.LoadFilteredStories(FilterType.Movies))
                } else
                    loadFilteredStories.onNext(
                        TopStoriesListIntent.LoadFilteredStories(
                            filterType = FilterType.Movies,
                            offline = true
                        )
                    )
            }
            clBtmSectionScience.id -> {
                currentSection = FilterType.Science
                if (isNetworkConnected()) {
                    updateStoriesList.onNext(TopStoriesListIntent.UpdateFilteredStories(FilterType.Science))
                    loadFilteredStories.onNext(TopStoriesListIntent.LoadFilteredStories(FilterType.Science))
                } else
                    loadFilteredStories.onNext(
                        TopStoriesListIntent.LoadFilteredStories(
                            filterType = FilterType.Science,
                            offline = true
                        )
                    )
            }
            clBtmSectionWorld.id -> {
                currentSection = FilterType.World
                if (isNetworkConnected()) {
                    updateStoriesList.onNext(TopStoriesListIntent.UpdateFilteredStories(FilterType.World))
                    loadFilteredStories.onNext(TopStoriesListIntent.LoadFilteredStories(FilterType.World))
                } else
                    loadFilteredStories.onNext(
                        TopStoriesListIntent.LoadFilteredStories(
                            filterType = FilterType.World,
                            offline = true
                        )
                    )
            }
        }
    }

    override fun onTopStoryClicked(title:String) {
        startActivity(TopStoriesDetailsActivity.newInstance(this,title))
    }
}
