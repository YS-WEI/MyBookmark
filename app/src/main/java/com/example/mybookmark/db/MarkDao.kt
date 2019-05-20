package com.example.mybookmark.db

import androidx.room.*
import com.example.mybookmark.db.model.Mark
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface MarkDao {

    @Query("SELECT * FROM mark")
    fun getAll(): Flowable<List<Mark>>

    @Query("SELECT * FROM mark WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): Flowable<List<Mark>>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): Mark


    @Query("SELECT * FROM mark WHERE url IN (:url)")
    fun check(url: String): Single<List<Mark>>

    @Insert
    fun insert(vararg users: Mark): Completable


    @Delete
    fun delete(user: Mark): Completable

    @Update
    fun update(vararg marks: Mark): Completable


    @Update
    fun updateByService(vararg marks: Mark)

    @Query("SELECT * FROM mark")
    fun getAllByService(): List<Mark>
}