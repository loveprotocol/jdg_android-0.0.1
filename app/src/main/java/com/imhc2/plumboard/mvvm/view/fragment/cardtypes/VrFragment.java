package com.imhc2.plumboard.mvvm.view.fragment.cardtypes;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.willy.ratingbar.BaseRatingBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class VrFragment extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.fragment_type_vr_down_btn) ImageView vrDownBtn;
    @BindView(R.id.fragment_type_vr_count) TextView vrCount;
    @BindView(R.id.fragment_type_vr_up_btn) ImageView vrUpBtn;
    @BindView(R.id.fragment_type_vr_rating_bar) BaseRatingBar vrRatingBar;
    View view;
    public VrFragment() {
        // Required empty public constructor
    }

    public static VrFragment newInstance() {
        VrFragment fragment = new VrFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_type_video_rating, container, false);
        unbinder = ButterKnife.bind(this, view);

        vrDownBtn.setOnClickListener(v -> {
            vrRatingBar.setRating(vrRatingBar.getRating() - 0.1f);
        });

        vrUpBtn.setOnClickListener(v -> {
            vrRatingBar.setRating(vrRatingBar.getRating() + 0.1f);
        });

        vrRatingBar.setOnRatingChangeListener((baseRatingBar, v) -> {
            vrCount.setText(String.valueOf(Math.round(v *100)/100.0));
            //RxEventBus.getInstance().sendEvent(vrCount.getText().toString());
            EventBus.get().post(new Events.CardResult(vrCount.getText().toString()));
        });

        return view;
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
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
