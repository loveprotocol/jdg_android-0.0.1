package com.imhc2.plumboard.mvvm.view.fragment.cardtypes;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.util.SharedPreferencesHelper;
import com.willy.ratingbar.BaseRatingBar;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class RatingFragment extends Fragment {
    @BindView(R.id.fragment_type_rating_title) TextView ratingTitle;
    @BindView(R.id.fragment_type_rating_content) TextView ratingContent;
    @BindView(R.id.fragment_type_rating_down_btn) ImageView ratingDownBtn;
    @BindView(R.id.fragment_type_rating_count) TextView ratingCount;
    @BindView(R.id.fragment_type_rating_up_btn) ImageView ratingUpBtn;
    @BindView(R.id.fragment_type_rating_RatingBar) BaseRatingBar ratingRatingBar;
    @BindView(R.id.fragment_type_rating_time_tv) TextView timeTv;
    @BindView(R.id.fragment_type_rating_point_tv) TextView pointTv;
    Unbinder unbinder;
    private static final String POINT = "point";
    private int point;
    View view;
    public RatingFragment() {
        // Required empty public constructor
    }

    public static RatingFragment newInstance(Integer point) {
        RatingFragment fragment = new RatingFragment();
        Bundle args = new Bundle();
        args.putInt(POINT, point);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            point = getArguments().getInt(POINT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_type_rating, container, false);
        unbinder = ButterKnife.bind(this, view);

        pointTv.setText(NumberFormat.getInstance().format(point));

        ratingDownBtn.setOnClickListener(view1 -> {
            ratingRatingBar.setRating(ratingRatingBar.getRating() - 0.1f);
        });
        ratingUpBtn.setOnClickListener(view1 -> {
            ratingRatingBar.setRating(ratingRatingBar.getRating() + 0.1f);
        });

        ratingRatingBar.setOnRatingChangeListener((baseRatingBar, v) -> {
            float rating = (float) (Math.round(v * 100) / 100.0);
            ratingCount.setText(String.valueOf(rating));
            //RxEventBus.getInstance().sendEvent(rating);
            EventBus.get().post(new Events.CardResult(rating));
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(getContext() !=null){
                Long startTime = (Long) SharedPreferencesHelper.get(getContext(), "startTime", 0L);
                Long left = System.currentTimeMillis() - startTime;
                timeTv.setText(onTick(left));
            }

            try{
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }catch (Exception e){
            }

        }
    }

    public String onTick(long millisUntilFinished) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date(millisUntilFinished);
        return dateFormat.format(date);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
