package com.siang.wei.mybookmark.parser.webs

import android.text.TextUtils
import com.siang.wei.mybookmark.db.model.Episode
import com.siang.wei.mybookmark.db.model.Mark
import com.siang.wei.mybookmark.model.WebType
import com.siang.wei.mybookmark.parser.WebParserUtils
import org.jsoup.nodes.Document

class TohomhWebParser: WebParser(){

    override fun information(doc: Document, mark: Mark): Mark? {

        var title: String = ""

        val infoElements = doc.getElementsByClass("detailForm")
        //name
        if(infoElements != null) {
            val titleElement = infoElements.select(".title")
            title = titleElement.text()


            val infoItemElement= infoElements.select("p")
            if(infoItemElement != null) {
                for (element in infoItemElement) {
                    val string = element.text()
                    if(string.indexOf("更新时间") != -1) {
                        var updateDate = WebParserUtils.parserIntFormString(string)
                        if(updateDate != null) {
                            mark.updateDate = "$updateDate"
                        }
                    }

                    if(element.hasClass("bottom")) {
                        val totalEpisode = element.text()
                        mark.totalEpisode = totalEpisode.trim()
                    }
                }
            }
        }

        val imageElements = doc.getElementsByClass("coverForm")
        if (imageElements != null) {
            val imgUrlElement = imageElements.select("img")
            if(imgUrlElement != null) {

                val imgTitle = imgUrlElement.attr("title")
                val imgAlt = imgUrlElement.attr("alt")

                mark.image = imgUrlElement.attr("src")

                if (TextUtils.isEmpty(title)) {
                    if(TextUtils.isEmpty(imgTitle)) {
                        title = imgAlt
                    } else {
                        title = imgTitle
                    }
                }
            }

        }
        if(!TextUtils.isEmpty(title)) {
            mark.name = title
        }

        mark.type = WebType.tohomh123.domain

        return mark
    }


    override fun episode(doc: Document): ArrayList<Episode>? {
        val episodeElements = doc.getElementsByClass("chapterList")
        if (episodeElements != null) {
            val itemsElements = episodeElements.select("a")

            if(itemsElements != null && itemsElements.size > 0) {
                var episodeList = ArrayList<Episode>()

                itemsElements.forEach { item ->

                    var episode = Episode()

                    episode.title = item.text()
                    episode.url = item.attr("href")

                    episodeList.add(episode)
                }


                return episodeList
            }

        }

        return null
    }
}