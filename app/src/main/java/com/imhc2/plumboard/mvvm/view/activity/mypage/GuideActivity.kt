package com.imhc2.plumboard.mvvm.view.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.adapter.CampaignMainRecyclerAdapter
import com.imhc2.plumboard.commons.querys.FirestoreQuerys
import com.imhc2.plumboard.mvvm.model.domain.*
import com.imhc2.plumboard.mvvm.view.activity.CampaignDetailActivity
import kotlinx.android.synthetic.main.activity_guide.*

class GuideActivity : AppCompatActivity() {

    lateinit var firestore: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth
    var tut1: Boolean = false
    var tut2: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()


        initToolbar()
        getData()

    }

    private fun initToolbar() {
        activity_guide_toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPlumBoardSub))
        activity_guide_toolbar.title = getString(R.string.GuideActivity_toolbarTitle)
        setSupportActionBar(activity_guide_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getData() {

        activity_guide_rv.isNestedScrollingEnabled = false
        activity_guide_rv.setHasFixedSize(true)
        activity_guide_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        var tutCheck = mutableListOf<Int>()

        FirestoreQuerys.getUserCrd(firestore, mAuth.currentUser?.uid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        tut1 = (task.result?.get("jdg.tut1") as? Boolean)!!
                        tut2 = (task.result?.get("jdg.tut2") as? Boolean)!!

                        val tutorialExeLists = mutableListOf<CampExe>()
                        FirestoreQuerys.getJdgTut(firestore)
                                .get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        for ((i, snapshot) in task.result!!.withIndex()) {
                                            if (snapshot.exists()) {
                                                val tutorial = snapshot.toObject(Tutorial::class.java)

                                                if (tut1) {//tut1함
                                                    if (tut2) {//tut2함
                                                        tutCheck.add(0, 0)
                                                        tutCheck.add(1, 0)
                                                    } else {//tut1함 tut2 안함
                                                        tutCheck.add(0, 0)
                                                        tutCheck.add(1, tutorial.pPC!!)
                                                    }
                                                } else {//tut1안함
                                                    if (tut2) {//tut1안함 tut2 함
                                                        tutCheck.add(0, tutorial.pPC!!)
                                                        tutCheck.add(1, 0)
                                                    } else {//tut1,2 안함
                                                        tutCheck.add(0, tutorial.pPC!!)
                                                        tutCheck.add(1, tutorial.pPC!!)
                                                    }
                                                }

                                                val campExe = CampExe(null, null, null, null, null, Prj(tutorial.pubTImg, tutorial.pubNm), Camp(tutorial!!.ttl, tutorial!!.bd, tutorial!!.img, 2, Bgt(null, null, tutCheck[i], null, null), null, Cd(false, false, false), null, null, Rtg(tutorial!!.rtg!!.avg, tutorial!!.rtg!!.ct, tutorial!!.rtg!!.cum)), tutorial!!.cDataRf)
                                                tutorialExeLists.add(campExe)

                                            }
                                        }
                                        val recyclerAdapter = CampaignMainRecyclerAdapter(R.layout.layout_campaign, tutorialExeLists, null)
                                        recyclerAdapter.setOnItemClickListener { _, _, position ->
                                            if (tutCheck[position] != 0) {
                                                val intent = Intent(this, CampaignDetailActivity::class.java)
                                                intent.putExtra("campexe", tutorialExeLists[position])
                                                intent.putExtra("isTut", true)
                                                intent.putExtra("isFree", false)
                                                startActivity(intent)
                                            } else {
                                                val intent = Intent(this, CampaignDetailActivity::class.java)
                                                intent.putExtra("campexe", tutorialExeLists[position])
                                                intent.putExtra("isTut", true)
                                                intent.putExtra("isFree", true)
                                                startActivity(intent)
                                            }
                                        }
                                        activity_guide_rv.adapter = recyclerAdapter
                                    }
                                }

                    } else {

                    }
                }


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
