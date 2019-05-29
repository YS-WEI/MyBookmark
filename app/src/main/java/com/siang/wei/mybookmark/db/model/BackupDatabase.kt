package com.siang.wei.mybookmark.db.model

data class BackupDatabase (
    val version :Int,
    val list: List<Mark>
)