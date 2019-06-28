package com.siang.wei.mybookmark.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.siang.wei.mybookmark.db.DatabaseKeys.Companion.DatabaseName
import com.siang.wei.mybookmark.db.DatabaseKeys.Companion.Version
import com.siang.wei.mybookmark.db.model.EpisodePages
import com.siang.wei.mybookmark.db.model.Mark

@Database(entities = [Mark::class, EpisodePages::class], version = Version)
abstract class AppDatabase : RoomDatabase() {
    abstract fun markDao(): MarkDao
    abstract fun episodePagesDao() : EpisodePagesDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, DatabaseName)
                .build()

    }




}