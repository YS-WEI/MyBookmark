package com.siang.wei.mybookmark.view_model

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.siang.wei.mybookmark.model.ParserProgress
import com.siang.wei.mybookmark.model.WebType
import com.siang.wei.mybookmark.parser.WebParserUtils
import com.siang.wei.mybookmark.parser.webs.DuzhezWebParser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class EpisodeViewModel : ViewModel() {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var imagesLiveData: MutableLiveData<ArrayList<String>> = MutableLiveData()
    private var progressDialogLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var parserProgressLiveData: MutableLiveData<ParserProgress> = MutableLiveData()
    fun getProgressDialogLiveData(): LiveData<Boolean> {
        return progressDialogLiveData
    }

    fun getImagesLiveData(): LiveData<ArrayList<String>> {
        return imagesLiveData
    }

    fun getParserProgressLiveData(): LiveData<ParserProgress> {
        return parserProgressLiveData
    }

    fun parseAllImage(url:String, context: Context? = null) {
        val type = getWebType(url)

        when(type) {
            WebType.duzhez -> {
                if(context != null) {
                    parseAllImageForDuzhez(url, context!!)
                }
            }
            else -> {

                progressDialogLiveData.value = true
                var disposable = WebParserUtils.startParseImage(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ list ->
                        Log.d("startParseImage", "${list}")

                        imagesLiveData.value = list
                        progressDialogLiveData.value = false
                    },
                        { error ->
                            Log.e("parser", "parser fail", error)
                            progressDialogLiveData.value = false
                        })

                compositeDisposable.add(disposable)

            }
        }

    }

    fun parseAllImageForDuzhez(url:String, context: Context) {
        val parserUtil = DuzhezWebParser()

        progressDialogLiveData.value = true
        parserUtil.getTotalPageCount(url).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ count ->

                if(count != null  && count > 0) {
                    val parserProgress = ParserProgress(count, 0)
                    parserProgressLiveData.value = parserProgress
                    startParseAllImageForDuzhez(context, url, count, parserUtil)
                } else {
                    progressDialogLiveData.value = false
                }
            },
            { error ->
                Log.e("parser", "parser fail", error)
                progressDialogLiveData.value = false
            })
    }

    fun startParseAllImageForDuzhez(context: Context, url:String, totalCount: Int, parser: DuzhezWebParser) {

        parser.parseEpisodeImages(context, url, totalCount)
            .subscribe({ list ->

                val parserProgress = ParserProgress(totalCount, list.size)
                parserProgressLiveData.value = parserProgress
                if(list.size >= totalCount) {

                    imagesLiveData.value = list
                    progressDialogLiveData.value = false
                }
            },
                { error ->
                    Log.e("parser", "parser fail", error)
                    progressDialogLiveData.value = false
                })
    }

    fun getWebType(url: String): WebType? {
        val uri = Uri.parse(url)
        return WebType.domainOfEnum(uri.host)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}