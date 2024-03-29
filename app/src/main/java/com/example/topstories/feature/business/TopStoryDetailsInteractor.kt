package com.example.topstories.feature.business

import android.util.Log
import com.example.topstories.feature.repo.TopStoriesRepo
import com.example.topstories.feature.ui.topStoriesDetails.TopStoriesDetailsAction
import com.example.topstories.feature.ui.topStoriesDetails.TopStoriesDetailsResult
import com.example.topstories.mvibase.MviInteractor
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import javax.inject.Inject


class TopStoryDetailsInteractor @Inject constructor(val repo: TopStoriesRepo) :
    MviInteractor<TopStoriesDetailsAction, TopStoriesDetailsResult> {

    override val actionProcessor =
        ObservableTransformer<TopStoriesDetailsAction, TopStoriesDetailsResult> { actions ->
            actions.publish { selector ->
                selector.ofType(TopStoriesDetailsAction.LoadStoryDetailAction::class.java).compose(loadTopStories)

            }
        }


    private val loadTopStories =
        ObservableTransformer<TopStoriesDetailsAction.LoadStoryDetailAction, TopStoriesDetailsResult> { actions ->
            actions.flatMap { action ->
                repo.getArticleByTitle(action.name!!)
                    .map { TopStoriesDetailsResult.LoadTopStoryDetailsResult.Success(it) }
                    .cast(TopStoriesDetailsResult::class.java)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

            }
        }


}