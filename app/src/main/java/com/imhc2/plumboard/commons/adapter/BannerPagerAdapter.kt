package com.imhc2.plumboard.commons.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.imhc2.plumboard.mvvm.model.domain.Banner
import com.imhc2.plumboard.mvvm.view.fragment.activityfragments.BannerFragment

class BannerPagerAdapter(fragmentManager: FragmentManager,var banners:MutableList<Banner>?) :
        FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return BannerFragment.newInstance(banners?.get(position)!!)
    }

    override fun getCount(): Int {
        return banners?.size!!
    }

}