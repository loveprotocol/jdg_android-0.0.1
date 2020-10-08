package com.imhc2.plumboard.mvvm.view.fragment.cardtypes;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.adapter.McAdapter;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.util.CustomImageView169;
import com.imhc2.plumboard.mvvm.model.domain.Mc;
import com.imhc2.plumboard.mvvm.model.domain.McSubList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class McFragment extends Fragment {

    private static final String MC = "mc";
    @BindView(R.id.fragment_type_mc_title) TextView mcTitle;
    @BindView(R.id.fragment_type_mc_RecyclerView) RecyclerView mcRecyclerView;
    @BindView(R.id.fragment_type_mc_content) TextView typeMcContent;
    @BindView(R.id.fragment_type_mc_img) CustomImageView169 typeMcImg;
    @BindView(R.id.fragment_type_mc_min_max_tv) TextView typeMcMinMaxTv;
    Unbinder unbinder;

    private Mc mc;
    View view;
    public McFragment() {
        // Required empty public constructor
    }

    public static McFragment newInstance(Mc param2) {
        McFragment fragment = new McFragment();
        Bundle args = new Bundle();
        args.putSerializable(MC, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mc = (Mc) getArguments().getSerializable(MC);
            if (mc.getOrd()==0&&mc.getReq() != null && !mc.getReq()) {//필수여부
                EventBus.get().post(new Events.CardResult(""));
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_type_mc, container, false);
        unbinder = ButterKnife.bind(this, view);
        List<McSubList> shuffleMcSubList = new ArrayList<>();
        List<McSubList> mcSubList = new ArrayList<>();
        mcTitle.setText(mc.getTtl());

        if (mc.getMA()) {
            //typeMcMinMaxTv.setText("(최소" + mc.getML() + "개 이상,최대" + mc.getMH() + "개까지 선택 가능" + ")");
            typeMcMinMaxTv.setVisibility(View.VISIBLE);
            if(mc.getML() !=null && mc.getMH() ==null){
                typeMcMinMaxTv.setText("(최소 "+mc.getML()+"개 이상 선택)");
            }else if(mc.getMH() !=null && mc.getML() ==null){
                typeMcMinMaxTv.setText("(최대 "+mc.getMH()+"개 이하 선택)");
            }else if(mc.getMH() !=null && mc.getML() !=null){
                typeMcMinMaxTv.setText("(정확히 "+mc.getMH()+"개 선택)");
            }

        }

        if (mc.getDescA() != null && mc.getDescA()) {
            typeMcContent.setText(mc.getDesc());
        }
        if (mc.getImgA() != null && mc.getImgA()) {
            Glide.with(getActivity())
                    .applyDefaultRequestOptions(new RequestOptions().centerCrop().placeholder(R.drawable.ic_campaign_empty))
                    .load(mc.getImg())
                    .into(typeMcImg);
        }else{
            typeMcImg.setVisibility(View.GONE);
        }
        mcSubList.clear();
        if (mc.getRes() != null) {
            for (int i = 0; i < mc.getRes().size(); i++) {
                McSubList subList = new McSubList();
                subList.setItem(mc.getRes().get(i));
                mcSubList.add(subList);
            }
        }

        if (mc.getSfl() != null && mc.getSfl()) {//랜덤활성
            shuffleMcSubList.clear();
            shuffleMcSubList.addAll(mcSubList);
            Collections.shuffle(shuffleMcSubList);
        } else {//비활성
            shuffleMcSubList.clear();
            shuffleMcSubList.addAll(mcSubList);
        }
        //mcRecyclerView.setNestedScrollingEnabled(false);

        mcRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        McAdapter adapter = new McAdapter(R.layout.layout_mc_sub_list, shuffleMcSubList, mcSubList, mc);
        mcRecyclerView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Log.e(":", savedInstanceState.getString("state"));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            try{
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }catch (Exception e){

            }

            if(mcRecyclerView !=null){
                mcRecyclerView.setFocusable(false);
            }

            if (mc != null) {
                if (mc.getReq() != null && !mc.getReq()) {//필수여부
                    //RxEventBus.getInstance().sendEvent("");
                    EventBus.get().post(new Events.CardResult(""));
                }
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
