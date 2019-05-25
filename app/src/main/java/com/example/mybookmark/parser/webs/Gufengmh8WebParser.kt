package com.example.mybookmark.parser.webs

import android.text.TextUtils
import com.example.mybookmark.db.model.Episode
import com.example.mybookmark.db.model.Mark
import com.example.mybookmark.model.WebType
import com.example.mybookmark.parser.WebParserUtils
import org.jsoup.nodes.Document

class Gufengmh8WebParser: WebParser(){

    override fun information(doc: Document, mark: Mark): Mark? {

        var title: String = ""

        val titleElements = doc.getElementsByClass("title")
        //name
        if(titleElements != null) {
            title = titleElements.text()
        }

        val detailedElements = doc.getElementsByClass("pic")

        if(detailedElements != null) {
            val itemElement= detailedElements.select("dl")
            if(itemElement != null)
            for (element in itemElement) {
                val dtElement= itemElement.select("dt")
                val ddElement = itemElement.select("dd")
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
        val episodeElement = doc.getElementById("chapterList")
        if (episodeElement != null) {
            val itemsElements = episodeElement.select("li")

            if(itemsElements != null && itemsElements.size > 0) {
                var episodeList = ArrayList<Episode>()

                itemsElements.forEach { itemsElements ->

                    var episode = Episode()
                    var item = itemsElements.select("a")
                    episode.url = item.attr("href")

                    var titleElements = item.select("b")
                    episode.title = titleElements.text()
                    episodeList.add(episode)
                }


                return episodeList
            }

        }

        return null
    }
}