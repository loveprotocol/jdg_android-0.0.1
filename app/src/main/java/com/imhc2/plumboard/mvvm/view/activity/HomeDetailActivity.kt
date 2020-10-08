package com.imhc2.plumboard.mvvm.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.adapter.CampaignMainRecyclerAdapter
import com.imhc2.plumboard.mvvm.model.domain.CampExe
import kotlinx.android.synthetic.main.activity_home_detail.*

class HomeDetailActivity : AppCompatActivity() {

    lateinit var campExeList: MutableList<CampExe>
    internal var oneClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_detail)

        if (intent.hasExtra("campExe")) {
            campExeList = intent.getParcelableArrayListExtra("campExe")


            if (campExeList.isEmpty() || campExeList.size == 0) {
                activity_home_detail_emptyCl.visibility = View.VISIBLE
            } else {
                activity_home_detail_emptyCl.visibility = View.GONE
                initRv()
            }

        }
        initToolbar()
    }

    fun initToolbar() {
        activity_home_detail_toolbar.apply {
            title = context.getString(R.string.HomeDetailActivity_toolbarTitle)
            setTitleTextColor(ContextCompat.getColor(context, R.color.colorPlumBoardSub))
        }
        setSupportActionBar(activity_home_detail_toolbar)
        supportActionBar.apply {
            this?.setDisplayHomeAsUpEnabled(true)
            //this?.setHomeAsUpIndicator(R.drawable.ic_x)
        }
    }

    override fun onResume() {
        super.onResume()
        oneClick = false
    }

    private fun initRv() {
        //activity_home_detail_rv.layoutManager = LinearLayoutManager(this)
        activity_home_detail_rv.setHasFixedSize(true)
        var adapter = CampaignMainRecyclerAdapter(R.layout.layout_campaign, campExeList, null)
        //val emptyView = layoutInflater.inflate(R.layout.layout_campaign_empty, activity_home_detail_rv.parent as ViewGroup, false)
        //adapter.emptyView = emptyView
        adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, _, position ->
            if (oneClick) {
                return@OnItemClickListener
            }
            oneClick = true
            val intent = Intent(this, CampaignDetailActivity::class.java)
            intent.putExtra("campexe", campExeList.get(position))
            //Timber.e("homeMainFragment = id = " + campExeList.get(position).id!!)
            startActivity(intent)
        }
        activity_home_detail_rv.adapter = adapter


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
