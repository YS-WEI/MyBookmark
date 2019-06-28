//package com.siang.wei.mybookmark.parser.service
//
//import android.app.IntentService
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.os.IBinder
//import android.util.Log
//import com.siang.wei.mybookmark.db.AppDatabase
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
//import android.text.TextUtils
//import com.siang.wei.mybookmark.db.EpisodePagesDao
//import com.siang.wei.mybookmark.db.model.EpisodePages
//import com.siang.wei.mybookmark.model.ParserProgress
//import com.siang.wei.mybookmark.parser.webs.DuzhezWebParser
//import io.reactivex.disposables.Disposable
//import kotlin.collections.ArrayList
//
//
////Deprecated
//class DuzhezWebParseService: Service() {
//
//
//    companion object {
//
//        val EXTRA_INPUT_URL = "EXTRA_INPUT_URL"
//
//        const val SERVICE_NAME = "DuzhezWebParseService"
//        val ACTION_RETURN_FINISH = "$SERVICE_NAME.action_return_finish"
//        val ACTION_RETURN_ERROR = "$SERVICE_NAME.action_return_error"
//        val ACTION_RETURN_UPDATE = "$SERVICE_NAME.action_return_update"
//
//        val EXTRA_TOTLE_SIZE = "EXTRA_TOTLE_SIZE"
//        val EXTRA_CURRENT_NUMBER = "EXTRA_CURRENT_NUMBER"
//        val EXTRA_DATA = "EXTRA_DATA"
//
//        val EXTRA_ERROR = "EXTRA_ERROR"
//    }
//    private var total: Int = 0
//    private var url = ""
//    private var episodePagesDao: EpisodePagesDao? = null
//    private var episodePagesByDB : EpisodePages? = null
//    private var listByDB : List<String>? = null
//
//    private var countSubscription: Disposable? = null
//    private var parserSubscription: Disposable? = null
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//
//        if( intent != null && intent!!.hasExtra(EXTRA_INPUT_URL)) {
//            url = intent.getStringExtra(EXTRA_INPUT_URL)
//        }
//
//        if(!TextUtils.isEmpty(url)) {
//            val database = AppDatabase.getInstance(this)
//            episodePagesDao = database.episodePagesDao()
//
////            episodePagesByDB = episodePagesDao!!.getEpisodePages(url);
//            episodePagesDao!!.getEpisodePages(url)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ list ->
//                    if(list.size != 0) {
//                        episodePagesByDB = list[0]
//                    } else {
//                        episodePagesByDB = null
//                    }
//
//
//                    if(episodePagesByDB != null && !TextUtils.isEmpty(episodePagesByDB!!.content)) {
//                        var content = episodePagesByDB!!.content
//                        content = content.replace("[", "")
//                        content = content.replace("]", "")
//
//
//                        listByDB = content.split(",").map { it.trim() }
//                    }
//                    parseAllImageForDuzhez(url, this)
//
//                },
//                { error ->
//                    Log.e("getEpisodePages", "getEpisodePages fail", error)
//                    errorSevice("parser Image is fail and end")
//                    this.onDestroy()
//            });
//
//
//
//        } else {
//            errorSevice("not url")
//            this.onDestroy()
//        }
//
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    override fun onDestroy() {
//        if(countSubscription != null) {
//            countSubscription!!.dispose()
//        }
//
//        if(parserSubscription != null) {
//            parserSubscription!!.dispose()
//        }
//        super.onDestroy()
//
//        Log.e(SERVICE_NAME, "onDestroy")
//    }
//
//    fun parseAllImageForDuzhez(url:String, context: Context) {
//        val parserUtil = DuzhezWebParser()
//
//
//        countSubscription = parserUtil.getTotalPageCount(url).subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ count ->
//
//                if(count != null  && count > 0) {
//                    this.total = count
//                    val parserProgress = ParserProgress(count, 0)
////                    parserProgressLiveData.value = parserProgress
//                    updateSevice(0, this.total)
//                    startParseAllImageForDuzhez(context, url, count, parserUtil)
//                } else {
//                    errorSevice("Parser count is 0")
//                    this.onDestroy()
//                }
//            },
//                { error ->
//                    Log.e("parser", "parser fail", error)
//                    errorSevice("parser fail count is fail")
//                    this.onDestroy()
//                })
//    }
//
//    fun startParseAllImageForDuzhez(context: Context, url:String, totalCount: Int, parser: DuzhezWebParser) {
//
//        parserSubscription = parser.parseEpisodeImages(context, url, totalCount, listByDB)
//            .subscribe({ list ->
//                endSaveList(list)
//                if(list.size >= totalCount) {
//                    finishSevice(list.size, this.total, list)
//                    this.onDestroy()
//                } else {
//                    updateSevice(list.size, this.total, list)
//                }
//            },
//            { error ->
//                Log.e("parser", "parser fail", error)
//                errorSevice("parser Image is fail and end")
//                this.onDestroy()
//            })
//    }
//
//    fun endSaveList (list: ArrayList<String>) {
//       if(episodePagesDao != null ) {
//
//           if(episodePagesByDB != null) {
//               val episodePages = episodePagesByDB!!.copy(content = list.toString())
//               episodePagesDao!!.update(episodePages)
//                   .subscribeOn(Schedulers.io())
//                   .observeOn(AndroidSchedulers.mainThread())
//                   .subscribe({
//                       episodePagesByDB = episodePages
//                   },
//                       { error -> Log.e("update", "update episode pages fail ", error) })
//           } else {
//               val episodePages = EpisodePages(pageUrl = url, content = list.toString())
//               episodePagesDao!!.insert(episodePages)
//                   .subscribeOn(Schedulers.io())
//                   .observeOn(AndroidSchedulers.mainThread())
//                   .subscribe({
//                       episodePagesByDB = episodePages
//                   },
//                       { error -> Log.e("insert", "insert episode pages fail ", error) })
//           }
//
//       }
//
//
//    }
//
//    private fun updateSevice( count: Int, total: Int, data: ArrayList<String>? = null) {
//        val intent = Intent(ACTION_RETURN_UPDATE)
//
//        intent.putExtra(EXTRA_TOTLE_SIZE, total)
//        intent.putExtra(EXTRA_CURRENT_NUMBER, count)
//        intent.putStringArrayListExtra(EXTRA_DATA, data)
//        sendBroadcast(intent)
//    }
//
//    private fun errorSevice(message: String) {
//        val intent = Intent(ACTION_RETURN_ERROR)
//        intent.putExtra(EXTRA_ERROR, message)
//        sendBroadcast(intent)
//    }
//
//
//    private fun finishSevice(count: Int, total: Int, data: ArrayList<String>) {
//        val intent = Intent(ACTION_RETURN_FINISH)
//
//        intent.putExtra(EXTRA_TOTLE_SIZE, total)
//        intent.putExtra(EXTRA_CURRENT_NUMBER, count)
//        intent.putStringArrayListExtra(EXTRA_DATA, data)
//        sendBroadcast(intent)
//    }
//}