package com.siang.wei.mybookmark.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.siang.wei.mybookmark.MarkDatebaseRepository
import com.siang.wei.mybookmark.db.DatabaseKeys
import com.siang.wei.mybookmark.db.model.BackupDatabase
import com.siang.wei.mybookmark.db.model.Mark
import com.siang.wei.mybookmark.parser.WebParserUtils
import com.siang.wei.mybookmark.util.BackupUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.disposables.CompositeDisposable


class MarkViewModel constructor(repository: MarkDatebaseRepository) : ViewModel() {

    private val repository: MarkDatebaseRepository = repository

    private var compositeDisposable:CompositeDisposable = CompositeDisposable()
    private var marksLiveData: MutableLiveData<List<Mark>> = MutableLiveData()
    private var showMessageLiveData: MutableLiveData<String> = MutableLiveData()
    private var progressDialogLiveData: MutableLiveData<Boolean> = MutableLiveData()

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

    fun getShowMessageLiveData(): LiveData<String> {
        return showMessageLiveData
    }

    fun getProgressDialogLiveData(): LiveData<Boolean> {
        return progressDialogLiveData
    }



    fun addMark(mark: Mark) {

        repository.check(mark.url).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({list ->
                if(list.size > 0) {
                    showMessageLiveData.value = mark.url +"\n漫畫已經存在"
                } else {
                    startAddMark(mark)
                }

            }, {
                    error -> Log.e("check", "check fail", error)
            })


    }

    fun delMark(mark: Mark) {
        repository
        compositeDisposable.add(repository.delect(mark)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({},
                { error -> Log.e("addMark", "Unable to delect", error) }))
    }

    fun actionBackup() {
        if(marksLiveData.value != null) {
            val backupData = BackupDatabase(DatabaseKeys.Version, marksLiveData.value!!)
            progressDialogLiveData.value = true
            BackupUtil.exportDatabaseToJson(backupData, true).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ isSave ->
                    progressDialogLiveData.value = false
                }, { error ->
                    Log.e("ActionBackup", "Backup fail", error)
                    progressDialogLiveData.value = false
                })


        } else {
            showMessageLiveData.value = "Data base is not data"
        }


    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    private fun startAddMark (mark: Mark) {
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

    fun parser(mark: Mark) : Observable<Mark> {
        return Observable.create {
            subscriber ->

            WebParserUtils.startParserInfo(mark)
            if(mark != null ) {
                subscriber.onNext(mark)
                subscriber.onComplete()
            } else {
                subscriber.onError(error("parser fail"))
            }

        }

    }



    fun test(url:String) {

        WebParserUtils.startParseImage(url).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    list ->
                    Log.d("startParseImage", "${list}")

            },
                { error -> Log.e("parser", "parser fail", error) })
    }
}