package com.imhc2.plumboard.mvvm.view.activity.mypage

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.functions.FunctionEvents
import com.imhc2.plumboard.mvvm.view.activity.auth.AuthFindIdDoneActivity
import com.imhc2.plumboard.mvvm.view.fragment.dialog.ProgressDialogHelper
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_auth.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*




class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initToolbar()
        initWebView()
    }


    private fun initWebView() {
        //activity_auth_webView.setWebViewClient(DanalWebViewClient(this))
        val settings = activity_auth_webView.settings
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = false
        settings.loadWithOverviewMode = true
        settings.defaultTextEncodingName = "UTF-8"

        //settings.domStorageEnabled=true //
        activity_auth_webView.webChromeClient = WebChromeClient()

        val intent = intent
        intent.getStringExtra("kind")


        activity_auth_webView.addJavascriptInterface(JsHandler(this, activity_auth_webView), "Android")
        activity_auth_webView.loadUrl(getString(R.string.AuthAddress))

    }

    private fun initToolbar() {
        activity_auth_toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPlumBoardSub))
        activity_auth_toolbar.title = getString(R.string.AuthActivity_toolbarTitle)
        setSupportActionBar(activity_auth_toolbar)
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
        overridePendingTransition(R.anim.slide_from_left , R.anim.slide_to_right)
    }

    class JsHandler(var context: Context, var webview: WebView) {

        @android.webkit.JavascriptInterface//절대필수
        fun closeActivity() {
            (context as AuthActivity).finish()
            webview.destroy()
        }


        @android.webkit.JavascriptInterface//절대필수
        fun getData(impUid: String) {
            val intent = (context as AuthActivity).intent
            var loading = ProgressDialogHelper(context)


            if (intent.hasExtra("kind")) {
                val kind = intent.getStringExtra("kind")

                if (kind == "certification") {
                    loading.show()

                    FunctionEvents().registerUserAuth(impUid).addOnCompleteListener { task ->
                        loading.dismiss()
                        if (task.isSuccessful) {
                            //val result = task.result as? MutableMap<Any, Any>
                            val isSuccess = task.result?.get("success") as? Boolean
                            if (isSuccess!!) {
                                closeActivity()
                                Toasty.normal(context,"인증 정보가 등록되었습니다", Toast.LENGTH_SHORT).show()
                              //  Timber.e("result = $result")
                            }else{
                                val message = task.result!!["message"] as? String
                                val builder = AlertDialog.Builder(context)
                                builder.setMessage(message)
                                builder.setPositiveButton(R.string.dialog_positive_check) { dialog, which ->
                                    dialog.dismiss()
                                    (context as AuthActivity).finish()
                                }
                                builder.create().show()
                                Timber.e("registerUserAuth fail: " + task.exception?.message)
                            }

                        } else {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage(R.string.dialog_function_error)
                            builder.setPositiveButton(R.string.dialog_positive_check, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                            builder.create().show()
                            Timber.e("signUpUsr fail: " + task.exception?.message)
                        }

                    }

                } else if (kind == "findId") {
                    loading.show()
                    FunctionEvents().findUserId(impUid).addOnCompleteListener { task ->
                        loading.dismiss()
                        if (task.isSuccessful) {
                            val isSuccess = task.result?.get("success") as? Boolean

                            Timber.e("task success =${task.result.toString()}")
                            //Timber.e("task success =${task.result?.get("accounts")}")
                            Timber.e("isSuccess =$isSuccess")
                            if (isSuccess!!) {
                                val result = task.result?.get("accounts") as? MutableList<*>
                                Timber.e("result = $result isEmpty = ${result?.isEmpty()}")
                                if(result?.isEmpty()!!){
                                    val goDone = Intent(context as AuthActivity, AuthFindIdDoneActivity::class.java)
                                    context.startActivity(goDone)
                                    (context as AuthActivity).finish()

                                }else{
                                    val resultDetail = result[0] as? Map<*,*>
                                    //Timber.e("resultDetail = $resultDetail isEmpty = ${result?.isEmpty()}")
                                    val email =resultDetail?.get("email") as? String
                                    //Timber.e("email = $email isEmpty = ${result?.isEmpty()}")
                                    val goDone = Intent(context as AuthActivity, AuthFindIdDoneActivity::class.java)
                                    goDone.putExtra("myEmail", email)
                                    context.startActivity(goDone)
                                    (context as AuthActivity).overridePendingTransition(R.anim.slide_from_right , R.anim.slide_to_left)
                                }

                            } else {
                                val message = task.result?.get("message") as? String
                                val goDone = Intent(context as AuthActivity, AuthFindIdDoneActivity::class.java)
                                context.startActivity(goDone)
                                (context as AuthActivity).finish()
                            }
                            //val result = task.result as MutableList<*>
                            //closeActivity()
                        } else {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage(R.string.dialog_function_error)
                            builder.setPositiveButton(R.string.dialog_positive_check, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                            builder.create().show()
                            Timber.e("signUpUsr fail: " + task.exception?.message)
                        }
                    }

                }

            }


//            val apiKey: String = context.getString(R.string.iamportAPiKey)
//            val apiSecretKey: String = context.getString(R.string.iamportAPiSecretKey)
//            val getData = iamportClient.token(AuthData(apiKey, apiSecretKey))
//            getData.enqueue(object : Callback<AccessToken> {
//                override fun onResponse(call: Call<AccessToken>?, response: Response<AccessToken>?) {
//                    if (response?.isSuccessful!!) {
//                        Timber.e("토큰은? ${response.body().response?.accessToken}")
//                        var token = response.body().response?.accessToken
//                        var getAuth = iamportClient.certification_by_imp_uid(token, impUid)
//                        getAuth.enqueue(object : Callback<Certification> {
//                            override fun onResponse(call: Call<Certification>?, response: Response<Certification>?) {
//                                if (response?.isSuccessful!!) {
//                                    Timber.e("인증정보 ${response.body().response?.name}")
//                                    var birth = response.body().response?.birth.toString()
//                                    Timber.e("birth=$birth")
//                                    val myBirth = getBirth(birth)
//                                    val gender = when (response.body().response?.gender) {
//                                        "male" -> 1
//                                        "female" -> 2
//                                        else -> 0
//                                    }
//
//                                }
//                            }
//
//                            override fun onFailure(call: Call<Certification>?, t: Throwable?) {
//
//                            }
//
//                        })
//
//                    }
//                }
//
//                override fun onFailure(call: Call<AccessToken>?, t: Throwable?) {
//
//                }
//
//            })
        }//가져올 매개변수타입 정의

        fun getBirth(birth: String): String? {
            val t = java.lang.Long.parseLong(birth + "000")
            val simpleDate = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
            simpleDate.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            return simpleDate.format(t)
        }

    }
}
