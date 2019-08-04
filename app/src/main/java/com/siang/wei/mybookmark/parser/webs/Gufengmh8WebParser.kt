package com.siang.wei.mybookmark.parser.webs

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.siang.wei.mybookmark.db.model.Episode
import com.siang.wei.mybookmark.db.model.EpisodeImageData
import com.siang.wei.mybookmark.db.model.Mark
import com.siang.wei.mybookmark.model.WebType
import com.siang.wei.mybookmark.parser.WebParserUtils
import com.siang.wei.mybookmark.util.ShareFun
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import java.lang.Exception

class Gufengmh8WebParser: WebParser(){

    override fun information(doc: Document, mark: Mark): Mark? {

        var title: String = ""

        val viewSubElements = doc.getElementsByClass("view-sub")
        //name
        if(viewSubElements != null) {
            val titleElements = viewSubElements.select("h1")
            if(titleElements != null && titleElements.size > 0)
            title = titleElements[0].text()


            val detailedElements = viewSubElements.select(".pic")

            if(detailedElements != null) {
                val itemElement= detailedElements.select("dl")
                if(itemElement != null)
                for (element in itemElement) {
                    val dtElement= element.select("dt")
                    val ddElement = element.select("dd")
                    if(dtElement != null && ddElement != null) {
                        val string = element.text()
                        if (string.indexOf("更新于：") != -1) {
                            var updateDate = WebParserUtils.parserIntFormString(ddElement.text())
                            if (updateDate != null) {
                                mark.updateDate = "$updateDate"
                            }
                        }

                        if (string.indexOf("更新至：") != -1) {
                            val totalEpisode = ddElement.text()
                            mark.totalEpisode = totalEpisode.trim()
                        }
                    }
                }
            }
        }

        val imageElement = doc.getElementById("Cover")
        if (imageElement != null) {
            val imgUrlElement = imageElement.select("mip-img")
            if(imgUrlElement != null) {
                val imgAlt = imgUrlElement.attr("alt")
                mark.image = imgUrlElement.attr("src")
                if (TextUtils.isEmpty(title)) {
                    title = imgAlt
                }
            }

        }
        if(!TextUtils.isEmpty(title)) {
            mark.name = title
        }

        mark.type = WebType.gufengmh8.domain

        return mark
    }


    override fun episode(doc: Document): ArrayList<Episode>? {
        val listBlockElement = doc.getElementById("list_block") ?: return null
        val listElements = listBlockElement.select(".list")
        if(listElements != null) {
            var episodeList = ArrayList<Episode>()
            listElements.forEach { element ->
                parseList(element, episodeList)
            }

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