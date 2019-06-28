package com.siang.wei.mybookmark.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.siang.wei.mybookmark.db.model.EpisodePages
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface EpisodePagesDao {
    @Query("SELECT * FROM episodePages WHERE pageUrl LIKE :url")
    fun getEpisodePages(url: String): Single<List<EpisodePages>>

    @Insert
    fun insert(vararg episodePages: EpisodePages): Completable

    @Update
    fun update(vararg episodePages: EpisodePages): Completable
}