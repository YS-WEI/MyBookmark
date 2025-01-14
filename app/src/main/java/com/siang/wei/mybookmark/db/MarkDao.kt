package com.siang.wei.mybookmark.db

import androidx.room.*
import com.siang.wei.mybookmark.db.model.Mark
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface MarkDao {

    @Query("SELECT * FROM mark ORDER BY updateDate DESC")
    fun getAll(): Flowable<List<Mark>>

    @Query("SELECT * FROM mark WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): Flowable<List<Mark>>


    @Query("SELECT * FROM mark WHERE comicType IN (:comicType) ORDER BY updateDate DESC")
    fun loadAllByComicType(comicType: Int): Flowable<List<Mark>>
//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): Mark


    @Query("SELECT * FROM mark WHERE url IN (:url)")
    fun check(url: String): Single<List<Mark>>

    @Insert
    fun insert(vararg marks: Mark): Completable


    @Delete
    fun delete(mark: Mark): Completable

    @Update
    fun update(vararg marks: Mark): Completable


    @Update
    fun updateByService(vararg marks: Mark)

    @Query("SELECT * FROM mark")
    fun getAllByService(): List<Mark>

    @Query("SELECT * FROM mark WHERE comicType IN (:comicType)")
    fun getAllByComicTypeByService(comicType: Int): List<Mark>

    @Query("DELETE FROM mark")
    fun nukeTable() : Completable

}