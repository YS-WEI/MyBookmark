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


class WebViewModel constructor(repository: MarkDatebaseRepository) : ViewModel() {

    private val repository: MarkDatebaseRepository = repository

    private var compositeDisposable:CompositeDisposable = CompositeDisposable()
    private var markData: MutableLiveData<Mark> = MutableLiveData()

    private var episodesData : MutableLiveData<ArrayList<Episode>> = MutableLiveData()

    private var progressDialogLiveData: MutableLiveData<Boolean> = MutableLiveData()

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

    fun getProgressDialogLiveData(): LiveData<Boolean> {
        return progressDialogLiveData
    }

    override fun onCleared() {
        super.onCleared()

    }

    fun parserWebEpisode() {
        if(episodesData.value != null) {
            return
        }


        val mark = markData.value
        if(mark != null) {
            val url = mark.url
            progressDialogLiveData.value = true
            WebParserUtils.startParseEpisode(url).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({list ->
                    episodesData.value = list
                    progressDialogLiveData.value = false
                }, {
                        error -> Log.e("Parser Episode", "", error)
                        progressDialogLiveData.value = false
                })
        }
    }

    fun nextUrl(url: String): Episode? {
        var mark: Mark? = markData.value ?: return null

        if(episodesData.value != null) {
           val episode: Episode? = episodesData.value!!.filter { episode ->
                equalsUrl(mark!!.url, episode.url, url)
            }.singleOrNull()



            if(episode != null) {

                mark!!.readEpisode = episode.title
                mark!!.lastTimeDate = ShareFun.getDate()
                updateMark(mark)
                return episode
            }

        }
        return null
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

        val url = ShareFun.combinUrl(rootUrl, episodeUrl!!)

        return url.equals(currentUrl, true)

    }


}