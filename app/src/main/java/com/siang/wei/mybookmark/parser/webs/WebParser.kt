package com.siang.wei.mybookmark.parser.webs

import com.siang.wei.mybookmark.db.model.Episode
import com.siang.wei.mybookmark.db.model.Mark
import org.jsoup.nodes.Document

abstract class WebParser constructor() {

    abstract fun information(doc: Document, mark: Mark): Mark?
    abstract fun episode(doc: Document): ArrayList<Episode>?
}