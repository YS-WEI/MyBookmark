package com.siang.wei.mybookmark.parser

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.webkit.*

class BackgroundWeb {
    private lateinit var mWebView: WebView
    interface CallbackListener {
        fun processHTML(string: String)
        fun parseImage(urlImage: String)
    }

    private var callback: CallbackListener? = null
    private var lock: Boolean = false


    @SuppressLint("JavascriptInterface")
    fun init(context: Context, url: String, callback: CallbackListener) {
        this.callback = callback
        mWebView = WebView(context)

        val webSettings = mWebView.settings
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

        mWebView.webViewClient = mWebClient
        mWebView.webChromeClient = mWebChromeClient
        mWebView.addJavascriptInterface(DemoJavaScriptInterface(), "HTMLOUT")



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
            mWebView.loadUrl(url)
        }
    }

    inner class DemoJavaScriptInterface {
        @SuppressWarnings("unused")
        @JavascriptInterface
        fun processHTML(html: String) {
//            Log.d("BackgroundWeb", html)
            if(callback != null) {
                callback!!.processHTML(html)
            }
        }
    }


    private val mWebChromeClient: WebChromeClient = object : WebChromeClient() {

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
//            Mixed Content: The page at 'https://m.duzhez.com/manhua/13858/' was loaded over HTTPS, but requested an insecure image 'http://mhimg.9mmc.com:44237/images/cover/201807/153207090559513858b45e5fc6.jpg'. This content should also be served over HTTPS.
            if(!lock  && consoleMessage != null) {
                val message = consoleMessage.message()
                if(message.indexOf("Mixed Content:") != -1) {

                    val messages = message.split(",")
                    if(messages.size == 2) {
                        val string = messages[1]
                        val strings = string.split("'")
                        if(strings.isNotEmpty()) {
                            strings.forEach { string ->
                                if(string.indexOf(".jpg") != -1) {
                                    mWebView.stopLoading()
                                    if(!lock && callback != null) {
                                        lock = true
                                        callback!!.parseImage(string)
                                    }
//
                                }
                            }
                        }
                    }
                }


            }


            return super.onConsoleMessage(consoleMessage)

        }
    }

    private val mWebClient: WebViewClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.d("url", url);
        }


        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            mWebView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            if(!lock && callback != null) {
                callback!!.parseImage("error_image")
            }
        }

    }

    fun close(context: Context) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.removeView(mWebView)
        callback = null

        mWebView.stopLoading()
        mWebView.destroy()
    }
}