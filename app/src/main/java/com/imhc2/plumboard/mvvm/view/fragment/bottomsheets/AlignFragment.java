package com.imhc2.plumboard.mvvm.view.fragment.bottomsheets;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.util.SharedPreferencesHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AlignFragment extends BottomSheetDialogFragment {

    Unbinder unbinder;
    @BindView(R.id.layout_align_popular_btn) Button popularBtn;
    @BindView(R.id.layout_align_new_btn) Button newBtn;
    @BindView(R.id.layout_align_rating_btn) Button ratingBtn;
    @BindView(R.id.layout_align_point_btn) Button pointBtn;

    public AlignFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_align, container, false);
        unbinder = ButterKnife.bind(this, v);

        if (SharedPreferencesHelper.contains(getContext(), "align")) {
            String align = (String) SharedPreferencesHelper.get(getContext(), "align", "popular");
            switch (align) {
                case "popular":
                    popularBtn.setTextColor(getResources().getColor(R.color.colorPlumBoard));
                    popularBtn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.drawable_align_check,0);
                    break;
                case "new":
                    newBtn.setTextColor(getResources().getColor(R.color.colorPlumBoard));
                    newBtn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.drawable_align_check,0);
                    break;
                case "rating":
                    ratingBtn.setTextColor(getResources().getColor(R.color.colorPlumBoard));
                    ratingBtn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.drawable_align_check,0);
                    break;
                case "point":
                    pointBtn.setTextColor(getResources().getColor(R.color.colorPlumBoard));
                    pointBtn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.drawable_align_check,0);
                    break;
            }
        }else {
            popularBtn.setTextColor(getResources().getColor(R.color.colorPlumBoard));
            popularBtn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.drawable_align_check,0);
            SharedPreferencesHelper.put(getContext(), "align", "popular");
        }

        return v;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.layout_align_popular_btn, R.id.layout_align_new_btn, R.id.layout_align_rating_btn, R.id.layout_align_point_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_align_popular_btn:
                SharedPreferencesHelper.put(getContext(), "align", "popular");
                break;
            case R.id.layout_align_new_btn:
                SharedPreferencesHelper.put(getContext(), "align", "new");
                break;
            case R.id.layout_align_rating_btn:
                SharedPreferencesHelper.put(getContext(), "align", "rating");
                break;
            case R.id.layout_align_point_btn:
                SharedPreferencesHelper.put(getContext(), "align", "point");
                break;
        }
        EventBus.get().post(new Events.FilterEvent());
        dismiss();
    }
}
