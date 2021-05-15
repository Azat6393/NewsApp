package com.azatberdimyradov.newsapp.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.azatberdimyradov.newsapp.api.NewsAPI
import com.azatberdimyradov.newsapp.db.ArticleDatabase
import com.azatberdimyradov.newsapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsAPI =
        retrofit.create(NewsAPI::class.java)

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: ArticleDatabase.CallBack
    ) = Room.databaseBuilder(app, ArticleDatabase::class.java, "article_db.db")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    @Singleton
    fun provideArticleDao(db: ArticleDatabase) = db.getArticleDao()

}