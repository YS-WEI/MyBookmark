package com.siang.wei.mybookmark.util

import android.net.Uri
import android.os.Build
import android.text.TextUtils
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object ShareFun {
    const val showDateFormat = "yyyy-MM-dd"
    const val dateFormat = "yyyyMMdd"
    const val dateTimeFormat = "yyyyMMddHHmmss"

    fun getDate(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern(dateFormat)
            val formatted = current.format(formatter)

            return formatted
        } else {
            var date = Date();
            val formatter = SimpleDateFormat(dateFormat)
            return formatter.format(date)
        }

    }

    fun getDateTime(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern(dateTimeFormat)
            val formatted = current.format(formatter)

            return formatted
        } else {
            var date = Date();
            val formatter = SimpleDateFormat(dateTimeFormat)
            return formatter.format(date)
        }

    }

    fun formatDate(date: String): String {
        if(TextUtils.isEmpty(date)) {
            return ""
        }

        val parser = SimpleDateFormat(dateFormat)
        val formatter = SimpleDateFormat(showDateFormat)
        return formatter.format(parser.parse(date))
    }

    fun converDate(date : Long): String {
        var dateTime = Date(date)
        val formatter = SimpleDateFormat(dateFormat)
        return formatter.format(date)
    }


    fun combinUrl(url: String, addUrl: String): String {
        var uri = Uri.parse(url)
        var addUri = Uri.parse(addUrl)

        var newUrl = url

        if(TextUtils.isEmpty(addUri.host) || uri.host.equals(addUri.host, true)) {
            var pathSegments = uri.pathSegments as ArrayList<String>

            addUri.pathSegments.forEach {addSeg ->
                val match = uri.pathSegments.find { seg ->
                    addSeg.equals(seg)
                }

                if(match == null) {
                    newUrl = SharedFileMethod.combinePath(newUrl, addSeg)
                }
            }

        }


        return newUrl

    }

}