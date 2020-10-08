package com.imhc2.plumboard.mvvm.view.fragment.activityfragments


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.imhc2.plumboard.R
import com.imhc2.plumboard.mvvm.model.domain.Banner
import com.imhc2.plumboard.mvvm.view.activity.auth.AuthMainActivity
import com.imhc2.plumboard.mvvm.view.activity.mypage.RecommenderActivity
import com.imhc2.plumboard.mvvm.view.activity.mypage.StampActivity
import kotlinx.android.synthetic.main.fragment_banner.view.*

private const val BANNER = "banner"

class BannerFragment : Fragment() {
    private var banner: Banner? = null
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            banner = it.getSerializable(BANNER) as Banner?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_banner, container, false)
        mAuth = FirebaseAuth.getInstance()

        view.fragment_banner_img.setOnClickListener {
            if (mAuth.currentUser?.uid != null) {
                when (banner?.act) {
                    "StampActivity" -> {
                        startActivity(Intent(activity, StampActivity::class.java))
                    }
                    "RecommenderActivity" -> {
                        startActivity(Intent(activity, RecommenderActivity::class.java))
                    }
                }
            } else {
                val builder = AlertDialog.Builder(activity)
                builder.setMessage(getString(R.string.dialog_title_needLogin))
                builder.setPositiveButton(getString(R.string.dialog_positive_check)) { _, _ ->
                    startActivity(Intent(activity,AuthMainActivity::class.java))
                }.setNegativeButton(getString(R.string.dialog_negative_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
            }


        }

        Glide.with(this).load(banner?.img).thumbnail(0.1f).apply(RequestOptions().placeholder(R.drawable.ic_campaign_empty).optionalCenterCrop()).into(view.fragment_banner_img)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Banner) =
                BannerFragment().apply {
                    arguments = Bundle().apply {
                        //putString(ARG_PARAM1, param1)
                        putSerializable(BANNER, param1)
                    }
                }
    }
}
