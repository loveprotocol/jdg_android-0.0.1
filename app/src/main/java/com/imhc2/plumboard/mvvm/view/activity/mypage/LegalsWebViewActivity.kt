package com.imhc2.plumboard.mvvm.view.activity.mypage

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.imhc2.plumboard.R
import kotlinx.android.synthetic.main.activity_legals_web_view.*

class LegalsWebViewActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legals_web_view)





//        activity_location_webView.webChromeClient = WebChromeClient()
//        activity_location_webView.webViewClient = object : WebViewClient() {
//            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
//
//            }
//        }


        val settings = activity_legals_web_view_webView.settings
        settings.builtInZoomControls = false
        settings.loadWithOverviewMode = true
        settings.defaultTextEncodingName = "UTF-8"
        settings.domStorageEnabled = true//local storage
        settings.javaScriptEnabled = true//자바스크립트 허용
        settings.javaScriptCanOpenWindowsAutomatically = true//javascript의 window.open 허용
        if (Build.VERSION.SDK_INT >= 21) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        activity_legals_web_view_webView.webChromeClient = WebChromeClient()
        activity_legals_web_view_webView.webViewClient = WebViewClient()
        var intent = intent
        var address = intent.getStringExtra("address")
        //이용약관
        //http://www.plumboard.io/blank-2

        //개인정보처리
        //http://www.plumboard.io/privacypolicy

        activity_legals_web_view_webView.loadUrl(address)
    }

}
