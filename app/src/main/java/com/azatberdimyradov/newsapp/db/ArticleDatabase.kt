package com.azatberdimyradov.newsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.azatberdimyradov.newsapp.model.Article
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)
abstract class ArticleDatabase: RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    class CallBack @Inject constructor(
        private val database: Provider<ArticleDatabase>
    ): RoomDatabase.Callback()
}