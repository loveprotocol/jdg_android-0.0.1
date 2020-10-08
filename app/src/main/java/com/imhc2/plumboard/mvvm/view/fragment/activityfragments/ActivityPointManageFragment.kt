package com.imhc2.plumboard.mvvm.view.fragment.activityfragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.imhc2.plumboard.PlumBoardApp
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.adapter.BannerPagerAdapter
import com.imhc2.plumboard.commons.querys.FirestoreQuerys
import com.imhc2.plumboard.mvvm.model.domain.Banner
import com.imhc2.plumboard.mvvm.view.activity.activitypoint.PointStateActivity
import com.imhc2.plumboard.mvvm.view.activity.activitypoint.WithDrawMoneyActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_activity_point_manage.view.*

class ActivityPointManageFragment : Fragment() {


    lateinit var firestore: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // AndroidSupportInjection.inject(this);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_activity_point_manage, container, false)
        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        FirestoreQuerys.getBanners(firestore)
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        var banners =task.result?.toObjects(Banner::class.java)
                        //Timber.e("activityPointManagerFragment = result ${data}")
                        view.fragment_activity_point_manage_viewpager.adapter = BannerPagerAdapter(activity?.supportFragmentManager!!,banners)
                    }else{

                    }
                }


        view.fragment_activity_point_manage_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                view.fragment_activity_point_manage_indicatorView.selection = p0
            }

        })

        onViewClicked(view)

        return view
    }


    fun onViewClicked(view: View) {
        view.fragment_activity_point_manage_money_draw_tv.setOnClickListener {
            startActivity(Intent(activity, WithDrawMoneyActivity::class.java))
        }
        view.fragment_activity_point_manage_use_store_tv.setOnClickListener {
            Toasty.normal(context!!, getString(R.string.toast_ActivityPointManageFragment_preparing), Toast.LENGTH_SHORT).show()
        }
        view.fragment_activity_point_manage_money_draw_state_tv.setOnClickListener {
            startActivity(Intent(activity, PointStateActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val refWatcher = PlumBoardApp.getRefWatcher(activity!!)
        refWatcher.watch(this)
    }
}// Required empty public constructor
