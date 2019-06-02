package com.siang.wei.mybookmark.model

enum class WebType {
    soudongman("m.soudongman.com", "搜动漫"),
    tohomh123("m.tohomh123.com", "土豪"),
    gufengmh8("m.gufengmh8.com", "古风"),
    mhkan("m.mhkan.com", "漫画看"),
    manhuagui("m.manhuagui.com", "看漫画");


    val domain: String
    val webName: String

    constructor(domain:String, name: String) {
        this.domain = domain
        this.webName = name
    }
    companion object {
        fun domainOfEnum(value: String): WebType? {
            for (type in WebType.values()) {
                if (type.domain == value) {
                    return type
                }
            }
            return null
        }
    }



}


// https://m.tohomh123.com/chongshengzhidoushixiuxian/

//http://m.soudongman.com/94560/