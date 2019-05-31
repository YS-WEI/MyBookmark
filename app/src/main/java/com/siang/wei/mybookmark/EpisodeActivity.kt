package com.siang.wei.mybookmark

import android.content.Context
import android.content.Intent
import android.os.Bundle


import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.siang.wei.mybookmark.databinding.ActivityEpisodeBinding
import com.siang.wei.mybookmark.databinding.ActivityMainBinding
import com.siang.wei.mybookmark.db.model.Mark
import com.siang.wei.mybookmark.view_model.EpisodeViewModel

import com.siang.wei.mybookmark.view_model.ViewModelFactory
import com.siang.wei.mybookmark.view_model.WebViewModel


class EpisodeActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityEpisodeBinding

    private lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mEpisodeViewModel: EpisodeViewModel

    private var mImageList: List<String>? = null
    private lateinit var mUrl : String
    private lateinit var mTitle : String

    companion object {
        const val URL_KEY = "url_key"
        const val TITLE_KEY = "title_key"

        fun open(context: Context, url: String, title: String) {

            var intent = Intent(context, EpisodeActivity::class.java)
            intent.putExtra(URL_KEY, url)
            intent.putExtra(TITLE_KEY, title)
            context.startActivity(intent)

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_episode)

        if(intent.hasExtra(URL_KEY)) {
            mUrl = intent.getStringExtra(URL_KEY)
        } else {
            mUrl = ""
        }

        if(intent.hasExtra(TITLE_KEY)) {
            mTitle = intent.getStringExtra(TITLE_KEY)
        } else {
            mTitle = ""
        }
        setTitle(mTitle)

        setViewModel()

    }

    private fun setViewModel() {

        mViewModelFactory = Injection.provideMarkViewModelFactory(this)
        mEpisodeViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EpisodeViewModel::class.java)

//        mEpisodeViewModel.getImagesLiveData().observe(this, )


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


}
