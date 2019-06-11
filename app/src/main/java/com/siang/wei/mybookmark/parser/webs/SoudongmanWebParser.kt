package com.siang.wei.mybookmark.parser.webs

import android.text.TextUtils
import android.util.Log
import com.siang.wei.mybookmark.db.model.Episode
import com.siang.wei.mybookmark.db.model.Mark
import com.siang.wei.mybookmark.model.WebType
import com.siang.wei.mybookmark.util.ShareFun
import org.jsoup.nodes.Document

class SoudongmanWebParser: WebParser(){

    override fun information(doc: Document, mark: Mark): Mark? {

        var title: String? = ""

        val nameElements = doc.getElementsByClass("name")
        if(nameElements != null) {
            title = nameElements.text()
        }


        val imagMap = imageParser(doc, "cover-bg")
        val imagMap_2 = imageParser(doc, "thumbnail")

        val imageUrl = imagMap.get("url")
        val imageUrl_2 = imagMap_2.get("url")

        if(TextUtils.isEmpty(imageUrl)) {
            mark.image = imageUrl_2
        } else {
            mark.image = imageUrl
        }

        if(TextUtils.isEmpty(title)) {
            val imageTitle = imagMap.get("title")
            val imageTitle_2 = imagMap_2.get("title")
            if(TextUtils.isEmpty(imageTitle)) {
                title = imageTitle_2
            } else {
                title = imageTitle
            }
        }
        mark.name = title


        //updateDate
        //totalEpisode
        val updatTimeElement = doc.getElementById("updateTime")
        if(updatTimeElement != null) {
            val datetime = updatTimeElement.attr("datetime")
            val totalEpisode = updatTimeElement.attr("data-lc_name")
            val updateDate= ShareFun.convertDataBaseDateFormat(datetime.toLong())
            if(!TextUtils.isEmpty(updateDate)) {
                mark.updateDate = updateDate
            }

            if(!TextUtils.isEmpty(totalEpisode)) {
                mark.totalEpisode = totalEpisode
            }

        }

        mark.type = WebType.soudongman.domain

        return mark
    }


    override fun episode(doc: Document): ArrayList<Episode>? {
        val episodeElements = doc.getElementsByClass("chapterlist")
        if (episodeElements != null) {
            val itemsElements = episodeElements.select("li")

            if(itemsElements != null && itemsElements.size > 0) {
                var episodeList = ArrayList<Episode>()

                itemsElements.forEach { itemElement ->

                    var episode = Episode()

                     var item = itemElement.select("a")

                    episode.title = item.text()
                    episode.url = item.attr("href")

                    episodeList.add(episode)
                }


                return episodeList
            }

        }

        return null
    }


    fun imageParser(doc: Document, key: String): HashMap<String, String> {

        var map = HashMap<String, String>()
        map.put("title", "")
        map.put("url", "")

        //image
        val imageElements = doc.getElementsByClass(key)
        if(imageElements != null) {
            val imgUrlElement = imageElements.select("img")
            if (imgUrlElement != null) {

                val imgAlt = imgUrlElement.attr("alt")
                val imaUrl = imgUrlElement.attr("data-src")

                Log.d("runParserWeb", "class: ${key} image title: ${imgAlt} , url: ${imaUrl}")
                map.put("title", imgAlt)
                map.put("url", imaUrl)
            }
        }
        return map
    }
}