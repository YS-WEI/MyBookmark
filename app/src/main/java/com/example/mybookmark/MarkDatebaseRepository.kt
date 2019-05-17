package com.example.mybookmark

import com.example.mybookmark.db.MarkDao
import com.example.mybookmark.db.model.Mark
import io.reactivex.Completable
import io.reactivex.Flowable

class MarkDatebaseRepository constructor(markDao: MarkDao){

    var markDao: MarkDao = markDao

    fun getMarkAll() : Flowable<List<Mark>> {
        return markDao.getAll()
    }

    fun insert(mark: Mark): Completable {
        return markDao.insert(mark)
    }

    fun delect(mark: Mark) : Completable {
        return markDao.delete(mark)
    }
}