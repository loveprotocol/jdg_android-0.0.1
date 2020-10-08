package com.imhc2.plumboard.mvvm.view.fragment.activityfragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.imhc2.plumboard.PlumBoardApp
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.adapter.CampaignMainRecyclerAdapter
import com.imhc2.plumboard.mvvm.model.domain.CampExe
import com.imhc2.plumboard.mvvm.view.activity.CampaignDetailActivity
import kotlinx.android.synthetic.main.fragment_activity_campaign_history.*
import kotlinx.android.synthetic.main.fragment_activity_campaign_history.view.*
import timber.log.Timber
import java.util.*

class ActivityCampaignHistoryFragment : Fragment() {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private var myParticipationList: MutableList<String> = ArrayList()
    private var doneNDoingList: MutableList<CampExe> = ArrayList()
    private lateinit var recyclerAdapter: CampaignMainRecyclerAdapter
    private var campIdNSts: MutableMap<String, Int?> = mutableMapOf()
    private var oneClick = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_activity_campaign_history, container, false)
        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser != null) {
            getDoneIngCampaign(view)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        oneClick = false
    }

    private fun getDoneIngCampaign(view: View) {

        firestore
                .collection("campPart")
                .whereEqualTo(FieldPath.of("inf", "cB"), mAuth.currentUser?.uid)
                .whereLessThanOrEqualTo("sts", 2)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (snapshot in task.result!!) {
                            if (snapshot.exists()) {
                                val data = snapshot.get(FieldPath.of("inf", "campRf")) as String?
                                var sts = (snapshot.get("sts") as Long).toInt()
                                myParticipationList.add(data!!)

                                campIdNSts.put(data, sts)
                                campIdNSts.toList().sortedBy { (key, value) -> value }.toMap()
                            }
                        }

                        firestore
                                .collection("campExe")
                                //.whereLessThanOrEqualTo("stsDtl",2)
                                .get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        for (snapshot in task.result!!) {
                                            if (snapshot.exists()) {
                                                val campExe = snapshot.toObject(CampExe::class.java)
                                                campExe.id = snapshot.id

                                                if (myParticipationList.contains(campExe.id!!)) {
                                                    if (campIdNSts[campExe.id!!] == 1) {
                                                        doneNDoingList.add(0, campExe)
                                                    } else if (campIdNSts[campExe.id!!] == 2) {
                                                        doneNDoingList.add(campExe)
                                                    }
                                                }

                                            }
                                        }
                                        Timber.e("campIdNSts = ${campIdNSts}")
                                        recyclerAdapter = CampaignMainRecyclerAdapter(R.layout.layout_campaign, doneNDoingList, campIdNSts)
                                        //val emptyView = layoutInflater.inflate(R.layout.layout_campaign_empty, fragment_activity_campaign_history_rv.parent as ViewGroup, false)
                                        //recyclerAdapter.emptyView=emptyView
                                        recyclerAdapter.setOnItemClickListener { _, _, position ->
                                            if (oneClick) {
                                                return@setOnItemClickListener
                                            }
                                            oneClick = true
                                            //같은거 분류하기
                                            val intent = Intent(activity, CampaignDetailActivity::class.java)
                                            intent.putExtra("campexe", doneNDoingList[position])
                                            intent.putExtra("campHistory", campIdNSts[doneNDoingList[position].id])
                                            startActivity(intent)
                                        }
                                        if (doneNDoingList.size == 0) {
                                            view.fragment_activity_campaign_history_empty_cl.visibility = View.VISIBLE
                                            fragment_activity_campaign_history_rv.visibility = View.GONE
                                        } else {
                                            fragment_activity_campaign_history_rv.adapter = recyclerAdapter
                                        }


                                    }
                                }

                    } else {
                        Timber.e(task.exception?.message)
                    }
                }

    }

    override fun onDestroy() {
        super.onDestroy()
        val refWatcher = PlumBoardApp.getRefWatcher(activity!!)
        refWatcher.watch(this)
    }
}// Required empty public constructor
