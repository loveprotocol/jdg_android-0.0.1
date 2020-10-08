package com.imhc2.plumboard.mvvm.view.activity

import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.adapter.CardHistoryAdapter
import com.imhc2.plumboard.commons.eventbus.EventBus
import com.imhc2.plumboard.commons.eventbus.Events
import com.imhc2.plumboard.mvvm.model.domain.CampExe
import com.imhc2.plumboard.mvvm.model.domain.CardHistoryItem
import kotlinx.android.synthetic.main.activity_card_history.*
import timber.log.Timber
import java.util.*

class CardHistoryActivity : AppCompatActivity() {

    lateinit var titles: ArrayList<String>
    lateinit var types: ArrayList<String>
    var list: MutableList<CardHistoryItem>? = mutableListOf()

    var position: Int = 0
    lateinit var resultMap: HashMap<Int, Any>
    lateinit var campExe: CampExe

    override fun onStart() {
        super.onStart()
        EventBus.get().register(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_history)
        initToolbar()
        getData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
        }
    }

    //@Subscribe
    private fun getData(/*event:Events.HistoryData*/) {
        if (intent.hasExtra("titles") && intent.hasExtra("types") && intent.hasExtra("resultMap")) {

                titles = intent.getStringArrayListExtra("titles")
                types = intent.getStringArrayListExtra("types")
                position = intent.getIntExtra("position", 0)
                resultMap = intent.getSerializableExtra("resultMap") as HashMap<Int, Any>
                campExe = intent.getParcelableExtra("campexe")

//            titles = event.titles as ArrayList<String>
//            types = event.types as ArrayList<String>
//            position = event.position
//            resultMap = event.resultMap as HashMap<Int, Any>
//            campExe = event.campExe

        }


//        var prjTitle = intent.getStringExtra("prj.ttl")
//        var campImg = intent.getStringExtra("camp.img")
//        var prjImg = intent.getStringExtra("prj.img")
//        var campTtl = intent.getStringExtra("camp.ttl")

        activity_card_history_title_tv.text = campExe.camp?.ttl//campTtl
        activity_card_history_project_tv.text = "게시자: ${campExe.prj?.ttl}"//"게시자: ${prjTitle}"

        Glide.with(this).load(campExe.camp?.img).apply(RequestOptions().override(1280, 720).optionalCenterCrop().placeholder(R.drawable.ic_campaign_empty)).into(activity_card_history_main_img)
        Glide.with(this).load(campExe.prj?.tImg).apply(RequestOptions().override(720, 480).circleCrop().placeholder(R.drawable.ic_campaign_empty)).into(activity_card_history_project_img)


        for (i in 0 until types.size) {
            var item = CardHistoryItem()
            item.type = types[i]
            item.title = titles[i]
            list?.add(item)
        }

        activity_card_history_rv.layoutManager = LinearLayoutManager(this)

        var adapter = CardHistoryAdapter(R.layout.layout_card_history_item, list, position, resultMap)
        adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, _, p ->
            Timber.e("현재 viewpager = $resultMap.size , 아이템 클릭 = $p")
            if (resultMap.size >= p) {
                EventBus.get().post(Events.HistoryPosition(p))
                finish()
            } else {

            }
        }
        activity_card_history_rv.adapter = adapter
    }

    fun initToolbar() {
        //activity_card_history_toolbar.title="콘텐츠 목록"
        setSupportActionBar(activity_card_history_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_x)
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
    override fun onDestroy() {
        super.onDestroy()
        EventBus.get().unregister(this)
    }
}
