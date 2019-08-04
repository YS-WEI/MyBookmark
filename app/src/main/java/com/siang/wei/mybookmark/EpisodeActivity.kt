package com.siang.wei.mybookmark

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog


import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.siang.wei.mybookmark.adapter.ImageRecyclerViewAdapter
import com.siang.wei.mybookmark.databinding.ActivityEpisodeBinding
import com.siang.wei.mybookmark.model.ParserProgress
import com.siang.wei.mybookmark.util.AlertUtil
import com.siang.wei.mybookmark.view_model.EpisodeViewModel

import com.siang.wei.mybookmark.view_model.ViewModelFactory


class EpisodeActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityEpisodeBinding

    private lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mEpisodeViewModel: EpisodeViewModel

    private lateinit var mAdapter: ImageRecyclerViewAdapter
    private lateinit var mUrl : String
    private lateinit var mTitle : String

    private lateinit var mImageList : ArrayList<String>

    private var mProgressDialog: AlertDialog? = null
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

        mImageList = ArrayList()
        mAdapter = ImageRecyclerViewAdapter(mImageList)
        val recyclerView = mBinding.recyclerView
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = mAdapter

        setViewModel()

    }



    private fun setViewModel() {

        mViewModelFactory = Injection.provideMarkViewModelFactory(this)
        mEpisodeViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EpisodeViewModel::class.java)

//        mEpisodeViewModel.getImagesLiveData().observe(this, )

        if(!TextUtils.isEmpty(mUrl)) {
            mEpisodeViewModel.parseAllImage(mUrl, this)
        }

        mEpisodeViewModel.getImagesLiveData().observe(this, Observer<ArrayList<String>> {
            list ->

            Log.d("EpisodeActivity", "update list: ${mImageList}")

            mBinding.recyclerView.setItemViewCacheSize(list.size)
            mAdapter.setList(list)
            mBinding.recyclerView.scrollToPosition(mAdapter.getPosition())


        })

        mEpisodeViewModel.getProgressDialogLiveData().observe(this, Observer<Boolean>{ isShow ->
            if(mProgressDialog == null) {
                mProgressDialog = AlertUtil.showProgressBar(this)
            }

            if(isShow)
                mProgressDialog!!.show()
            else
                mProgressDialog!!.dismiss()
        })

        mEpisodeViewModel.getParserProgressLiveData().observe(this, Observer<ParserProgress> { progress ->

//            if(mProgressDialog != null && mProgressDialog!!.isShowing) {
//
//
//
////                mProgressDialog!!.setTitle("${progress.current}/${progress.total}")
//
//            }

            if(progress.isError) {
                setTitle("$mTitle - error - ${progress.error} ")
            } else {
                if(progress.isFinish) {
                    setTitle("$mTitle - Pass ${progress.total}")
                } else {
                    setTitle("$mTitle - sync.. ${progress.total})")
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

}
