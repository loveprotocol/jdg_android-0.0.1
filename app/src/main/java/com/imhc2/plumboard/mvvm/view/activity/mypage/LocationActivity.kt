package com.imhc2.plumboard.mvvm.view.activity.mypage

import android.content.Context
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.webkit.*
import com.imhc2.plumboard.R
import com.imhc2.plumboard.mvvm.view.fragment.dialog.LocationDialogFragment
import kotlinx.android.synthetic.main.activity_location.*
import timber.log.Timber

open class LocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        initToolbar()
        //activity_auth_webView.setWebViewClient(DanalWebViewClient(this))
        initWebView()
    }

    open fun initWebView() {
        val settings = activity_location_webView.getSettings()
        settings.domStorageEnabled = true//local storage
        settings.javaScriptEnabled = true//자바스크립트 허용
        settings.javaScriptCanOpenWindowsAutomatically = true//javascript의 window.open 허용
        //settings.setSupportMultipleWindows(true)
        //settings.domStorageEnabled=true //
        if (Build.VERSION.SDK_INT >= 21) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        activity_location_webView.webChromeClient = WebChromeClient()
        activity_location_webView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {

            }
        }

        activity_location_webView.addJavascriptInterface(LocationActivity.LocaHandler(this), "Android")
        //activity_location_webView.loadUrl("http://118.129.135.141:8080/htmlTest/Android.html")
        activity_location_webView.loadUrl(getString(R.string.LocationAddress))


    }


    private fun initToolbar() {
        activity_location_toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPlumBoardSub))
        activity_location_toolbar.title = "지역 검색"
        setSupportActionBar(activity_location_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    private class LocaHandler(context: Context) {

        val context = context
        @JavascriptInterface
        fun setAddress(add1: String, add2: String, add3: String) {
//            Observable
//                    .just<String>(add2)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe { address ->
//                        Timber.e("주소 = $add2")
//
//                    }
            Timber.e("주소는 = $add1 + $add2 + $add3}")

            if (add2.contains("구")) {
                var sigu = add2.substring(0, add2.indexOf("구") + 1)
                LocationDialogFragment.newInstance(add1, sigu).show((context as? LocationActivity)?.supportFragmentManager, "")
            } else if (add2.contains("시")) {
                var sigu = add2.substring(0, add2.indexOf("시") + 1)
                LocationDialogFragment.newInstance(add1, sigu).show((context as? LocationActivity)?.supportFragmentManager, "")
            }


            //(context as LocationActivity).finish()
            Timber.e("주소는 = $add1 + $add2 + $add3}")
        }
    }


}


