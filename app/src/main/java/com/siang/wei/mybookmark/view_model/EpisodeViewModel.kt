package com.siang.wei.mybookmark.view_model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.siang.wei.mybookmark.model.ParserProgress
import com.siang.wei.mybookmark.model.WebType
import com.siang.wei.mybookmark.parser.WebParserUtils
import com.siang.wei.mybookmark.parser.webs.DuzhezWebParser
import com.siang.wei.mybookmark.parser.webs.WebEpisodeParser
import com.siang.wei.mybookmark.parser.webs.WebParser
import com.siang.wei.mybookmark.parser.webs.WuyoyhuiWebParser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class EpisodeViewModel : ViewModel() {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var imagesLiveData: MutableLiveData<ArrayList<String>> = MutableLiveData()
    private var progressDialogLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var parserProgressLiveData: MutableLiveData<ParserProgress> = MutableLiveData()

    private var serviceIntent: Intent? = null
    fun getProgressDialogLiveData(): LiveData<Boolean> {
        return progressDialogLiveData
    }

    fun getImagesLiveData(): LiveData<ArrayList<String>> {
        return imagesLiveData
    }

    fun getParserProgressLiveData(): LiveData<ParserProgress> {
        return parserProgressLiveData
    }

    fun getServiceIntent(): Intent? {
        return serviceIntent
    }

    fun parseAllImage(url:String, context: Context? = null) {
        val type = getWebType(url)

        when(type) {
            WebType.duzhez -> {
                if(context != null) {
                    val parser = DuzhezWebParser()
                    parseAllImage(parser, url, context)
                }
            }
            WebType.wuyouhui -> {
                if(context != null) {
                    val parser = WuyoyhuiWebParser()
                    parseAllImage(parser, url, context)
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

    fun parseAllImage(parser: WebEpisodeParser, url:String, context: Context) {


        progressDialogLiveData.value = true
        val disposable =  parser.parseEpisodeImages(context, url)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->

                if(list.size > 0) {
                    imagesLiveData.value = list

                    val parserProgress = ParserProgress(total = list.size, isFinish =  true)
                    parserProgressLiveData.value = parserProgress
                    progressDialogLiveData.value = false
                }
            },
                { error ->
                    Log.e("parser", "parser fail", error)
                    var parserProgress = parserProgressLiveData.value
                    if( parserProgress != null) {
                        parserProgress = parserProgress.copy(isError = true, isFinish = false)
                    } else {
                        parserProgress = ParserProgress(isError = true, isFinish = false)
                    }

                    parserProgressLiveData.value = parserProgress
                    progressDialogLiveData.value = false
                })

        compositeDisposable.add(disposable)
    }


    fun getWebType(url: String): WebType? {
        val uri = Uri.parse(url)
        return WebType.domainOfEnum(uri.host)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()

    }

//    private fun registerReceiver(context: Context)
//    {
//        val filter = IntentFilter()
//        filter.addAction(DuzhezWebParseService.ACTION_RETURN_FINISH);
//        filter.addAction(DuzhezWebParseService.ACTION_RETURN_UPDATE);
//        filter.addAction(DuzhezWebParseService.ACTION_RETURN_ERROR);
//        context.registerReceiver(syncServiceState, filter);
//    }
//
//    fun closeService(context: Context) {
//
//        if(serviceIntent != null) {
//            context.stopService(serviceIntent)
//        }
//    }
//
//    fun unregisterReceiver(context: Context)
//    {
//        context.unregisterReceiver(syncServiceState);
//    }
//
//    val syncServiceState = object : BroadcastReceiver() {
//
//        override fun onReceive(context: Context, intent: Intent) {
//            val action = intent.action
//
//            if (action == DuzhezWebParseService.ACTION_RETURN_ERROR) {
//                var message = ""
//                if(intent.hasExtra(DuzhezWebParseService.EXTRA_ERROR)) {
//                    message = intent.getStringExtra(DuzhezWebParseService.EXTRA_ERROR)
//                }
//
//                val parserProgress = ParserProgress(0, 0, false, true, message)
//                parserProgressLiveData.value = parserProgress
//            } else {
//                var totle = 0
//                var current = 0
//                var data: java.util.ArrayList<String>? = null
//                if(intent.hasExtra(DuzhezWebParseService.EXTRA_CURRENT_NUMBER)) {
//                    current = intent.getIntExtra(DuzhezWebParseService.EXTRA_CURRENT_NUMBER, 0)
//                }
//
//                if(intent.hasExtra(DuzhezWebParseService.EXTRA_TOTLE_SIZE)) {
//                    totle = intent.getIntExtra(DuzhezWebParseService.EXTRA_TOTLE_SIZE, 0)
//                }
//
//                if(intent.hasExtra(DuzhezWebParseService.EXTRA_DATA)) {
//                    data = intent.getStringArrayListExtra(DuzhezWebParseService.EXTRA_DATA)
//                }
//
//
//                if (action == DuzhezWebParseService.ACTION_RETURN_FINISH) {
//                    val parserProgress = ParserProgress(totle, current, true)
//                    parserProgressLiveData.value = parserProgress
//                } else if (action == DuzhezWebParseService.ACTION_RETURN_UPDATE) {
//                    val parserProgress = ParserProgress(totle, current, false)
//                    parserProgressLiveData.value = parserProgress
//                }
//
//                if(data != null) {
//                    imagesLiveData.value = data
//                }
//            }
//
//        }
//    }

}