package com.siang.wei.mybookmark.parser.webs

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.siang.wei.mybookmark.db.model.Episode
import com.siang.wei.mybookmark.db.model.EpisodeImageData
import com.siang.wei.mybookmark.db.model.Mark
import com.siang.wei.mybookmark.model.WebType
import com.siang.wei.mybookmark.util.ShareFun
import io.reactivex.ObservableEmitter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.lang.Exception
import java.util.*



class WuyoyhuiWebParser: WebParser(){

    override fun information(doc: Document, mark: Mark): Mark? {
        val defDateFormat = "MM-dd"
        var title: String = ""

        val viewSubElements = doc.getElementsByClass("Introduct_Sub")
        //name
        if(viewSubElements != null) {

            //Title
            val titleElements = viewSubElements.select(".title")
            if (titleElements != null && titleElements.size > 0) {
                titleElements.forEach { titleElement ->
                    val titleH1Elements = titleElement.select("h1")
                    run titleRun@{
                        titleH1Elements.forEach { titleH1Element ->
                            val text = titleH1Element.text()

                            if (!TextUtils.isEmpty(text) && TextUtils.isEmpty(title)) {
                                title = text
                                return@titleRun
                            }
                        }

                    }
                }

            }

            val subElements = viewSubElements.select(".sub_r")
            //name
            if (subElements != null) {
                subElements.forEach { subElement ->
                    val itemElements = subElement.select(".txtItme")
                    if (itemElements != null) {
                        itemElements.forEach { element ->
                            val icon01Elements = element.select(".icon01")
                            val dateElements = element.select(".date")

                            if (icon01Elements != null && icon01Elements.size >= 1) {
                                val newElement = element.removeClass("icon01")
                                if (newElement != null) {
                                    var text = newElement.text()

                                    text = text.replace("更新至", "")
                                    text = text.replace("《", "")
                                    text = text.replace("》", "")
                                    text = text.trim()

                                    mark.totalEpisode = text

                                }

                            }

                            if (dateElements != null && dateElements.size >= 1) {
                                val dateElement = dateElements[0]
                                val text = dateElement.text()
                                val date = ShareFun.convertToDate(text, defDateFormat)
                                val calendar = Calendar.getInstance()
                                calendar.time = date
                                val newCalendar = Calendar.getInstance()
                                val year = newCalendar.get(Calendar.YEAR)
                                calendar.set(Calendar.YEAR, year)


                                if (date != null) {
                                    val dateString = ShareFun.convertDataBaseDateFormat(calendar.timeInMillis)
                                    mark.updateDate = dateString
                                }
                            }
                        }
                    }
                }
            }
        }


        val imageElement = doc.getElementById("Cover")
        if (imageElement != null) {
            val imgUrlElements = imageElement.select("img")
            if(imgUrlElements != null && imgUrlElements.size > 0) {
                val imgUrlElement = imgUrlElements[0];
                val imgAlt = imgUrlElement.attr("title")
                mark.image = imgUrlElement.attr("src")
                if (TextUtils.isEmpty(title)) {
                    title = imgAlt
                }
            }

        }

        if(!TextUtils.isEmpty(title)) {
            mark.name = title
        }

        mark.type = WebType.wuyouhui.domain

        return mark
    }


    override fun episode(doc: Document): ArrayList<Episode>? {
        val listBlockElement = doc.getElementById("mh-chapter-list-ol-0") ?: return null
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


    fun parseEpisodeImages(context: Context, url: String, imageList : ArrayList<String>, subscriber: ObservableEmitter<ArrayList<String>>) {
//       val backgroundWeb = BackgroundWeb()
//        backgroundWeb.run(context, url)

//        val intent = Intent(context, BackgroundWebService::class.java)
//        intent.putExtra(BackgroundWebService.URL_KEY, url)
//        context.startService(intent)
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