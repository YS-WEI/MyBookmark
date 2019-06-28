package com.siang.wei.mybookmark.model
import com.siang.wei.mybookmark.R

enum class WebType {
    soudongman("m.soudongman.com", "搜动漫", R.color.web_type_soudongman),
    tohomh123("m.tohomh123.com", "土豪",R.color.web_type_tohomh123),
    gufengmh8("m.gufengmh8.com", "古风",R.color.web_type_gufengmh8),
    mhkan("m.mhkan.com", "漫画看",R.color.web_type_mhkan),
    manhuagui("m.manhuagui.com", "看漫画",R.color.web_type_manhuagui),
    duzhez("wap.duzhez.com", "亲亲漫画", R.color.web_type_duzhez),
    wuyouhui("m.wuyouhui.net", "友绘漫画", R.color.web_type_wuyouhui);

    val domain: String
    val webName: String
    val color: Int

    constructor(domain:String, name: String, color: Int) {
        this.domain = domain
        this.webName = name
        this.color = color
    }
    companion object {
        fun domainOfEnum(domain: String): WebType? {
            for (type in WebType.values()) {
                if (type.domain == domain) {
                    return type
                }
            }
            return null
        }
    }



}


// https://m.tohomh123.com/chongshengzhidoushixiuxian/

//http://m.soudongman.com/94560/