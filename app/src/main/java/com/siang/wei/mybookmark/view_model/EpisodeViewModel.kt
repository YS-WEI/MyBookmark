package com.siang.wei.mybookmark.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.siang.wei.mybookmark.parser.WebParserUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class EpisodeViewModel : ViewModel() {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var imagesLiveData: MutableLiveData<ArrayList<String>> = MutableLiveData()
    private var progressDialogLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getProgressDialogLiveData(): LiveData<Boolean> {
        return progressDialogLiveData
    }

    fun getImagesLiveData(): LiveData<ArrayList<String>> {
        return imagesLiveData
    }

    fun parseAllImage(url:String) {
        progressDialogLiveData.value = true
       var disposable = WebParserUtils.startParseImage(url).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    list ->
                Log.d("startParseImage", "${list}")

                imagesLiveData.value = list
                progressDialogLiveData.value = false
            },
                { error -> Log.e("parser", "parser fail", error)
                    progressDialogLiveData.value = false})

        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}