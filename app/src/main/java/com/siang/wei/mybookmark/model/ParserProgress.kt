package com.siang.wei.mybookmark.model

data class ParserProgress (
    var total: Int = 0,
    var isFinish: Boolean = false,
    var isError: Boolean = false,
    var error: String? = null
)