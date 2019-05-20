package com.example.mybookmark.model

enum class ComicType {
    Blood(1, "熱血"),
    Love(2, "戀愛");

    val value: Int
    val type: String

    constructor(value: Int,domain:String) {
        this.value = value
        this.type = domain
    }
    companion object {

        fun valueOfEnum(value: Int): ComicType? {
            for (type in ComicType.values()) {
                if (type.value == value) {
                    return type
                }
            }
            return null
        }

        fun domainOfEnum(value: String): ComicType? {
            for (type in ComicType.values()) {
                if (type.type == value) {
                    return type
                }
            }
            return null
        }
    }



}


// https://m.tohomh123.com/chongshengzhidoushixiuxian/

//http://m.soudongman.com/94560/