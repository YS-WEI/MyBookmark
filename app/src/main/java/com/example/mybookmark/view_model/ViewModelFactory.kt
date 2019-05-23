package com.example.mybookmark.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mybookmark.MarkDatebaseRepository

class ViewModelFactory constructor(private val repository: MarkDatebaseRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarkViewModel::class.java)) {
            return MarkViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(WebViewModel::class.java)) {
            return WebViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}