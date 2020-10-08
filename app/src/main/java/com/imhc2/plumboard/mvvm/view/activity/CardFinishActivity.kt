package com.imhc2.plumboard.mvvm.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.querys.FirestoreQuerys
import kotlinx.android.synthetic.main.activity_card_finish.*
import java.text.NumberFormat

class CardFinishActivity : AppCompatActivity() {
    lateinit var firestore: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth
    var point: Int? = null
    private var boostPoint: Long? = null
    private var boostPercent: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_finish)
        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        val intent = intent
        point = intent.getIntExtra("point", 0)
        boostPoint = intent.getLongExtra("boostPoint", 0)
        boostPercent = intent.getDoubleExtra("boostPercent", 0.0)

        initData()

        activity_card_finish_floatingBtn.setOnClickListener { _ ->
            goToHome()
        }

    }

    private fun initData() {
        val nf = NumberFormat.getInstance()
        activity_card_finish_campaign_doneTv.text = point.toString()//nf.format(point)

        FirestoreQuerys.getUserCrd(firestore, mAuth.currentUser?.uid)
                .get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        val nowPoint = task.result?.get("jdg.pt.crtTot")?.toString()?.toDouble()
                        activity_card_finish_campaign_nowPointTv.text = nf.format(nowPoint)
                    } else {

                    }
                }

        activity_card_finish_tv.text = "${point!! + boostPoint!!} 포인트 적립"

        if (intent.getBooleanExtra("isTut", false)) {
            activity_card_finish_campaign_boostText.visibility = View.GONE
            activity_card_finish_campaign_boostTv.visibility = View.GONE
        } else {
            activity_card_finish_campaign_boostTv.text = boostPoint.toString()
            activity_card_finish_campaign_boostText.text = "플럼부스트(${Math.round(boostPercent!! * 100)}%)"
        }

    }

    private fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        goToHome()
    }
}
