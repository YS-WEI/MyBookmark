package com.example.mybookmark.view_model

import android.net.Uri
import android.text.TextUtils
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
        var mark: Mark? = markData.value ?: return
        //        Log.d("nextUrl", "url: ${url}")
//        Log.d("nextUrl", "pathSegments: ${uri.pathSegments}")
//        Log.d("nextUrl", "path: ${uri.path}")
//
        //                episode.url.equals(url, true)
        if(episodesData.value != null) {
           val episode = episodesData.value!!.filter { episode ->
                equalsUrl(mark!!.url, episode.url, url)
            }.single()



            if(episode != null) {

                mark!!.readEpisode = episode.title
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

    private fun equalsUrl(rootUrl: String, episodeUrl: String?, currentUrl: String): Boolean {
        if(TextUtils.isEmpty(episodeUrl)) {
            return false
        }

        val url = combinUrl(rootUrl, episodeUrl!!)

        return url.equals(currentUrl, true)

    }

    private fun combinUrl(url: String, addUrl: String): String {
        var uri = Uri.parse(url)
        var addUri = Uri.parse(addUrl)

        var newUrl = url

        if(TextUtils.isEmpty(addUri.host) || uri.host.equals(addUri.host, true)) {
            var pathSegments = uri.pathSegments as ArrayList<String>

            addUri.pathSegments.forEach {addSeg ->
                val match = uri.pathSegments.find { seg ->
                    addSeg.equals(seg)
                }
                if(match == null) {
                    newUrl += addSeg
                }
            }

        }


        return newUrl

    }
}