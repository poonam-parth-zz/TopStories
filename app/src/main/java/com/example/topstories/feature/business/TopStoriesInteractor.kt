package com.example.topstories.feature.business


import android.util.Log
import com.example.topstories.feature.repo.TopStoriesRepo
import com.example.topstories.feature.ui.topStoriesList.FilterType
import com.example.topstories.feature.ui.topStoriesList.TopStoriesListAction
import com.example.topstories.feature.ui.topStoriesList.TopStoriesListResult
import com.example.topstories.feature.ui.topStoriesList.TopStoriesListResult.LoadTopStoriesResult
import com.example.topstories.mvibase.MviInteractor
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TopStoriesInteractor @Inject constructor(val repo: TopStoriesRepo) :
    MviInteractor<TopStoriesListAction, TopStoriesListResult> {

    override val actionProcessor =
        ObservableTransformer<TopStoriesListAction, TopStoriesListResult> { actions ->
            actions.publish { selector ->
                Observable.merge(
                    selector.ofType(TopStoriesListAction.LoadStoriesListAction::class.java).compose(
                        loadTopStories
                    )
                        .doOnNext { result ->
                            Log.d("result:", "$result")
                        },
                    selector.ofType(TopStoriesListAction.UpdateStoriesListAction::class.java).compose(
                        updateTopStories
                    )
                        .doOnNext { result ->
                            Log.d("result:", "$result")
                        }
                )
            }
        }

    private val loadTopStories =
        ObservableTransformer<TopStoriesListAction.LoadStoriesListAction, TopStoriesListResult> { actions ->
            actions.flatMap { action ->
                if (!action.offline) {
                    repo.loadTopStoriesNY(getTypeFrmFilterType(action.filterType))
                        .andThen(
                            Observable.just(LoadTopStoriesResult.Success(action.filterType))
                        )
                        .cast(LoadTopStoriesResult::class.java)
                        .onErrorReturn { LoadTopStoriesResult.Failure(it) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                } else {
                    repo.getArticlesFrmDB()
                        .map { LoadTopStoriesResult.Offline(it,action.filterType) }
                        .cast(LoadTopStoriesResult::class.java)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                }

            }
        }

    fun getTypeFrmFilterType(filterType: FilterType): String {
        return when (filterType) {
            FilterType.Business -> "business"
            FilterType.Movies -> "movies"
            FilterType.World -> "world"
            FilterType.Science -> "science"
        }
    }

    private val updateTopStories =
        ObservableTransformer<TopStoriesListAction.UpdateStoriesListAction, TopStoriesListResult> { actions ->
            actions.flatMap { action ->

                repo.getArticlesFrmDB()
                    .map { TopStoriesListResult.UpdateTopStoriesListResult.Success(it.filter { it.section==getTypeFrmFilterType(action.filterType) },action.filterType)}
                    .cast(TopStoriesListResult.UpdateTopStoriesListResult::class.java)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

            }
        }


}