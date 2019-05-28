package com.siang.wei.mybookmark.model

enum class WebType {
    soudongman("m.soudongman.com"),
    tohomh123("m.tohomh123.com");

    val domain: String

    constructor(domain:String) {
        this.domain = domain
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