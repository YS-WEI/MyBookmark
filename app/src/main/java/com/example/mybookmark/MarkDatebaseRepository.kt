package com.example.mybookmark

import com.example.mybookmark.db.MarkDao
import com.example.mybookmark.db.model.Mark
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

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

    fun check(url: String) : Single<List<Mark>> {
        return markDao.check(url)
    }

    fun updateByNoWait(mark: Mark):Completable {
        return markDao.update(mark)
    }
}