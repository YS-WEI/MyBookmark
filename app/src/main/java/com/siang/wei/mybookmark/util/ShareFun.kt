package com.siang.wei.mybookmark.util

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

    // Joins two path components, adding a separator only if necessary.
    fun combinePath(parent: String, add: String): String {
        val prefixLength = parent.length
        var haveSlash = prefixLength > 0 && parent[prefixLength - 1] == File.separatorChar
        if (!haveSlash) {
            haveSlash = add.length > 0 && add[0] == File.separatorChar
        }
        return if (haveSlash) parent + add else parent + File.separatorChar + add
    }
}