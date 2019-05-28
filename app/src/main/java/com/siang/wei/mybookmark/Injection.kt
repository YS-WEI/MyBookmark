package com.siang.wei.mybookmark

import android.content.Context
import com.siang.wei.mybookmark.db.AppDatabase
import com.siang.wei.mybookmark.view_model.ViewModelFactory

object Injection {

    fun provideMarkDatebaseRepository(context: Context): MarkDatebaseRepository {

        val database = AppDatabase.getInstance(context);
        val repository = MarkDatebaseRepository(database.markDao());
        return repository;
    }

    fun provideMarkViewModelFactory(context: Context): ViewModelFactory {
        val dataSource = provideMarkDatebaseRepository(context)
        return ViewModelFactory(dataSource)
    }

}