package com.siang.wei.mybookmark.parser.webs

import android.content.Context
import com.siang.wei.mybookmark.db.model.Episode
import com.siang.wei.mybookmark.db.model.Mark
import io.reactivex.Observable
import org.jsoup.nodes.Document

abstract class WebParser {

    abstract fun information(doc: Document, mark: Mark): Mark?
    abstract fun episode(doc: Document): ArrayList<Episode>?
}