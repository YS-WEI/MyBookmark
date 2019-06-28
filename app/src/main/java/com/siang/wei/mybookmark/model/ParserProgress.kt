package com.siang.wei.mybookmark.model

data class ParserProgress (
    var total: Int,
    var current: Int,
    var isFinish: Boolean = false,
    var isError: Boolean = false,
    var error: String? = null
)