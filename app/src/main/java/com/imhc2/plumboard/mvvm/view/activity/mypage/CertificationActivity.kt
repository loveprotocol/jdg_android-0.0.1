package com.imhc2.plumboard.mvvm.view.activity.mypage

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.Spanned
import android.view.MenuItem
import android.widget.Toast
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.querys.FirestoreQuerys
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_certification.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CertificationActivity : AppCompatActivity() {

    var firestore = FirebaseFirestore.getInstance()
    var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certification)
        ButterKnife.bind(this)
        initView()

        activity_certification_authCl.setOnClickListener {
            FirestoreQuerys.getUserCrd(firestore,mAuth.currentUser?.uid)
                    .get()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            var authCheck = task.result?.get("jdg.ath.is") as? Boolean

                            if(authCheck!= null&& authCheck){
                                Toasty.normal(this,"인증 정보 변경은 불가능합니다",Toast.LENGTH_SHORT).show()
                            }else{
                                var intent = Intent(this, AuthActivity::class.java)
                                intent.putExtra("kind", "certification")
                                startActivity(intent)
                            }

                        }else{

                        }
                    }

        }

        activity_certification_locationCl.setOnClickListener {
            getLocDate()
        }


    }

    override fun onResume() {
        super.onResume()
        getStatus()
    }

    private fun getStatus() {
        FirestoreQuerys.getUserCrd(firestore, mAuth.currentUser?.uid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result?.exists()!!) {
                            val authCheck = task.result?.get("jdg.ath.is") as Boolean
                            val locCheck = task.result?.get("jdg.loc.is") as Boolean

                            if (authCheck) {
                                activity_certification_authTv.setTextColor(ContextCompat.getColor(this, R.color.colorPlumBoard))
                                activity_certification_authTv.text = getString(R.string.CertificationActivity_certificationDone)
                            } else {
                                activity_certification_authTv.setTextColor(Color.parseColor("#F44336"))
                                activity_certification_authTv.text = "미등록"
                            }

                            if (locCheck) {
                                activity_certification_locationTv.setTextColor(ContextCompat.getColor(this, R.color.colorPlumBoard))
                                activity_certification_locationTv.text = task.result?.get("jdg.loc.str")?.toString()
                            } else {
                                activity_certification_locationTv.setTextColor(Color.parseColor("#F44336"))
                                activity_certification_locationTv.text = "미등록"
                            }
                        }
                    } else {

                    }
                }
    }

    private fun getLocDate() {
        //startActivity(Intent(this, LocationActivity::class.java))
        firestore
                .collection("admPub").document("gen")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var locDate = task.result!!.get("jdgLocLock") as Long //30일
                        var maxDays = TimeUnit.DAYS.toMillis(locDate)   // days to millisecond
                        var beforeDay = System.currentTimeMillis() - maxDays

                        firestore
                                .collection("usrCrd").document(mAuth.currentUser!!.uid)
                                .get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                            val locCheck = task.result?.get("jdg.loc.is") as? Boolean
                                            if (locCheck!!) {
                                                val locInitTime = task.result?.get("jdg.loc.regAt") as? Long
                                                if (locInitTime!! >= beforeDay) {
                                                    var day = TimeUnit.MILLISECONDS.toDays(locInitTime - beforeDay)

                                                    Toasty.normal(this, "지역 변경은 ${day}일 후 가능합니다", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    startActivity(Intent(this, LocationActivity::class.java))
                                                }
                                            } else {
                                                startActivity(Intent(this, LocationActivity::class.java))
                                            }


                                    } else {

                                    }
                                }


                    } else {

                    }
                }
    }

    private fun dateCheck(whatDate: Long): String {
        val date = Date(whatDate)
        val simpleDate = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
        simpleDate.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return simpleDate.format(date)
    }

    private fun initView() {
        activity_certification_toolbar.title = getString(R.string.CertificationActivity_toolbarTitle)
        activity_certification_toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPlumBoardSub))
        setSupportActionBar(activity_certification_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firestore
                .collection("admPub").document("gen")
                .get()
                .addOnCompleteListener { task ->
                    var locDate = task.result!!.get("jdgLocLock") as Long //30일
                    activity_certification_tv3.text = addHtmlTest("-지역 정보는 등록 후 <font color='#3F51B5'>${locDate}일</font> 동안 <font color='#3F51B5'>변경 불가능</font>합니다")
                }

        activity_certification_tv1.text = addHtmlTest(getString(R.string.Certification_sub1))
        activity_certification_tv2.text = addHtmlTest(getString(R.string.Certification_sub2))


    }

    fun addHtmlTest(tv: String): Spanned? {
        val htmlAsSpanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(tv, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(tv)
        }
        return htmlAsSpanned
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
}
