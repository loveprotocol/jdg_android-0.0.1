package com.imhc2.plumboard.mvvm.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.eventbus.Subscribe;
import com.imhc2.plumboard.PlumBoardApp;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.adapter.MyInfoMainPagerAdapter;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.squareup.leakcanary.RefWatcher;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ActivityMainFragment extends Fragment{

    @BindView(R.id.fragment_my_info_main_tab_layout) TabLayout tabLayout;
    @BindView(R.id.fragment_my_info_main_tab_view_pager) ViewPager viewPager;
    Unbinder unbinder;
    private boolean isViewShown = true;

    public ActivityMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        //AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.get().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.get().unregister(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_activity_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Subscribe
    private void CheckActivityTab(Events.ActivityToPoint nul){
        viewPager.setCurrentItem(0);
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
    }


    private void initTabLayout(){
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.drawable_tablayout_graph_select));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.drawable_tablayout_speaker_select));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.drawable_tablayout_store_select));

        MyInfoMainPagerAdapter adapter =new MyInfoMainPagerAdapter(getFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                //ImageView imageView =tabView.findViewById(R.id.icon);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.activity_main_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(isViewShown){
                initTabLayout();
                isViewShown=false;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = PlumBoardApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
