package com.example.topstories.feature.repo

import androidx.lifecycle.MutableLiveData
import com.example.topstories.api.ApiInterface
import com.example.topstories.db.dao.TopStoriesDao
import com.example.topstories.db.entity.ArticleEntity
import com.example.topstories.feature.data.ArticleItem
import com.example.topstories.feature.mapper.getTopStoriesEntityFromResponse
import com.example.topstories.feature.mapper.getTopStoriesItemFromResponse
import com.example.topstories.feature.network.ArticleResponse
import com.example.topstories.utils.AppRxSchedulers
import com.example.topstories.utils.Constants
import io.reactivex.Completable
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class TopStoriesRepo @Inject constructor(
    val api: ApiInterface,
    val rxSchedulers: AppRxSchedulers,
    val topStoriesDao: TopStoriesDao
) {


    fun getTopStoriesNY() {

        api.getTopStoriesNY("arts")
            .subscribeOn(rxSchedulers.io())
            .subscribe(object : SingleObserver<ArticleResponse> {
                override fun onSuccess(t: ArticleResponse) {
                    val articleList: ArrayList<ArticleEntity> = ArrayList()
                    t.articleList.let {
                        for (item in it) {
                            articleList.add(getTopStoriesEntityFromResponse(item))
                        }
                            SaveArticleInDB(articleList)


                    }
                }

                override fun onSubscribe(d: Disposable) {}

                override fun onError(e: Throwable) {}

            })

    }


    fun getArticlesFrmDB(section : String = "arts") = topStoriesDao.getAllArticle(section)

    fun SaveArticleInDB(list: List<ArticleEntity>) {
        topStoriesDao.insertFetchedArticle(list)
    }


}