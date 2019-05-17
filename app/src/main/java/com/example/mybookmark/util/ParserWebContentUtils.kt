package com.example.mybookmark.util

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.example.mybookmark.db.model.Mark
import com.example.mybookmark.model.WebType
import org.jsoup.Jsoup
import java.io.IOException
import java.util.regex.Pattern

object ParserWebContentUtils {
    fun startParser(mark: Mark): Mark? {

        val uri = Uri.parse(mark.url)

        val type = WebType.domainOfEnum(uri.host) ?: return null

        when(type) {
            WebType.tohomh123 -> return tohomh123Parser(mark)
            WebType.soudongman -> return null
            else -> return null
        }




    }

    private fun tohomh123Parser(mark: Mark): Mark? {
        try {
            val doc = Jsoup.connect(mark.url).get()


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
                            var updateDate = parserIntFormString(string)
                            if(updateDate != null) {
                                Log.d("runParserWeb", "updateDate: $updateDate")
                                mark.updateDate = "$updateDate"
                            }
                        }

                        if(element.hasClass("bottom")) {
                            val totalPart = element.text()

                            Log.d("runParserWeb", "totalPart: $totalPart")
                            mark.totalPart = totalPart.trim()
                        }
                    }
                }
            }

            val imageElements = doc.getElementsByClass("coverForm")
            if (imageElements != null) {
                val imgUrlElement = imageElements.select("img")
                if(imgUrlElement != null) {
                    Log.d("runParserWeb", "img src: " + imgUrlElement.attr("src"))
                    Log.d("runParserWeb", "img title: " + imgUrlElement.attr("title"))
                    Log.d("runParserWeb", "img alt: " + imgUrlElement.attr("alt"))

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

            Log.d("runParserWeb", mark.toString())
            return mark
        } catch (e: IOException) {

            Log.d("runParserWeb", "", e)
//            builder.append("Error : ").append(e.message).append("\n")
        }

        return null
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