package com.imhc2.plumboard.mvvm.view.fragment.cardtypes;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.util.CustomImageView169;
import com.imhc2.plumboard.mvvm.model.domain.Dp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DpFragment extends Fragment {

    private static final String DP = "dp";
    @BindView(R.id.fragment_type_dp_title) TextView dpTitle;
    @BindView(R.id.fragment_type_dp_img) CustomImageView169 dpImg;
    @BindView(R.id.fragment_type_dp_text) TextView dpText;
    Unbinder unbinder;

    private Dp dp;
    View view;

    public DpFragment() {
        // Required empty public constructor
    }

    public static DpFragment newInstance(Dp dp) {
        DpFragment fragment = new DpFragment();
        Bundle args = new Bundle();
        args.putSerializable(DP, dp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dp = (Dp) getArguments().getSerializable(DP);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_type_dp, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (dp.getImgA() != null && dp.getImgA()) {
            Glide.with(getActivity())
                    .applyDefaultRequestOptions(new RequestOptions().optionalCenterCrop().placeholder(R.drawable.ic_campaign_empty))
                    .load(dp.getImg())
                    .into(dpImg);
        }else{
            dpImg.setVisibility(View.GONE);
        }
        dpTitle.setText(dp.getTtl());
        dpText.setText(dp.getTtl());
        //dpContent.setText(dp.get);
        dpText.setText(dp.getBd());


        Log.e("TypeDp:", "onCreateView");
        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            try{
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }catch (Exception e){

            }
            //RxEventBus.getInstance().sendEvent("");
            EventBus.get().post(new Events.CardResult(""));

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
