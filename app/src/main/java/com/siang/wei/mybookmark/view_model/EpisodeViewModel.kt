package com.siang.wei.mybookmark.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.siang.wei.mybookmark.parser.WebParserUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class EpisodeViewModel : ViewModel() {

    private var imagesLiveData: MutableLiveData<List<String>> = MutableLiveData()


    fun getImagesLiveData(): LiveData<List<String>> {
        return imagesLiveData
    }

    fun parseAllImage(url:String) {

        WebParserUtils.startParseImage(url).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    list ->
                Log.d("startParseImage", "${list}")

                imagesLiveData.value = list

            },
                { error -> Log.e("parser", "parser fail", error) })
    }


}