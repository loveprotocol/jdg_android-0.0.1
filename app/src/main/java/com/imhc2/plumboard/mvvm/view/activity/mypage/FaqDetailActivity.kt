package com.imhc2.plumboard.mvvm.view.activity.mypage

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.imhc2.plumboard.R
import com.imhc2.plumboard.mvvm.model.domain.Faq
import kotlinx.android.synthetic.main.activity_faq_detail.*

class FaqDetailActivity : AppCompatActivity() {

    lateinit var faq:Faq

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq_detail)
        initToolbar()
        if(intent.hasExtra("faq")){
            faq = intent.getSerializableExtra("faq") as Faq
            activity_faq_detail_title.setText(faq.ttl)
            activity_faq_detail_content.setText(faq.bd)
        }
    }
    fun initToolbar(){
        activity_faq_detail_toolbar.apply {
            title = context.getString(R.string.FaqActivity_toolbarTitle)
            setTitleTextColor(ContextCompat.getColor(context, R.color.colorPlumBoardSub))
        }
        setSupportActionBar(activity_faq_detail_toolbar)
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
    }
}
