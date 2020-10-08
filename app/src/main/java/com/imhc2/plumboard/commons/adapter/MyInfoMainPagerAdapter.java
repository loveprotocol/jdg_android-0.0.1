package com.imhc2.plumboard.commons.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.imhc2.plumboard.mvvm.view.fragment.activityfragments.ActivityGraphFragment;
import com.imhc2.plumboard.mvvm.view.fragment.activityfragments.ActivityCampaignHistoryFragment;
import com.imhc2.plumboard.mvvm.view.fragment.activityfragments.ActivityPointManageFragment;

/**
 * Created by user on 2018-04-09.
 */

public class MyInfoMainPagerAdapter extends FragmentStatePagerAdapter{

    private int tabCount;

    public MyInfoMainPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ActivityGraphFragment();
            case 1:
                return new ActivityCampaignHistoryFragment();
            case 2:
                return new ActivityPointManageFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
