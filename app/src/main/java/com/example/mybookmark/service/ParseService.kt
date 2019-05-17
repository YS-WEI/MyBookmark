package com.example.mybookmark.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.example.mybookmark.db.AppDatabase
import com.example.mybookmark.util.ParserWebContentUtils

class ParseService: IntentService(SERVICE_NAME) {

    companion object {
        const val SERVICE_NAME = "ParseService"
    }



    override fun onHandleIntent(intent: Intent?) {
        val database = AppDatabase.getInstance(this);
        val markDao = database.markDao();
        val marks = markDao.getAllByService();
        Log.d("ParseService", "" + marks.size)

        marks.forEach { mark -> run{

            ParserWebContentUtils.startParser(mark)

            markDao.updateByService(mark)
        } }
    }


}