package com.example.mybookmark.view_model

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mybookmark.MarkDatebaseRepository
import com.example.mybookmark.db.model.Mark
import com.example.mybookmark.util.ParserWebContentUtils
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.disposables.CompositeDisposable
import org.jsoup.Jsoup
import java.io.IOException


class MarkViewModel constructor(repository: MarkDatebaseRepository) : ViewModel() {

    private val repository: MarkDatebaseRepository = repository

    private var compositeDisposable:CompositeDisposable = CompositeDisposable()
    private var marksLiveData: MutableLiveData<List<Mark>> = MutableLiveData()

    fun loadMadks() {
        val disposable = repository.getMarkAll()
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(this::onFetched, this::onError);
        compositeDisposable.add(disposable)
    }


    private fun onError(throwable: Throwable) {
        Log.d("MarkViewModel", throwable.message)
    }

    private fun onFetched(marks: List<Mark>) {
        marksLiveData.setValue(marks)
    }

    fun getMarksLiveData(): LiveData<List<Mark>> {
        return marksLiveData
    }

    fun addMark(url: String) {

        var mark: Mark = Mark(url=url)
        parser(mark).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                markUpdate ->

                compositeDisposable.add(repository.insert(markUpdate)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({},
                        { error -> Log.e("addMark", "Unable to insert", error) }))
            },
                { error -> Log.e("parser", "parser fail", error) })



    }

    fun delMark(mark: Mark) {

        compositeDisposable.add(repository.delect(mark)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({},
                { error -> Log.e("addMark", "Unable to delect", error) }))
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun parser(mark: Mark) : Observable<Mark> {
        return Observable.create {
            subscriber ->

            ParserWebContentUtils.startParser(mark)
            if(mark != null ) {
                subscriber.onNext(mark)
                subscriber.onComplete()
            } else {
                subscriber.onError(error("parser fail"))
            }

        }

    }

}