package com.siang.wei.mybookmark

import com.siang.wei.mybookmark.db.AppDatabase
import com.siang.wei.mybookmark.db.MarkDao
import com.siang.wei.mybookmark.db.model.Mark
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class MarkDatebaseRepository constructor(markDao: MarkDao){

    var markDao: MarkDao = markDao

    fun getMarkAll() : Flowable<List<Mark>> {
        return markDao.getAll()
    }

    fun getMarkAllByComicType(type: Int) : Flowable<List<Mark>> {
        return markDao.loadAllByComicType(type)
    }

    fun insert(mark: Mark): Completable {
        return markDao.insert(mark)
    }

//    fun insert(marks: List<Mark>): Completable {
//        return markDao.insert(marks)
//    }

    fun delect(mark: Mark) : Completable {
        return markDao.delete(mark)
    }

    fun check(url: String) : Single<List<Mark>> {
        return markDao.check(url)
    }

    fun updateByNoWait(mark: Mark):Completable {
        return markDao.update(mark)
    }

    fun delectAll() : Completable{
        return markDao.nukeTable()
    }

}