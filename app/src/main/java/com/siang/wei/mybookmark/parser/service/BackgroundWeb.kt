package com.siang.wei.mybookmark.parser.service

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build

import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.webkit.*
import com.siang.wei.mybookmark.model.WebType
import android.R.attr.data
import java.nio.charset.Charset


class BackgroundWeb {
    private var mWebView: WebView? = null
    interface CallbackListener {
        fun error(error: String)
        fun parseImage(urlImage: List<String>)
    }

    private var callback: CallbackListener? = null
    private var lock: Boolean = false
    private var timeout: Boolean = false
    private lateinit var inputUrl: String


    @SuppressLint("JavascriptInterface")
    fun init(context: Context, url: String, callback: CallbackListener) {
        this.inputUrl = url
        this.callback = callback
        this.lock = false
        mWebView = WebView(context)

        val webSettings = mWebView!!.settings
        //允许js代码
        webSettings.javaScriptEnabled = true

        //放缩
        webSettings.displayZoomControls = false
        webSettings.builtInZoomControls = false

        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true

        //允许SessionStorage/LocalStorage存储
        webSettings.domStorageEnabled = true

        //允许缓存，设置缓存位置
        webSettings.setAppCacheEnabled(true)
        webSettings.setAppCachePath(context.getDir("appcache", 0).path)
        webSettings.allowFileAccess = true
//        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        //自动加载图片
        webSettings.loadsImagesAutomatically = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        mWebView!!.webViewClient = mWebClient
        mWebView!!.webChromeClient = mWebChromeClient
        mWebView!!.addJavascriptInterface(DemoJavaScriptInterface(), "contact")

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 0
        params.width = 0
        params.height = 0
        windowManager.addView(mWebView, params)

        if(TextUtils.isEmpty(url) != null) {
            timeout = true
            mWebView!!.loadUrl(url)
        }
    }

    inner class DemoJavaScriptInterface {
        @SuppressWarnings("unused")
        @JavascriptInterface
        fun getDizhezImages(urls: String) {
            if (!TextUtils.isEmpty(urls)) {
                val list = urls.split(",")
                if (!lock && list != null && list.isNotEmpty() && callback != null) {
                    lock = true
                    callback!!.parseImage(list)

                }
            }
        }
        @SuppressWarnings("unused")
        @JavascriptInterface
        fun getWuyouhuiImages(urlsBase64: String) {
            if (!TextUtils.isEmpty(urlsBase64)) {

               val data = Base64.decode(urlsBase64, Base64.DEFAULT)
//                val url2 = String(Base64.decode(urlsBase64, Base64.NO_WRAP))
                val urls = String(data, Charset.forName("UTF-8"))
                val newUrls = urls.replace("\$qingtiandy\$", ".jpg;")
                val list = newUrls.split(";")
                if (!lock && callback != null) {
                    lock = true
                    callback!!.parseImage(list)

                }
            }
        }
    }


    private val mWebChromeClient: WebChromeClient = object : WebChromeClient() {

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {

            return super.onConsoleMessage(consoleMessage)

        }
    }

    private val mWebClient: WebViewClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
            if(!lock && mWebView != null) {
                val type = getWebType(inputUrl)
                when (type) {

                    WebType.wuyouhui -> {
                        getImagesbyWuyouhui()
                    }
                    WebType.duzhez -> {
                        getImagesbyDizhez()
                    }
                }
            }

        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

        }

        @TargetApi(android.os.Build.VERSION_CODES.M)    //171016 处理404错误
        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?
        ) {
            timeout = false
            super.onReceivedHttpError(view, request, errorResponse)
            val statusCode = errorResponse!!.statusCode
            Log.d("url", "error code: $statusCode")
            if(!lock && callback != null) {
                callback!!.error("error code: $statusCode")
            }
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            timeout = false
            if(!lock && callback != null) {
                callback!!.error("onReceivedError")
            }
        }

    }

     fun getImagesbyDizhez() {
        val string1 = "var urls = []; var count =SinMH.getChapterImageCount();"
        val string2 = "for(var i = 1; i <= count; i++) { var url = SinMH.getChapterImage(i);  urls.push(url); }"
        val string3 = "contact.getDizhezImages(urls.toString());"
        mWebView!!.loadUrl(
            "javascript:(function() { " +
                    string1 + string2 + string3 +
                    "})()"
        );
     }

    fun getImagesbyWuyouhui() {
            val string1 = "contact.getWuyouhuiImages(qTcms_S_m_murl_e);"
            mWebView!!.loadUrl(
                "javascript:(function() { " +
                        string1 +
                        "})()"
            );
    }

    fun close(context: Context) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if(mWebView != null) {
//            mWebView!!.webChromeClient = null
//            mWebView!!.webViewClient = null
            mWebView!!.stopLoading()
            mWebView!!.destroy()
        }
        windowManager.removeView(mWebView)
        mWebView = null;
        callback = null

    }

    fun getWebType(url: String): WebType? {
        val uri = Uri.parse(url)
        return WebType.domainOfEnum(uri.host)
    }
}