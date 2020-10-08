package com.imhc2.plumboard.mvvm.view.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.imhc2.plumboard.R
import kotlinx.android.synthetic.main.activity_legal_notice.*

class LegalNoticeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legal_notice)
        initViews()
        events()
    }

    private fun events(){
        activity_legal_notice_termsServiceTv.setOnClickListener {
            val intent = Intent(this, LegalsWebViewActivity::class.java)
            intent.putExtra("address", getString(R.string.termsService))
            startActivity(intent)
        }
        activity_legal_notice_privacyPolicyTv.setOnClickListener {
            val intent2 = Intent(this, LegalsWebViewActivity::class.java)
            intent2.putExtra("address", getString(R.string.privacyPolicy))
            startActivity(intent2)
        }
    }

    private fun initViews(){
        activity_legal_notice_toolbar.apply {
            title=context.getString(R.string.LegalNoticeActivity_toolbarTitle)
            setTitleTextColor(ContextCompat.getColor(context, R.color.colorPlumBoardSub))
        }
        setSupportActionBar(activity_legal_notice_toolbar)
        supportActionBar.apply {
            this!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
