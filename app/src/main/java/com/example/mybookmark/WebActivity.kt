package com.example.mybookmark

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.os.Build
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mybookmark.db.model.Episode
import com.example.mybookmark.db.model.Mark
import com.example.mybookmark.view_model.ViewModelFactory
import com.example.mybookmark.view_model.WebViewModel


class WebActivity : AppCompatActivity() {

    companion object {
        const val URL_KEY = "mark_url"

        fun open(context: Context, mark: Mark) {

            var intent = Intent(context, WebActivity::class.java)
            intent.putExtra(URL_KEY, mark)
            context.startActivity(intent)

        }
    }


    private lateinit var mWebView: WebView
    private lateinit var mSwipeRefresh: SwipeRefreshLayout
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mInputUrl: Uri
    private lateinit var mInputMark : Mark

    private lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mWebViewModel: WebViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        if(intent.hasExtra(URL_KEY)) {
            mInputMark = intent.getParcelableExtra(URL_KEY)
            if(mInputMark != null) {
                setViewModel(mInputMark)
            }
        }


        mWebView = findViewById(R.id.web_view)
        mProgressBar = findViewById(R.id.progress_bar)
        initRefresh()

        val webSettings = mWebView.settings
        webSettings.javaScriptEnabled = true

        //放缩
        webSettings.displayZoomControls = true
        webSettings.builtInZoomControls = true
        removeJavascriptInterfaces(mWebView)
        mWebView.webViewClient = mWebClient
        mWebView.webChromeClient = mWebChromeClient

        if(mInputMark != null) {
            mInputUrl = Uri.parse(mInputMark!!.url)
            Log.d("mInputUrl url host:", mInputUrl.host)
            mWebView.loadUrl(mInputMark!!.url)
        }

    }

    private fun setViewModel(mark: Mark) {

        mViewModelFactory = Injection.provideMarkViewModelFactory(this)
        mWebViewModel = ViewModelProviders.of(this, mViewModelFactory).get(WebViewModel::class.java)

        mWebViewModel.setMark(mark)

        mWebViewModel.getEpisodesData().observe(this,
            Observer<List<Episode>> { list ->
                run {
                  showToast("Episode number: ${list.size}")
                }
            })
    }

    private fun initRefresh() {
        mSwipeRefresh = findViewById(R.id.swipe_refresh)
        mSwipeRefresh.setOnRefreshListener {
            //重新加载刷新页面
            mWebView.loadUrl(mWebView.getUrl())
        }

        //首次启动刷新页面
        mSwipeRefresh.post {
            mSwipeRefresh.isRefreshing = true
            mWebView.loadUrl(mWebView.getUrl())
        }
    }


    @TargetApi(11)
    private fun removeJavascriptInterfaces(webView: WebView) {
        try {
            if (Build.VERSION.SDK_INT in 11..16) {
                webView.removeJavascriptInterface("searchBoxJavaBridge_")
                webView.removeJavascriptInterface("accessibility")
                webView.removeJavascriptInterface("accessibilityTraversal")
            }
        } catch (tr: Throwable) {
            tr.printStackTrace()
        }

    }


    private val mWebChromeClient: WebChromeClient = object : WebChromeClient() {

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.progress = newProgress
            mProgressBar.visibility = if (newProgress < 100) { View.VISIBLE } else { View.GONE }


            if (newProgress == 100) {
                //隐藏进度条
                mSwipeRefresh.isRefreshing = false
            } else if (!mSwipeRefresh.isRefreshing) {
                mSwipeRefresh.isRefreshing = true
            }

        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)

            Log.d("onReceivedTitle", title);
        }
    }

    private val mWebClient: WebViewClient = object : WebViewClient() {
        @SuppressWarnings("deprecation")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val uri = Uri.parse(url)
            Log.d("url", url);
            // Returning false means that you are going to load this url in the webView itself
            if(!checkDomain(uri)) {
                return true
            }
            mWebViewModel.nextUrl(url)

            return false
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val uri = request.url
            Log.d("url", request.url.path);
            if(!checkDomain(uri)) {
                return true
            }

            mWebViewModel.nextUrl(request.url.toString())

            return false
        }

//        @SuppressWarnings("deprecation")
//        override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
//            val uri = Uri.parse(url)
//            return if (checkDomain(uri)) {
//                super.shouldInterceptRequest(view, url)
//            } else {
//                WebResourceResponse(null, null, null)
//            }
//        }
//
//        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
//            return if (checkDomain(request.url)) {
//                super.shouldInterceptRequest(view, request)
//            } else {
//                WebResourceResponse(null, null, null)
//            }
//        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.d("url", url);
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.d("url", url)

//            if(!TextUtils.isEmpty(url) && !mInputMark.url.equals(url, true)) {
//                mWebViewModel.nextUrl(url!!)
//            }

        }
    }

    private fun checkDomain(uri: Uri): Boolean {
        return mInputUrl.host.equals(uri.host, true)
    }

    fun showToast(text: String) {
        val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

}

