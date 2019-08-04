package com.siang.wei.mybookmark.parser.webs

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.siang.wei.mybookmark.db.model.Episode
import com.siang.wei.mybookmark.db.model.Mark
import com.siang.wei.mybookmark.model.WebType
import com.siang.wei.mybookmark.parser.service.BackgroundWeb
import com.siang.wei.mybookmark.parser.service.BackgroundWeb.*
import com.siang.wei.mybookmark.util.ShareFun
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import kotlin.collections.ArrayList
import com.siang.wei.mybookmark.parser.service.BackgroundWeb.DemoJavaScriptInterface as DemoJavaScriptInterface1

class DuzhezWebParser: WebEpisodeParser(){

    val defDateFormat = "yyyy-MM-dd HH:mm"

    override fun information(doc: Document, mark: Mark): Mark? {

        var title: String = ""
        val comicNameElement = doc.getElementById("comicName")
        if(comicNameElement != null) {
            title = comicNameElement.text()
        }

        val subElements = doc.getElementsByClass("sub_r")
        //name
        if(subElements != null) {
            subElements.forEach { subElement ->
                val dateElements = subElement.select(".date")
                if(dateElements != null) {
                    dateElements.forEach { element ->
                        val text = element.text()
                        val date = ShareFun.convertToDate(text, defDateFormat)
                        if(date != null) {
                            val dateString = ShareFun.convertDataBaseDateFormat(date.time)
                            mark.updateDate = dateString
                        } else {
                            val elements= element.select("a")
                            if(elements != null) {
                                elements.forEach { element ->
                                    mark.totalEpisode = element.text()
                                }
                            }
                        }
                    }
                }
            }
        }

        val imageElement = doc.getElementById("Cover")
        if (imageElement != null) {
            val imgUrlElement = imageElement.select("img")
            if(imgUrlElement != null) {
                val imgTitle = imgUrlElement.attr("title")
                mark.image = imgUrlElement.attr("src")

                if (TextUtils.isEmpty(title)) {
                    title = imgTitle
                }
            }

        }
        if(!TextUtils.isEmpty(title)) {
            mark.name = title
        }

        mark.type = WebType.duzhez.domain

        return mark
    }


    override fun episode(doc: Document): ArrayList<Episode>? {
        val listBlockElement = doc.getElementById("chapter-list-1") ?: return null
        if(listBlockElement != null) {
            var episodeList = ArrayList<Episode>()
            parseList(listBlockElement, episodeList)
            return episodeList
        }




        return null
    }

    private fun parseList(episodeElement: Element, episodeList: ArrayList<Episode>) {
        if (episodeElement != null) {
            val itemsElements = episodeElement.select("li")

            if(itemsElements != null && itemsElements.size > 0) {
                itemsElements.forEach { itemsElements ->

                    var episode = Episode()
                    var item = itemsElements.select("a")
                    episode.url = item.attr("href")

                    var titleElements = item.select("span")
                    episode.title = titleElements.text()
                    episodeList.add(episode)
                }

            }

        }
    }
//
//    fun getTotalPageCount(url: String, context: Context? = null): Observable<Int> {
//        return Observable.create {
//                subscriber ->
//            var doc: Document? = null
//
//            try {
//                doc = Jsoup.connect(url).get()
//            } catch (e: IOException) {
//                Log.d("runParserWeb", "", e)
//                subscriber.onError(e)
//
//            }
//            if(doc != null) {
//                val totalElement = doc.getElementById("total-page")
//                if(totalElement != null) {
//                    val total = totalElement.text().trim().toInt()
//
//                    if(total != null) {
//                        subscriber.onNext(total)
//                        subscriber.onComplete()
//
//                    } else {
//                        val throwable = Throwable("Can't get total page")
//                        subscriber.onError(throwable)
//                    }
//                } else {
//                    val throwable = Throwable("Can't get total page")
//                    subscriber.onError(throwable)
//                }
//            }
//        }
//    }

    private var rootUrl = ""

    override fun parseEpisodeImages(context: Context, url: String): Observable<ArrayList<String>> {
        this.rootUrl = url

        val imageList = ArrayList<String>()
        return Observable.create {
                subscriber ->
            parseEpisodeImages(context, imageList, subscriber)

        }
    }

    fun parseEpisodeImages(context: Context, imageList : ArrayList<String>,subscriber: ObservableEmitter<ArrayList<String>>) {
        var isBaseExist = false
        val backgroundWeb = BackgroundWeb()

        val url = rootUrl


        if(!isBaseExist) {
            backgroundWeb.init(context, url, object : CallbackListener {
                override fun parseImage(urlImages: List<String>) {
                    imageList.addAll(urlImages)


//                    currentIndex++
//                    if (currentIndex > totalCount) {
                    imageList.add("end_image")
                    subscriber.onNext(imageList)
                    subscriber.onComplete()
                    backgroundWeb.close(context);
//                    } else {
//                        parseEpisodeImages(context, imageList, baseList, subscriber)
//                    }


                }

                override fun error(error: String) {
                    val throwable = Throwable(error)
                    subscriber.onError(throwable)
                }
            })
        }

    }

//
//    fun parseEpisodeImages(context: Context, imageList : ArrayList<String>, baseList: List<String>? = null, subscriber: ObservableEmitter<ArrayList<String>>) {
//        var isBaseExist = false
//        val backgroundWeb = BackgroundWeb()
//
//        val url = rootUrl + "?p=${currentIndex}"
//        if(baseList != null &&  currentIndex <= baseList.size ) {
//            val urlImage =  baseList.get(currentIndex - 1)
//            if(!TextUtils.isEmpty(urlImage) && !urlImage.equals("error_image") && !urlImage.equals("end_image")) {
//                isBaseExist = true
//
//                imageList.add(urlImage)
//                subscriber.onNext(imageList)
//                currentIndex++
//
//                if (currentIndex > totalCount) {
//                    imageList.add("end_image")
//                    subscriber.onNext(imageList)
//                    subscriber.onComplete()
//                } else {
//                    parseEpisodeImages(context, imageList, baseList, subscriber)
//                }
//            }
//        }
//
//        if(!isBaseExist) {
//            backgroundWeb.init(context, url, object : CallbackListener {
//                override fun parseImage(urlImage: String) {
//                    Log.d("parseImage", urlImage)
//                    imageList.add(urlImage)
//                    subscriber.onNext(imageList)
//
//                    backgroundWeb.close(context)
//
//                    currentIndex++
//                    if (currentIndex > totalCount) {
//                        imageList.add("end_image")
//                        subscriber.onNext(imageList)
//                        subscriber.onComplete()
//                    } else {
//                        parseEpisodeImages(context, imageList, baseList, subscriber)
//                    }
//
//
//                }
//
//                override fun processHTML(string: String) {
////                parseEpisodeImages(context, url, string, imageList, subscriber)
//                }
//            })
//        }
//
//    }


}