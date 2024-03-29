package com.example.topstories.di.module

import android.content.Context
import com.example.topstories.BuildConfig
import com.example.topstories.api.ApiInterface
import com.example.topstories.utils.AppRxSchedulers
import com.example.topstories.utils.Constants.Companion.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttpCache(context: Context): Cache = Cache(context.cacheDir, (10 * 1024 * 1024).toLong())

    @Provides
    @Singleton
    fun provideAPI(retrofit: Retrofit): ApiInterface = retrofit.create(ApiInterface::class.java)


    @Provides
    @Singleton
    fun provideRetrofit(
        gson: Gson,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        cache: Cache,
        context: Context
    ): Retrofit {

        val okHttpClient: OkHttpClient
        if (BuildConfig.DEBUG) {
            okHttpClient = OkHttpClient()
                .newBuilder()
                .cache(null) // Cache disabled for testing no internet condition
                // .cache(cache)
                .connectTimeout(78000, TimeUnit.MILLISECONDS)
                .readTimeout(78000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .writeTimeout(78000, TimeUnit.MILLISECONDS)
                .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                .addInterceptor(httpLoggingInterceptor)
                .build()
        } else {
            okHttpClient = OkHttpClient()
                .newBuilder()
                .cache(null)  // Cache disabled for testing no internet condition
                // .cache(cache)
                .connectTimeout(78000, TimeUnit.MILLISECONDS)
                .readTimeout(78000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .writeTimeout(78000, TimeUnit.MILLISECONDS)
                .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                .build()
        }

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun httpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    fun provideRxSchedulers(): AppRxSchedulers {
        return AppRxSchedulers()
    }
}