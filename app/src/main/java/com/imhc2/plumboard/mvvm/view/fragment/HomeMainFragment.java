package com.imhc2.plumboard.mvvm.view.fragment;


import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.common.eventbus.Subscribe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imhc2.plumboard.PlumBoardApp;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.adapter.BannerPagerAdapter;
import com.imhc2.plumboard.commons.adapter.MainHomeRecyclerAdapter;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.commons.util.SharedPreferencesHelper;
import com.imhc2.plumboard.mvvm.model.domain.Banner;
import com.imhc2.plumboard.mvvm.model.domain.Bgt;
import com.imhc2.plumboard.mvvm.model.domain.Camp;
import com.imhc2.plumboard.mvvm.model.domain.CampExe;
import com.imhc2.plumboard.mvvm.model.domain.Cd;
import com.imhc2.plumboard.mvvm.model.domain.Prj;
import com.imhc2.plumboard.mvvm.model.domain.Rtg;
import com.imhc2.plumboard.mvvm.model.domain.Tutorial;
import com.imhc2.plumboard.mvvm.model.domain.enums.HomeCampaignType;
import com.imhc2.plumboard.mvvm.view.activity.CampaignDetailActivity;
import com.imhc2.plumboard.mvvm.view.activity.HomeDetailActivity;
import com.imhc2.plumboard.mvvm.view.activity.MainActivity;
import com.imhc2.plumboard.mvvm.view.activity.auth.AuthMainActivity;
import com.imhc2.plumboard.mvvm.view.activity.mypage.GuideActivity;
import com.imhc2.plumboard.mvvm.viewmodel.HomeCampaignViewModel;
import com.rd.PageIndicatorView;
import com.squareup.leakcanary.RefWatcher;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

public class HomeMainFragment extends Fragment {

    //@BindView(R.id.fragment_home_toolbar) Toolbar homeToolbar;
    Unbinder unbinder;
    @BindView(R.id.mainHomeRecyclerViewPopularCampaign) RecyclerView RecyclerViewPopularCampaign;
    @BindView(R.id.fragment_home_my_point) TextView myPoint;
    @BindView(R.id.fragment_home_appBarLayout) AppBarLayout appBarLayout;
    //@BindView(R.id.fragment_home_sv) NestedScrollView mSv;
    @BindView(R.id.fragment_home_loading) LottieAnimationView loadingProgress;
    @BindView(R.id.fragment_home_viewpager) ViewPager mViewPager;
    @BindView(R.id.fragment_home_empty_cl) ConstraintLayout mEmptyView;
    @BindView(R.id.fragment_activity_point_manage_indicatorView) PageIndicatorView mIndicatorView;
    @BindView(R.id.fragment_home_tutorialRv) RecyclerView mTutorialRv;
    @BindView(R.id.fragment_home_tutorialCl) ConstraintLayout mTutorialCl;
    @BindView(R.id.fragment_home_line) View mHomeLine;
    @BindView(R.id.fragment_home_bannerFl) FrameLayout bannerFl;
    @BindView(R.id.fragment_home_popularCl) ConstraintLayout mPopularCl;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ArrayList<CampExe> campExeLists;
    //ProgressDialogHelper progressDialogHelper;
    private boolean oneClick = false;
    private boolean isViewShown = true;
    ArrayList<CampExe> tutorialExeLists = new ArrayList<>();
    Boolean tut1Check, tut2Check;

    public HomeMainFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        //firestore = ((PlumBoardApp) getActivity().getApplication()).getFirestore();

        RecyclerViewPopularCampaign.setNestedScrollingEnabled(false);
        RecyclerViewPopularCampaign.setHasFixedSize(true);
        RecyclerViewPopularCampaign.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        //progressDialogHelper = new ProgressDialogHelper(getContext());
        initToolbar();
        initBanner();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private void initBanner() {
        FirestoreQuerys.INSTANCE.getBanners(firestore)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Banner> banners = task.getResult().toObjects(Banner.class);
                        mViewPager.setAdapter(new BannerPagerAdapter(getActivity().getSupportFragmentManager(), banners));
                    }
                });


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mIndicatorView.setSelection(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    @Subscribe
    public void eventScrollDown(Events.ScrollDown scrollDown) {
        appBarLayout.setExpanded(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        oneClick = false;
        getPoint();
        EventBus.get().register(this);
        Timber.e("homeMainFragment = onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.get().unregister(this);
    }

    private void getCampaigns() {
        //progressDialogHelper.show();
        if (mAuth.getCurrentUser() != null) {//로그인한유저
            FirestoreQuerys.INSTANCE.getUserCrd(firestore, mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                tut1Check = (Boolean) task.getResult().get("jdg.tut1");
                                tut2Check = (Boolean) task.getResult().get("jdg.tut2");

                                Boolean authCheck = (Boolean) task.getResult().get("jdg.ath.is");
                                Boolean locCheck = (Boolean) task.getResult().get("jdg.loc.is");
                                Timber.e("home-인증여부:" + authCheck);
                                HomeCampaignViewModel model = ViewModelProviders.of(getActivity()).get(HomeCampaignViewModel.class);
                                if (authCheck) {
                                    Timber.e("HomeMain:auth인증");
                                    if (locCheck) {
                                        Timber.e("HomeMain:지역 인증");
                                        model.getCampaigns(HomeCampaignType.AUTHLOC).observe(getActivity(), this::campaignSort);
                                    } else {
                                        Timber.e("HomeMain:지역 비인증");
                                        model.getCampaigns(HomeCampaignType.AUTHNOTLOC).observe(getActivity(), this::campaignSort);
                                    }

                                } else {
                                    Timber.e("HomeMain:auth 비인증");
                                    if (locCheck) {
                                        Timber.e("HomeMain:지역 인증");
                                        model.getCampaigns(HomeCampaignType.NOTAUTHLOC).observe(getActivity(), this::campaignSort);
                                    } else {
                                        Timber.e("HomeMain:지역 비인증");
                                        model.getCampaigns(HomeCampaignType.NOTAUTHNOTLOC).observe(getActivity(), this::campaignSort);
                                    }
                                }


                            } else {
                                Timber.e("homeMainFragment exists() =" + task.getResult().exists());
                                //getCampaigns();
                            }
                        }
                    });

        } else {//비로그인
            Timber.e("HomeMain:비로그인");
//            HomeCampaignViewModel model = ViewModelProviders.of(getActivity()).get(HomeCampaignViewModel.class);
//            model.getCampaigns(HomeCampaignType.NOUSER).observe(this, campExes -> {
//                initRecyclerView(campExes);
//                campExeLists = (ArrayList<CampExe>) campExes;
//            });

            List<CampExe> list = new ArrayList<>();
            FirestoreQuerys.INSTANCE.notUserGetCampaignData(firestore)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                CampExe campExe = snapshot.toObject(CampExe.class);
                                campExe.setId(snapshot.getId());
                                list.add(campExe);
                                if (list.size() == 10) {
                                    break;
                                }
                            }
                            initRecyclerView(list);
                            campExeLists = (ArrayList<CampExe>) list;

                        } else {
                            Timber.e(task.getException().getMessage());
                        }
                    });

        }
    }


    private void getTutorial(Boolean tut1, Boolean tut2) {
        Timber.e("tut1 =" + tut1 + ", tut2 =" + tut2);
        if (tut1) {
            if (tut2) {

            } else {
                FirestoreQuerys.INSTANCE.getTut2(firestore)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Tutorial tutorial = snapshot.toObject(Tutorial.class);
                                    CampExe campExe = new CampExe(null, null, null, null, null, new Prj(tutorial.getPubTImg(), tutorial.getPubNm()), new Camp(tutorial.getTtl(), tutorial.getBd(), tutorial.getImg(), 2, new Bgt(null, null, tutorial.getPPC(), null, null), null, new Cd(true, true, true), null, null, new Rtg(tutorial.getRtg().getAvg(), tutorial.getRtg().getCt(), tutorial.getRtg().getCum())), tutorial.getCDataRf());
                                    tutorialExeLists.add(campExe);
                                }

                                mTutorialCl.setVisibility(View.VISIBLE);
                                mHomeLine.setVisibility(View.VISIBLE);
                            }
                        });
            }
        } else {
            if (tut2) {

                FirestoreQuerys.INSTANCE.getTut1(firestore)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Tutorial tutorial = snapshot.toObject(Tutorial.class);
                                    CampExe campExe = new CampExe(null, null, null, null, null, new Prj(tutorial.getPubTImg(), tutorial.getPubNm()), new Camp(tutorial.getTtl(), tutorial.getBd(), tutorial.getImg(), 2, new Bgt(null, null, tutorial.getPPC(), null, null), null, new Cd(false, false, false), null, null, new Rtg(tutorial.getRtg().getAvg(), tutorial.getRtg().getCt(), tutorial.getRtg().getCum())), tutorial.getCDataRf());
                                    tutorialExeLists.add(campExe);
                                }

                                mTutorialCl.setVisibility(View.VISIBLE);
                                mHomeLine.setVisibility(View.VISIBLE);
                            }
                        });
            } else {
                FirestoreQuerys.INSTANCE.getJdgTut(firestore)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Tutorial tutorial = snapshot.toObject(Tutorial.class);
                                    CampExe campExe = new CampExe(null, null, null, null, null, new Prj(tutorial.getPubTImg(), tutorial.getPubNm()), new Camp(tutorial.getTtl(), tutorial.getBd(), tutorial.getImg(), 2, new Bgt(null, null, tutorial.getPPC(), null, null), null, new Cd(false, false, false), null, null, new Rtg(tutorial.getRtg().getAvg(), tutorial.getRtg().getCt(), tutorial.getRtg().getCum())), tutorial.getCDataRf());
                                    tutorialExeLists.add(campExe);
                                }
                                mTutorialCl.setVisibility(View.VISIBLE);
                                mHomeLine.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }

        mTutorialRv.setNestedScrollingEnabled(false);
        mTutorialRv.setHasFixedSize(true);
        mTutorialRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        MainHomeRecyclerAdapter mainHomeRecyclerVideoAdapter = new MainHomeRecyclerAdapter(R.layout.layout_mainhome, tutorialExeLists, getActivity());
        mainHomeRecyclerVideoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), CampaignDetailActivity.class);
                intent.putExtra("campexe", tutorialExeLists.get(position));
                intent.putExtra("isTut", true);
                startActivity(intent);
            }
        });
        mTutorialRv.setAdapter(mainHomeRecyclerVideoAdapter);
    }

    private void campaignSort(List<CampExe> campExes) {
        Timber.e("homeMainFragment size = " + campExes.size());

        Collections.sort(campExes, (campExeList, t1) -> t1.getCamp().getPop().getTot().compareTo(campExeList.getCamp().getPop().getTot()));//수동정렬 인기

        List<CampExe> campTen = new ArrayList<>();

        if (campExes.size() > 8) {
            for (int i = 0; i < campExes.size(); i++) {
                campTen.add(campExes.get(i));
                if (campTen.size() == 10) {
                    break;
                }
            }
        } else {
            campTen = campExes;
        }


        Timber.e("campTen.size() =" + campTen.size());

        initRecyclerView(campTen);
        campExeLists = (ArrayList<CampExe>) campTen;
    }

    public void getPoint() {
        if (mAuth.getCurrentUser() != null) {
            FirestoreQuerys.INSTANCE.getUserCrd(firestore, mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Double point = Double.parseDouble(document.get(FieldPath.of("jdg", "pt", "crtTot")).toString());
                                myPoint.setText(NumberFormat.getInstance().format(point.intValue()));
                                SharedPreferencesHelper.put(getActivity(), "point", point.intValue());
                            }
                        }
                    });
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.home_main_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void initToolbar() {
        //setHasOptionsMenu(true);

//        homeToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.homeMainProfile:
//                        if (mAuth.getCurrentUser() != null) {
//                            ((MainActivity) getActivity()).gotoActivity(ProfileMainActivity.class);
//                        } else {
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//                            builder.setMessage(R.string.dialog_title_needLogin);
//                            builder.setPositiveButton(R.string.dialog_positive_check, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //item.setIntent(new Intent(MainActivity.this, AuthMainActivity.class));
//                                    ((MainActivity) getActivity()).gotoActivity(AuthMainActivity.class);
//
//                                }
//                            }).setNegativeButton(R.string.dialog_negative_cancel, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            }).create().show();
//
//
//                        }
//                        return true;
////                    case R.id.homeMainAlarm:
////                        if (mAuth.getCurrentUser() != null) {
////                            startActivity(new Intent(getActivity(), AlarmActivity.class));
////                        }else{
////                            AlertDialog.Builder builder = ((MainActivity) getActivity()).getBuilder();
////                            builder.setMessage(R.string.dialog_title_needLogin);
////                            builder.setPositiveButton(R.string.dialog_positive_check, new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialog, int which) {
////                                    //item.setIntent(new Intent(MainActivity.this, AuthMainActivity.class));
////                                    ((MainActivity) getActivity()).gotoActivity(AuthMainActivity.class);
////                                }
////                            }).setNegativeButton(R.string.dialog_negative_cancel, new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialog, int which) {
////                                    dialog.dismiss();
////                                }
////                            }).create().show();
////
////                        }
////
////                        return true;
//                }
//                return false;
//            }
//        });
    }

    public void initRecyclerView(List<CampExe> campExeList) {
        if (campExeList.isEmpty() || campExeList.size()==0) {
            mEmptyView.setVisibility(View.VISIBLE);
            RecyclerViewPopularCampaign.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            RecyclerViewPopularCampaign.setVisibility(View.VISIBLE);
        }

        MainHomeRecyclerAdapter mainHomeRecyclerVideoAdapter = new MainHomeRecyclerAdapter(R.layout.layout_mainhome, campExeList, getActivity());
        mainHomeRecyclerVideoAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //View emptyView = getLayoutInflater().inflate(R.layout.layout_campaign_empty,(ViewGroup)RecyclerViewPopularCampaign.getParent(),false);
        //mainHomeRecyclerVideoAdapter.setEmptyView(emptyView);
        RecyclerViewPopularCampaign.setAdapter(mainHomeRecyclerVideoAdapter);
        //RecyclerViewPopularCampaign.
        //RecyclerViewPopularCampaign.setNestedScrollingEnabled(true);
        mainHomeRecyclerVideoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (oneClick) {
                    return;
                }
                oneClick = true;
                Intent intent = new Intent(getActivity(), CampaignDetailActivity.class);
                intent.putExtra("campexe", campExeList.get(position));
                intent.putExtra("isTut", false);
                Timber.e("homeMainFragment = id = " + campExeList.get(position).getId());
                startActivity(intent);
            }
        });
        //progressDialogHelper.dismiss();
        //progressFragment.dismiss();
        Timber.e("loadingProgress 실행");
        loadingProgress.setVisibility(View.GONE);
        if (mAuth.getCurrentUser() != null) {
            getTutorial(tut1Check, tut2Check);
        }
        appBarLayout.setVisibility(View.VISIBLE);
        //mSv.setVisibility(View.VISIBLE);
        bannerFl.setVisibility(View.VISIBLE);
        mPopularCl.setVisibility(View.VISIBLE);
        setBottom();
    }

    private void setBottom(){
        ((MainActivity) getActivity()).getBottomNavi().getMenu().getItem(0).setEnabled(true);
        ((MainActivity) getActivity()).getBottomNavi().getMenu().getItem(1).setEnabled(true);
        ((MainActivity) getActivity()).getBottomNavi().getMenu().getItem(2).setEnabled(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isViewShown) {
                getCampaigns();
                isViewShown = false;
            }else{
                setBottom();
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

    @OnClick({R.id.fragment_home_my_point, R.id.fragment_home_popular_moreCl, R.id.fragment_home_tutorialMoreCl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_home_my_point:
                if (mAuth.getCurrentUser() != null) {
                    EventBus.get().post(new Events.HomeToActivityPage());
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.dialog_title_needLogin));
                    builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getActivity(), AuthMainActivity.class));
                        }
                    }).setNegativeButton(getString(R.string.dialog_negative_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
                break;

            case R.id.fragment_home_popular_moreCl:
                Intent intent = new Intent(getActivity(), HomeDetailActivity.class);
                intent.putExtra("campExe", campExeLists);
                startActivity(intent);
                break;

            case R.id.fragment_home_tutorialMoreCl:
                Intent intent2 = new Intent(getActivity(), GuideActivity.class);
                startActivity(intent2);
                break;
        }
    }

}
