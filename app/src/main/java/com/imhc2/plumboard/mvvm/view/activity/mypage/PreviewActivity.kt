package com.imhc2.plumboard.mvvm.view.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.adapter.CampaignMainRecyclerAdapter
import com.imhc2.plumboard.mvvm.model.domain.*
import com.imhc2.plumboard.mvvm.view.activity.CampaignDetailActivity
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : AppCompatActivity() {

    lateinit var firestore: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth
    lateinit var campExes: MutableList<CampExe>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        initViews()
        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        campExes = mutableListOf()

        firestore
                .collection("camp")
                .whereEqualTo("prv." + mAuth.currentUser!!.uid, true)
                .whereEqualTo("pub.is", false)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if(task.result?.isEmpty!!){
                            activity_preview_empty_cl.visibility= View.VISIBLE
                            activity_preview_rv.visibility=View.GONE
                        }else{
                            activity_preview_empty_cl.visibility= View.GONE
                            activity_preview_rv.visibility=View.VISIBLE
                        }

                        for (snapshot in task.result!!) {
                            val campExe = CampExe()
                            val camp = Camp()
                            val bgt = Bgt()
                            camp.cD=Cd(snapshot.get("camp.cD.age") as Boolean?,snapshot.get("camp.cD.gndr") as Boolean?,snapshot.get("camp.cD.loc") as Boolean?)
                            camp.ttl = snapshot.get("camp.ttl") as? String?
                            camp.bd = snapshot.get("camp.bd") as? String?
                            camp.img = snapshot.get("camp.img") as? String?
                            camp.type = (snapshot.get("camp.type") as? Long?)!!.toInt()
                            bgt.jdg = (snapshot.get("camp.bgt.pPC") as? Long?)!!.toInt()
                            camp.bgt = bgt
                            campExe.camp = camp
                            campExe.id=snapshot.get("camp.contRf").toString()
                            campExe.prj= Prj("https://firebasestorage.googleapis.com/v0/b/plumboard-aacb2.appspot.com/o/tImg%2Fdefault%2Fdefault.png?alt=media&token=f139835d-5f55-48e4-a3c7-9367a08073f4","미리보기 캠페인")
                            campExes.add(campExe)
                        }

                        val recyclerAdapter = CampaignMainRecyclerAdapter(R.layout.layout_campaign, campExes, null)
                        recyclerAdapter.setOnItemClickListener { _, _, position ->
                            val intent = Intent(this, CampaignDetailActivity::class.java)
                            intent.putExtra("campexe", campExes[position])
                            intent.putExtra("isFree", true)
                            startActivity(intent)
                        }
                        activity_preview_rv.layoutManager = LinearLayoutManager(this)
                        activity_preview_rv.adapter = recyclerAdapter
                    } else {

                    }
                }


    }

    private fun initViews() {
        activity_preview_toolbar.title = getString(R.string.PreviewActivity_toolbarTitle)
        activity_preview_toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPlumBoardSub))
        setSupportActionBar(activity_preview_toolbar)
        supportActionBar.apply {
            this!!.setDisplayHomeAsUpEnabled(true)
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
