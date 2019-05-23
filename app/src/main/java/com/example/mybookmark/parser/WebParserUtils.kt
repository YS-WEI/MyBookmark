package com.example.mybookmark.parser

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.example.mybookmark.db.model.Episode
import com.example.mybookmark.db.model.Mark
import com.example.mybookmark.model.WebType
import com.example.mybookmark.parser.webs.TohomhWebParser
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.regex.Pattern

object WebParserUtils {
    fun startParserInfo(mark: Mark): Mark? {
        var doc: Document? = null

        try {
            doc = Jsoup.connect(mark.url).get()
        } catch (e: IOException) {
            Log.d("runParserWeb", "", e)
            return mark
        }

        if(doc == null) {
            return mark
        }

        val uri = Uri.parse(mark.url)
        val type = WebType.domainOfEnum(uri.host) ?: return null

        when(type) {
            WebType.tohomh123 -> run {
                val tohomhWebParser = TohomhWebParser()
                return tohomhWebParser.information(doc, mark)
            }
            WebType.soudongman -> return mark
            else -> return mark
        }

    }


    fun startParseEpisode(url: String): Observable<ArrayList<Episode>> {
        return Observable.create {
                subscriber -> parseEpisode(url, subscriber)
        }
    }

    private fun parseEpisode(url:String, subscriber: ObservableEmitter<ArrayList<Episode>>) {
        var doc: Document? = null

        try {
            doc = Jsoup.connect(url).get()
        } catch (e: IOException) {
            Log.d("runParserWeb", "", e)
            subscriber.onError(e)
        }

        if(doc == null) {

            val error = Throwable("Read Web doc is null")
            subscriber.onError(error)
        }

        val uri = Uri.parse(url)
        val type = WebType.domainOfEnum(uri.host) ?: run {
            val error = Throwable("Web Type error")
            subscriber.onError(error)
        }

        var list: ArrayList<Episode>? = null
        when(type) {
            WebType.tohomh123 -> run {
                val tohomhWebParser = TohomhWebParser()
                list = tohomhWebParser.episode(doc!!)
            }
            WebType.soudongman -> list = null
            else -> list = null
        }

        if(list != null) {
            subscriber.onNext(list!!)
            subscriber.onComplete()
        } else {
            val error = Throwable("parseEpisode list is null")
            subscriber.onError(error)
        }
    }



    fun parserIntFormString(string : String) :String {
        val pattern = Pattern.compile("\\d+")
        val matcher = pattern.matcher(string)
        var numberString = ""
        while (matcher.find()) {
            numberString += matcher.group()
        }

        return numberString;
    }



}