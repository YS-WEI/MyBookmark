package com.example.mybookmark.parser.webs

import android.text.TextUtils
import com.example.mybookmark.db.model.Episode
import com.example.mybookmark.db.model.Mark
import com.example.mybookmark.model.WebType
import com.example.mybookmark.parser.WebParserUtils
import org.jsoup.nodes.Document

class ManhuaguiWebParser: WebParser(){

    override fun information(doc: Document, mark: Mark): Mark? {

        var title: String = ""

        val titleBoxElements = doc.getElementsByClass("main-bar")
        //name
        if(titleBoxElements != null) {
            var titleElements= titleBoxElements.select("h1")
            if(titleElements != null) {
                title = titleElements.text()
            }
        }

        val detailedElements = doc.getElementsByClass("cont-list")

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

            val imageElement = detailedElements.select("thumb")
            if (imageElement != null) {
                val imgUrlElement = imageElement.select("img")
                if(imgUrlElement != null) {
                    mark.image = imgUrlElement.attr("src")
                }

            }
        }


        if(!TextUtils.isEmpty(title)) {
            mark.name = title
        }

        mark.type = WebType.manhuagui.domain

        return mark
    }


    override fun episode(doc: Document): ArrayList<Episode>? {
        val episodeElement = doc.getElementById("chapter-list-1")
        if (episodeElement != null) {
            val itemsElements = episodeElement.select("li")

            if(itemsElements != null && itemsElements.size > 0) {
                var episodeList = ArrayList<Episode>()

                itemsElements.forEach { itemsElements ->

                    var episode = Episode()
                    var item = itemsElements.select("a")
                    episode.url = item.attr("href")

                    var titleElements = item.select("span")
                    episode.title = titleElements.text()
                    episodeList.add(episode)
                }


                return episodeList
            }

        }

        return null
    }
}