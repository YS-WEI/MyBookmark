package com.siang.wei.mybookmark.util

import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.siang.wei.mybookmark.db.MarkDao
import com.siang.wei.mybookmark.db.model.BackupDatabase
import com.siang.wei.mybookmark.parser.WebParserUtils
import io.reactivex.Completable
import io.reactivex.Observable
import java.io.File
import java.io.FileReader
import java.io.IOException

object BackupUtil {
    private val BACKUP_DIR = Environment.getExternalStorageDirectory().absolutePath + File.separator + "MyBookMark" + File.separator

    fun exportDatabaseToJson(backupDatabase: BackupDatabase, isManual: Boolean? = false): Observable<Boolean> {

        return Observable.create {
                subscriber ->

            var gson = Gson()

            var jsonString = gson.toJson(backupDatabase)
            var fileName = "database_auto_${ShareFun.getDateTime()}.json"
            if(isManual!!) {
                fileName = "database_${ShareFun.getDateTime()}.json"
            }

            try {
                SharedFileMethod.saveFile(jsonString, SharedFileMethod.combinePath(BACKUP_DIR, fileName))
                subscriber.onNext(true)
                subscriber.onComplete()
            } catch (ex: IOException) {
                ex.printStackTrace()
                subscriber.onError(error("backup fail"))
            }
        }


    }

    fun importBackuoData(): BackupDatabase? {
        val filePath = SharedFileMethod.combinePath(BACKUP_DIR, "reload.json")
        val file = File(filePath)
        if(!file.exists()) {
            return null
        }

        var gson = Gson()

        val backupDatabase = gson.fromJson(FileReader(filePath), BackupDatabase::class.java)
        Log.d("importBackuoData", backupDatabase.toString())
        return backupDatabase
    }
}