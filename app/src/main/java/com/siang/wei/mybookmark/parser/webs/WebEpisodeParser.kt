package com.siang.wei.mybookmark.parser.webs

import android.content.Context
import io.reactivex.Observable

abstract class WebEpisodeParser: WebParser() {
    abstract fun parseEpisodeImages(context: Context, url: String): Observable<ArrayList<String>>
}