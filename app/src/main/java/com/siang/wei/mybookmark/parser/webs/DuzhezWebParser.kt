package com.siang.wei.mybookmark.parser.webs

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.siang.wei.mybookmark.db.model.Episode
import com.siang.wei.mybookmark.db.model.EpisodeImageData
import com.siang.wei.mybookmark.db.model.Mark
import com.siang.wei.mybookmark.model.WebType
import com.siang.wei.mybookmark.util.ShareFun
import io.reactivex.ObservableEmitter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import java.lang.Exception
import java.util.*

class DuzhezWebParser: WebParser(){

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


    fun parseEpisodeImages(url: String, imageList : ArrayList<String>, subscriber: ObservableEmitter<ArrayList<String>>) {

        Log.d("parseEpisodeImages", url)
        var doc: Document? = null

        try {
            doc = Jsoup.connect(url).get()

        } catch (e: IOException) {
            Log.d("parseEpisodeImages", "", e)
            return
        }

        if(doc != null) {
            val xx = doc.toString();
            val episodeImageData = getImageAndNextUrl(doc)
            if(!TextUtils.isEmpty(episodeImageData.imageUrl)) {
                imageList.add(episodeImageData.imageUrl!!)
            } else {
                imageList.add("error_image")
            }

            if(!TextUtils.isEmpty(episodeImageData.nextUrl)) {
                val nextUrl = ShareFun.mergeUrl(url, episodeImageData.nextUrl!!)
                parseEpisodeImages(nextUrl, imageList, subscriber)
            } else {
                imageList.add("end_image")
            }

        }
    }

    private fun getImageAndNextUrl(doc: Document): EpisodeImageData {
        var imageUrl = ""
        var nextUrl = ""

        val contentElements = doc.getElementsByClass("chapter-content")
        if(contentElements != null) {

            contentElements.forEach { contentElement ->
                val linkElements = contentElement.select("a");

                linkElements.forEach { linkElement ->
                    val text = linkElement.text()
                    if(!TextUtils.isEmpty(text)) {
                        if(text.equals("下一页", true)) {

                            nextUrl = linkElement.attr("href")
                            try {
                                var uri = Uri.parse(nextUrl);
                                if(uri.path == null) {
                                    nextUrl = ""
                                }
                            } catch (e: Exception) {
                                nextUrl = ""
                            }
                        }
                    }

                    val imageElements = linkElement.select("img")

                    if(imageElements != null && imageElements.size == 1) {
                        imageUrl = imageElements.attr("src")
                    }

                }
            }
        }

        return EpisodeImageData(imageUrl, nextUrl)
    }

}