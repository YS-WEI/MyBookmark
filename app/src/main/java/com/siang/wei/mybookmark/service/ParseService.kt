package com.siang.wei.mybookmark.service

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.siang.wei.mybookmark.db.AppDatabase
import com.siang.wei.mybookmark.db.DatabaseKeys
import com.siang.wei.mybookmark.db.MarkDao
import com.siang.wei.mybookmark.db.model.BackupDatabase
import com.siang.wei.mybookmark.parser.WebParserUtils
import com.siang.wei.mybookmark.util.BackupUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ParseService: IntentService(SERVICE_NAME) {

    companion object {
        const val SERVICE_NAME = "ParseService"
    }



    override fun onHandleIntent(intent: Intent?) {
        val database = AppDatabase.getInstance(this);
        val markDao = database.markDao();
        val marks = markDao.getAllByService();

        marks.forEach { mark -> run{

            Log.d(SERVICE_NAME, "${mark.name} url: ${mark.url}" )
            WebParserUtils.startParserInfo(mark)

            markDao.updateByService(mark)
        } }
        if(marks.size > 0) {
            endSaveBackup(markDao)
        }
    }

    fun endSaveBackup (markDao: MarkDao ) {
        // Auto Backup
        val marks = markDao.getAllByService();
        val backupData = BackupDatabase(DatabaseKeys.Version, marks)
        BackupUtil.exportDatabaseToJson(backupData).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { error ->
                Log.e("Auto Backup", "Backup fail", error)
            })
    }


}