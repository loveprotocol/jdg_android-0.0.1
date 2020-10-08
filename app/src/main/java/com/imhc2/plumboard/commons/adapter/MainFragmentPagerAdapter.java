package com.imhc2.plumboard.commons.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.imhc2.plumboard.mvvm.view.fragment.CampaignMainFragment;
import com.imhc2.plumboard.mvvm.view.fragment.HomeMainFragment;
import com.imhc2.plumboard.mvvm.view.fragment.ActivityMainFragment;

/**
 * Created by user on 2018-09-04.
 */

public class MainFragmentPagerAdapter extends FragmentStatePagerAdapter{

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeMainFragment();
            case 1:
                return new CampaignMainFragment();
            case 2:
                return new ActivityMainFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
