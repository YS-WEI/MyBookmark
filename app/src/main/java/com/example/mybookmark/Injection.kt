package com.example.mybookmark

import android.content.Context
import com.example.mybookmark.db.AppDatabase
import com.example.mybookmark.view_model.MarkViewModelFactory

object Injection {

    fun provideMarkDatebaseRepository(context: Context): MarkDatebaseRepository {

        val database = AppDatabase.getInstance(context);
        val repository = MarkDatebaseRepository(database.markDao());
        return repository;
    }

    fun provideMarkViewModelFactory(context: Context): MarkViewModelFactory {
        val dataSource = provideMarkDatebaseRepository(context)
        return MarkViewModelFactory(dataSource)
    }

}