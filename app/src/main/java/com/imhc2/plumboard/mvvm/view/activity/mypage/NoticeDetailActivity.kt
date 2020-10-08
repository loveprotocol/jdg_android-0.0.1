package com.imhc2.plumboard.mvvm.view.activity.mypage

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.imhc2.plumboard.R
import com.imhc2.plumboard.mvvm.model.domain.Notice
import kotlinx.android.synthetic.main.activity_notice_detail.*

class NoticeDetailActivity : AppCompatActivity() {
    lateinit var notice:Notice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_detail)
        initToolbar()

        if(intent.hasExtra("notice")){
            notice = intent.getSerializableExtra("notice") as Notice
            activity_notice_detail_title.setText(notice.ttl)
            activity_notice_detail_content.setText(notice.bd)
        }

    }

    fun initToolbar(){
        activity_notice_detail_toolbar.apply {
            title = getString(R.string.NoticeActivity_toolbarTitle)
            setTitleTextColor(ContextCompat.getColor(context, R.color.colorPlumBoardSub))
        }
        setSupportActionBar(activity_notice_detail_toolbar)
        supportActionBar.apply {
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left , R.anim.slide_to_right);
    }
}
