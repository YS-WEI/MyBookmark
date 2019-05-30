package com.siang.wei.mybookmark.view_model

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.siang.wei.mybookmark.MarkDatebaseRepository
import com.siang.wei.mybookmark.db.model.Episode
import com.siang.wei.mybookmark.db.model.Mark
import com.siang.wei.mybookmark.parser.WebParserUtils
import com.siang.wei.mybookmark.util.ShareFun
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.disposables.CompositeDisposable


class EpisodeViewModel constructor(repository: MarkDatebaseRepository) : ViewModel() {

    private val repository: MarkDatebaseRepository = repository

    private var compositeDisposable:CompositeDisposable = CompositeDisposable()

    private var imagesLiveData: MutableLiveData<List<String>> = MutableLiveData()


    fun getImagesLiveData(): LiveData<List<String>> {
        return imagesLiveData
    }

    fun parseAllImage(url:String) {

        WebParserUtils.startParseImage(url).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    list ->
                Log.d("startParseImage", "${list}")

                imagesLiveData.value = list

            },
                { error -> Log.e("parser", "parser fail", error) })
    }


}