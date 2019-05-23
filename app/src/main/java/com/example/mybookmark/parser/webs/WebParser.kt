package com.example.mybookmark.parser.webs

import com.example.mybookmark.db.model.Episode
import com.example.mybookmark.db.model.Mark
import org.jsoup.nodes.Document

abstract class WebParser constructor() {

    abstract fun information(doc: Document, mark: Mark): Mark?
    abstract fun episode(doc: Document): ArrayList<Episode>?
}