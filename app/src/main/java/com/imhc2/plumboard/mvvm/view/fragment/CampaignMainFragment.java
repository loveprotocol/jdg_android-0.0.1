package com.imhc2.plumboard.mvvm.view.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.common.eventbus.Subscribe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.imhc2.plumboard.PlumBoardApp;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.adapter.CampaignMainPagingAdapter;
import com.imhc2.plumboard.commons.adapter.CampaignMainRecyclerAdapter;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.commons.util.SharedPreferencesHelper;
import com.imhc2.plumboard.mvvm.model.domain.CampExe;
import com.imhc2.plumboard.mvvm.model.domain.enums.HomeCampaignType;
import com.imhc2.plumboard.mvvm.view.activity.CampaignDetailActivity;
import com.imhc2.plumboard.mvvm.view.activity.MainActivity;
import com.imhc2.plumboard.mvvm.view.fragment.bottomsheets.AlignFragment;
import com.imhc2.plumboard.mvvm.view.fragment.bottomsheets.FilterFragment;
import com.imhc2.plumboard.mvvm.viewmodel.CampaignMainViewModel;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

public class CampaignMainFragment extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.fragment_campaign_main_recycler_view) RecyclerView mainRecyclerView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Query query;
    PagedList.Config config;

    CampExe campExe;
    CampaignMainPagingAdapter adapter;

    List<CampExe> campExeList = new ArrayList<>();
    List<CampExe> campExeListDone = new ArrayList<>();
    CampaignMainRecyclerAdapter recyclerAdapter;

    @BindView(R.id.fragment_campaign_main_filter_btn) Button mFilterBtn;
    @BindView(R.id.fragment_campaign_main_align_btn) Button mAlignBtn;
    @BindView(R.id.fragment_campaign_main_swipe_refresh) MaterialRefreshLayout swipeRefresh;

    View emptyView;
    @BindView(R.id.fragment_campaign_main_loading) LottieAnimationView mLoading;
    @BindView(R.id.fragment_campaign_main_Fl) FrameLayout mFl;
    @BindView(R.id.fragment_campaign_main_empty_cl) ConstraintLayout emptyViewCl;

    private boolean isViewShown = true;

    private final static String MAINFILTER = "CampaignMainFragment_filter";
    private final static String MAINALIGN = "CampaignMainFragment_align";
    private boolean oneClick = false;
    View view;

    public CampaignMainFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        oneClick = false;
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
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_campaign_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        //firestore = ((PlumBoardApp) getActivity().getApplication()).getFirestore();
        config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(30)
                .setPageSize(15)
                .build();

        initRv();
        return view;
    }

    private void initRv() {
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && ((MainActivity) getActivity()).getBottomNavi().isShown()) {
                    //((MainActivity) getActivity()).getBottomNavi().setVisibility(View.GONE);
                    mFilterBtn.setVisibility(View.GONE);
                    mAlignBtn.setVisibility(View.GONE);
                } else if (dy < 0) {
                    //((MainActivity) getActivity()).getBottomNavi().setVisibility(View.VISIBLE);
                    mFilterBtn.setVisibility(View.VISIBLE);
                    mAlignBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        recyclerAdapter = new CampaignMainRecyclerAdapter(R.layout.layout_campaign, campExeList, null);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //emptyView = getLayoutInflater().inflate(R.layout.layout_campaign_empty, (ViewGroup) mainRecyclerView.getParent(), false);

        swipeRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerAdapter.getData().clear();
                        campExeList.clear();
                        campExeListDone.clear();
                        getCampaigns();
                        materialRefreshLayout.finishRefresh();
                    }
                }, 2000);
                materialRefreshLayout.finishRefreshLoadMore();
            }

            @Override
            public void onfinish() {
                super.onfinish();
            }
        });
    }


    @Subscribe
    public void getChangeList(Events.ListChange check) {
        if (check.isCampaignKind()) {
            if (mAuth.getCurrentUser() != null) {
                recyclerAdapter = new CampaignMainRecyclerAdapter(R.layout.layout_campaign, null, null);
                filterCheck();
            } else {
                //adapter = new CampaignMainPagingAdapter(firestorePagingOptionsCustom(query), 1);
                //initRecyclerView(adapter);
                noUserGetData();
            }

        } else {
            if (mAuth.getCurrentUser() != null) {
                recyclerAdapter = new CampaignMainRecyclerAdapter(R.layout.layout_campaign_m, null, null);
                mainRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
                filterCheck();
            } else {
                //adapter = new CampaignMainPagingAdapter(firestorePagingOptionsCustom(query), 0);
                //initRecyclerView(adapter);
                noUserGetData();
            }

        }
    }

    @Subscribe
    public void CampaignScrollUp(Events.CampaignScrollUp up) {
        LinearLayoutManager manager = (LinearLayoutManager) mainRecyclerView.getLayoutManager();
        Timber.e("mainRecyclerView.findFirstVisibleItemPosition() = " + manager.findFirstVisibleItemPosition());

        if ((manager.findFirstVisibleItemPosition() > 0)) {
            int top = 0;
            mainRecyclerView.smoothScrollToPosition(top);
        }

    }


    private void getCampaigns() {
        if (mAuth.getCurrentUser() != null) {//로그인한유저
            FirestoreQuerys.INSTANCE.getUserCrd(firestore, mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            if (task2.getResult().exists()) {
                                Boolean userAuthCheck = (Boolean) task2.getResult().get("jdg.ath.is");
                                Boolean locCheck = (Boolean) task2.getResult().get("jdg.loc.is");
                                Timber.e("campaign-인증여부:" + userAuthCheck);
                                CampaignMainViewModel model = ViewModelProviders.of(getActivity()).get(CampaignMainViewModel.class);
                                if (userAuthCheck) {//인증 한유저
                                    Timber.e("campaign:auth 인증");
                                    if (locCheck) {
                                        Timber.e("campaign:지역 인증");
                                        model.getCampaigns(HomeCampaignType.AUTHLOC).observe(getActivity(), campExes -> {
                                            //checkEmptyView(campExes);
                                            campExeList = campExes;
                                            filterCheck();
                                        });
                                    } else {
                                        Timber.e("campaign:지역 비인증");
                                        model.getCampaigns(HomeCampaignType.AUTHNOTLOC).observe(getActivity(), campExes -> {
                                            //checkEmptyView(campExes);
                                            campExeList = campExes;
                                            filterCheck();
                                        });
                                    }

                                } else {//인증 안한 유저
                                    Timber.e("campaign:auth 비인증");
                                    if (locCheck) {
                                        Timber.e("campaign:지역 인증");
                                        model.getCampaigns(HomeCampaignType.NOTAUTHLOC).observe(getActivity(), campExes -> {
                                            //checkEmptyView(campExes);
                                            campExeList = campExes;
                                            filterCheck();
                                        });
                                    } else {
                                        Timber.e("campaign:지역 비인증");
                                        model.getCampaigns(HomeCampaignType.NOTAUTHNOTLOC).observe(getActivity(), campExes -> {
                                            //checkEmptyView(campExes);
                                            campExeList = campExes;
                                            filterCheck();
                                        });
                                    }

                                }
                            }
                        }
                    });


        } else {//비로그인
            Timber.e("campaignMainFragment:비로그인:");
            noUserGetData();
        }

    }

    private void checkEmptyView(List<CampExe> campExes) {
        if (campExes.isEmpty() || campExes.size() == 0) {//데이터 없을때
            emptyViewCl.setVisibility(View.VISIBLE);
            Timber.e("campaignMainFragment 데이터 있다");
        } else {
            emptyViewCl.setVisibility(View.GONE);
            Timber.e("campaignMainFragment 데이터 없다");
        }
    }

    private void noUserGetData() {

        String align = (String) SharedPreferencesHelper.get(getContext(), "align", "");

        if (SharedPreferencesHelper.contains(getContext(), "kind")) {//필터 설정 o
            //mFilterBtn.setTextColor(getResources().getColor(R.color.colorPlumBoard));
            String category = (String) SharedPreferencesHelper.get(getContext(), "kind", "all");
            int minValue = (int) SharedPreferencesHelper.get(getContext(), "minValue", 0);
            int maxValue = (int) SharedPreferencesHelper.get(getContext(), "maxValue", 10);
            Timber.e("Filter : " + category + "align :" + align);
            switch (category) {
                case "all":
                    if (maxValue == 500) {
                        query = FirestoreQuerys.INSTANCE.notUserFilterMaxGetCampaign(firestore, minValue);
                    } else {
                        query = FirestoreQuerys.INSTANCE.notUserFilterMinMaxGetCampaign(firestore, minValue, maxValue);
                    }
                    break;
                case "video":
                    if (maxValue == 500) {
                        query = FirestoreQuerys.INSTANCE.notUserFilterVideoMaxGetCampaign(firestore, minValue);
                    } else {
                        query = FirestoreQuerys.INSTANCE.notUserFilterVideoMinMaxGetCampaign(firestore, minValue, maxValue);
                    }
                    break;
                case "survey":
                    if (maxValue == 500) {
                        query = FirestoreQuerys.INSTANCE.notUserFilterSurveyMaxGetCampaign(firestore, minValue);
                    } else {
                        query = FirestoreQuerys.INSTANCE.notUserFilterSurveyMinMaxGetCampaign(firestore, minValue, maxValue);
                    }

                    break;
            }

        } else { //필터 설정 x

            query = FirestoreQuerys.INSTANCE.userGetCampaignDataAll(firestore);

            if (SharedPreferencesHelper.contains(getContext(), "align")) {
                Timber.e("notuser+filter x ,align o");
                switch (align) {
                    case "popular":
                        query.orderBy("camp.pop.tot", Query.Direction.DESCENDING);
                        break;
                    case "new":
                        query.orderBy("inf.cA", Query.Direction.DESCENDING);
                        break;
                    case "rating":
                        query.orderBy("camp.rtg.avg", Query.Direction.DESCENDING);
                        break;
                    case "point":
                        query.orderBy("camp.bgt.jdg", Query.Direction.DESCENDING);
                        break;
                }
            } else {
                query.orderBy("camp.pop.tot", Query.Direction.DESCENDING);
            }

        }
        boolean check = (boolean) SharedPreferencesHelper.get(getContext(), "listChange", true);
        Timber.e("listChange = " + check);
        if (check) {
            adapter = new CampaignMainPagingAdapter(firestorePagingOptionsCustom(query), 1);
        } else {
            adapter = new CampaignMainPagingAdapter(firestorePagingOptionsCustom(query), 0);
            mainRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        }


        //adapter = new CampaignMainPagingAdapter(firestorePagingOptionsCustom(query),1);

        adapter.setOnclickListener((view, holder) -> {
            if (oneClick) {
                return;
            }
            oneClick = true;
            Intent intent = new Intent(getActivity(), CampaignDetailActivity.class);
            intent.putExtra("campexe", holder.campExe);
            intent.putExtra("isTut", false);
            Timber.e("campExeId = " + holder.campExe.getId());
            startActivity(intent);
        });

        initRecyclerView(adapter);
    }


    private FirestorePagingOptions<CampExe> firestorePagingOptionsCustom(Query query) {
        FirestorePagingOptions<CampExe> options = new FirestorePagingOptions.Builder<CampExe>()
                .setLifecycleOwner(this)
                .setQuery(query, config, new SnapshotParser<CampExe>() {
                    @NonNull
                    @Override
                    public CampExe parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        if (snapshot.exists()) {
                            campExe = snapshot.toObject(CampExe.class);
                            campExe.setId(snapshot.getId());
                        }
                        return campExe;
                    }
                }).build();
        return options;
    }


    private void initRecyclerView(RecyclerView.Adapter adapter) {
        mainRecyclerView.setAdapter(adapter);
        mFl.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);
    }

    //알람참고 https://stackoverflow.com/questions/17696486/actionbar-notification-count-icon-badge-like-google-has
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.campaign_main_toolbar, menu);
//        MenuItem menuItem = menu.findItem(R.id.campaignMainAlarm);
//        menuItem.setIcon(AlarmCounterHelper.buildCounterDrawable(getActivity(), 0, R.drawable.ic_noti_outline));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.fragment_campaign_main_filter_btn, R.id.fragment_campaign_main_align_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_campaign_main_filter_btn:
                Fragment f = getActivity().getSupportFragmentManager().findFragmentByTag(MAINFILTER);
                if (f == null) {
                    FilterFragment filterFragment = new FilterFragment();
                    filterFragment.show(getActivity().getSupportFragmentManager(), MAINFILTER);
                }

                break;
            case R.id.fragment_campaign_main_align_btn:
                Fragment f2 = getActivity().getSupportFragmentManager().findFragmentByTag(MAINALIGN);
                if (f2 == null) {
                    AlignFragment alignFragment = new AlignFragment();
                    alignFragment.show(getActivity().getSupportFragmentManager(), MAINALIGN);
                }
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isViewShown) {
                getCampaigns();
                isViewShown = false;
            }
//            Timber.e("campaignMainFragment setUserVisibleHint");
//            campExeList.clear();
//            campExeListDone.clear();
//            recyclerAdapter.notifyDataSetChanged();
//            getCampaigns();
        }
    }

    @Subscribe
    public void getEvent(Events.FilterEvent event) {

        String align = (String) SharedPreferencesHelper.get(getContext(), "align", "popular");

        if (!align.equals("popular")) {
            mAlignBtn.setTextColor(getResources().getColor(R.color.colorPlumBoard));
            mAlignBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_campaign_align_color, 0, 0, 0);
        } else {
            mAlignBtn.setTextColor(Color.BLACK);
            mAlignBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_campaign_align, 0, 0, 0);
        }


        Integer minValue = (int) SharedPreferencesHelper.get(getContext(), "minValue", 0);
        Integer maxValue = (int) SharedPreferencesHelper.get(getContext(), "maxValue", 500);
        String category = (String) SharedPreferencesHelper.get(getContext(), "kind", "all");

        if (!(minValue == 0 && maxValue == 500 && category.equals("all"))) {
            mFilterBtn.setTextColor(getResources().getColor(R.color.colorPlumBoard));
            mFilterBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_campaign_filter_color, 0, 0, 0);
        } else {
            mFilterBtn.setTextColor(Color.BLACK);
            mFilterBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_campaign_filter, 0, 0, 0);
        }

        if (mAuth.getCurrentUser() != null) {//유저
            recyclerAdapter.getData().clear();
            filterCheck();
        } else {//유저 x
            noUserGetData();
        }
    }


    private void filterCheck() {
        //SharedPreferencesHelper.get(getContext(), "align", "");
        String align = (String) SharedPreferencesHelper.get(getContext(), "align", "popular");

        if (SharedPreferencesHelper.contains(getActivity(), "kind")) {//필터 o
            String category = (String) SharedPreferencesHelper.get(getContext(), "kind", "all");
            int minValue = (int) SharedPreferencesHelper.get(getContext(), "minValue", 0);
            int maxValue = (int) SharedPreferencesHelper.get(getContext(), "maxValue", 500);
            Timber.e("Filter : " + category);

            if (SharedPreferencesHelper.contains(getActivity(), "align")) {//정렬 o
                campExeListDone = filter(align(align, campExeList), category, minValue, maxValue);
            } else {//정렬 x
                campExeListDone = filter(campExeList, category, minValue, maxValue);
            }
            recyclerAdapter.getData().clear();
            recyclerAdapter.addData(campExeListDone);
            recyclerAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
            recyclerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (oneClick) {
                        return;
                    }
                    oneClick = true;
                    Intent intent = new Intent(getActivity(), CampaignDetailActivity.class);
                    intent.putExtra("campexe", campExeListDone.get(position));
                    Timber.e("CampaignMainFragment DocId =" + campExeListDone.get(position).getId());
                    //intent.putExtra("campexe", campExeListDone.get(position));
                    startActivity(intent);

                }
            });
            initRecyclerView(recyclerAdapter);

        } else {//필터 x

            if (SharedPreferencesHelper.contains(getActivity(), "align")) { //정렬 o
                //recyclerAdapter.addData(align(align, campExeList));
                campExeListDone = align(align, campExeList);
            } else {//정렬 x
                //recyclerAdapter.addData(align("popular", campExeList));
                campExeListDone = align("popular", campExeList);
            }
            recyclerAdapter.getData().clear();
            recyclerAdapter.addData(campExeListDone);
            recyclerAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
            recyclerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (oneClick) {
                        return;
                    }
                    oneClick = true;
                    Intent intent = new Intent(getActivity(), CampaignDetailActivity.class);
                    intent.putExtra("campexe", campExeList.get(position));
                    Timber.e("CampaignMainFragment DocId =" + campExeList.get(position).getId());
                    startActivity(intent);
                }
            });
            initRecyclerView(recyclerAdapter);
        }
    }


    private List<CampExe> filter(List<CampExe> campExes, String category, int minValue, int maxValue) {
        List<CampExe> campExeListDone = new ArrayList<>();
        for (CampExe campExe : campExes) {//필터
            switch (category) {
                case "all":
                    if (maxValue == 500) {
                        if (minValue <= campExe.getCamp().getBgt().getJdg()) {
                            campExeListDone.add(campExe);
                        }
                    } else {
                        if (minValue <= campExe.getCamp().getBgt().getJdg() && maxValue >= campExe.getCamp().getBgt().getJdg()) {
                            campExeListDone.add(campExe);
                        }
                    }

                    break;
                case "video":
                    if (maxValue == 500) {
                        if (campExe.getCamp().getType() == 1 && minValue <= campExe.getCamp().getBgt().getJdg()) {
                            campExeListDone.add(campExe);
                        }
                    } else {
                        if (campExe.getCamp().getType() == 1 && minValue <= campExe.getCamp().getBgt().getJdg() && maxValue >= campExe.getCamp().getBgt().getJdg()) {
                            campExeListDone.add(campExe);
                        }
                    }
                    break;
                case "survey":
                    if (maxValue == 500) {
                        if (campExe.getCamp().getType() == 2 && minValue <= campExe.getCamp().getBgt().getJdg()) {
                            campExeListDone.add(campExe);
                        }
                    } else {
                        if (campExe.getCamp().getType() == 2 && minValue <= campExe.getCamp().getBgt().getJdg() && maxValue >= campExe.getCamp().getBgt().getJdg()) {
                            campExeListDone.add(campExe);
                        }
                    }

                    break;
            }

        }
        return campExeListDone;
    }

    private List<CampExe> align(String kind, List<CampExe> campExes) {
        switch (kind) {//정렬
            case "popular":
                Collections.sort(campExes, (campExeList, t1) -> t1.getCamp().getPop().getTot().compareTo(campExeList.getCamp().getPop().getTot()));//수동정렬 평점순
                break;
            case "new":
                Collections.sort(campExes, (campExeList, t1) -> t1.getInf().getCA().compareTo(campExeList.getInf().getCA()));//수동정렬 최신순
                break;
            case "rating":
                Collections.sort(campExes, (campExeList, t1) -> t1.getCamp().getRtg().getAvg().compareTo(campExeList.getCamp().getRtg().getAvg()));//수동정렬 평점순
                break;
            case "point":
                Collections.sort(campExes, (campExeList, t1) -> t1.getCamp().getBgt().getJdg().compareTo(campExeList.getCamp().getBgt().getJdg()));//수동정렬 포인트순
                break;
        }
        return campExes;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = PlumBoardApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
