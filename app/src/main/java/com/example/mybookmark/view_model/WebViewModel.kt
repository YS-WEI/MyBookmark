package com.example.mybookmark.view_model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mybookmark.MarkDatebaseRepository
import com.example.mybookmark.db.model.Episode
import com.example.mybookmark.db.model.Mark
import com.example.mybookmark.parser.WebParserUtils
import com.example.mybookmark.util.ShareFun
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.disposables.CompositeDisposable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class WebViewModel constructor(repository: MarkDatebaseRepository) : ViewModel() {

    private val repository: MarkDatebaseRepository = repository

    private var compositeDisposable:CompositeDisposable = CompositeDisposable()
    private var markData: MutableLiveData<Mark> = MutableLiveData()

    private var episodesData : MutableLiveData<ArrayList<Episode>> = MutableLiveData()

    fun setMark(mark: Mark) {
        markData.value = mark
        parserWebEpisode()
    }

    fun getMarkData(): LiveData<Mark> {
        return markData
    }

    fun getEpisodesData(): LiveData<ArrayList<Episode>> {
        return episodesData
    }

    override fun onCleared() {
        super.onCleared()

    }

    fun parserWebEpisode() {
        val mark = markData.value
        if(mark != null) {
            val url = mark.url

            WebParserUtils.startParseEpisode(url).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({list ->
                    episodesData.value = list
                }, {
                        error -> Log.e("Parser Episode", "", error)
                })
        }
    }

    fun nextUrl(url: String) {
        val uri = Uri.parse(url)
//        Log.d("nextUrl", "url: ${url}")
//        Log.d("nextUrl", "pathSegments: ${uri.pathSegments}")
//        Log.d("nextUrl", "path: ${uri.path}")
//
        if(episodesData.value != null) {
           val episode = episodesData.value!!.filter { episode ->
                episode.url.equals(url, true)
            }.single()

            var mark = markData.value
            if(mark != null) {

                mark.readEpisode = episode.title
                mark!!.lastTimeDate = ShareFun.getDate()
                updateMark(mark)
            }

        }

    }

    fun updateMark(mark: Mark) {
        setMark(mark)
        repository.updateByNoWait(mark)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({},
                { error -> Log.e("updateMark", "Unable to update", error) })
    }

}