package com.siang.wei.mybookmark.parser.webs

import android.text.TextUtils
import com.siang.wei.mybookmark.db.model.Episode
import com.siang.wei.mybookmark.db.model.Mark
import com.siang.wei.mybookmark.model.WebType
import com.siang.wei.mybookmark.parser.WebParserUtils
import org.jsoup.nodes.Document
import java.util.*
import kotlin.collections.ArrayList

class M17KWebParser: WebParser() {

    override fun information(doc: Document, mark: Mark): Mark? {

        var title: String? = ""
        val subElements = doc.getElementsByClass("sub_r")
        if (subElements != null) {
            val itemElements = subElements.select(".txtItme")
            if (itemElements != null) {
                itemElements.forEachIndexed { index, element ->
                    if (index == 0) {
                        title = element.text()
                    } else {
                        val text = element.text()
                        if (text.indexOf("状态") != -1) {

                            val totalEpisode = text.replace("状态：", "")
                            if (!TextUtils.isEmpty(totalEpisode)) {
                                mark.totalEpisode = totalEpisode
                            }
                        } else if (text.indexOf("时间") != -1) {

                            val year = Calendar.getInstance().get(Calendar.YEAR).toString()
                            var updateDate = year + WebParserUtils.parserIntFormString(text)
                            if (!TextUtils.isEmpty(updateDate)) {
                                mark.updateDate = updateDate
                            }
                        }
                    }
                }
            }
        }


        val imageElement = doc.getElementById("Cover")
        if(imageElement != null) {
            val itemsTag = imageElement.select("img")
            if(itemsTag != null) {
                val imageUrl = itemsTag[0].attr("src")
                mark.image = imageUrl

                if (TextUtils.isEmpty(title)) {
                    val imageTitle =  itemsTag[0].attr("title")
                    if (!TextUtils.isEmpty(imageTitle)) {
                        title = imageTitle
                    }
                }
            }
        }
        mark.name = title


        mark.type = WebType.m17kmanhua.domain

        return mark
    }


    override fun episode(doc: Document): ArrayList<Episode>? {
        val episodeElement = doc.getElementById("chapterList_1")
        if (episodeElement != null) {
            val itemsElements = episodeElement.select("li")

            if (itemsElements != null && itemsElements.size > 0) {
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
}