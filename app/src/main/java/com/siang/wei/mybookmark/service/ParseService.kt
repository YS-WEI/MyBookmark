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
import android.text.TextUtils
import androidx.annotation.Nullable
import com.siang.wei.mybookmark.db.model.Mark


class ParseService: IntentService(SERVICE_NAME) {

    companion object {
        const val SERVICE_NAME = "ParseService"
        val ACTION_RETURN_FINISH = "action_return_finish"
        val ACTION_RETURN_UPDATE = "action_return_update"

        val EXTRA_TOTLE_SIZE = "EXTRA_TOTLE_SIZE"
        val EXTRA_CURRENT_NUMBER = "EXTRA_CURRENT_NUMBER"
    }



    override fun onHandleIntent(intent: Intent?) {
        val database = AppDatabase.getInstance(this);
        val markDao = database.markDao();
        val marks = markDao.getAllByService();
        var count = 0
        marks.forEach { mark -> run{
            count++
            Log.d(SERVICE_NAME, "${mark.name} url: ${mark.url}" )
            WebParserUtils.startParserInfo(mark)

            markDao.updateByService(mark)

            updateSevice(marks, count)
        } }

        if(marks.size > 0) {
            endSaveBackup(markDao)
            finishSevice(marks, count)
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

    private fun updateSevice(data: List<Mark>, count: Int) {
        val intent = Intent(ACTION_RETURN_UPDATE)

        intent.putExtra(EXTRA_TOTLE_SIZE, data.size)
        intent.putExtra(EXTRA_CURRENT_NUMBER, count)

        sendBroadcast(intent)
    }


    private fun finishSevice(data: List<Mark>, count: Int) {
        val intent = Intent(ACTION_RETURN_FINISH)

        intent.putExtra(EXTRA_TOTLE_SIZE, data.size)
        intent.putExtra(EXTRA_CURRENT_NUMBER, count)

        sendBroadcast(intent)
    }
}