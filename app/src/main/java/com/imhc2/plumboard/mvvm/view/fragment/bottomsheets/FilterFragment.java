package com.imhc2.plumboard.mvvm.view.fragment.bottomsheets;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.util.SharedPreferencesHelper;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends BottomSheetDialogFragment {


    Unbinder unbinder;
    @BindView(R.id.layout_filter_seekbar) RangeSeekBar filterSeekbar;
    @BindView(R.id.layout_filter_point_min_tv) TextView pointMinTv;
    @BindView(R.id.layout_filter_point_max_tv) TextView pointMaxTv;
    @BindView(R.id.layout_filter_radio_group) RadioGroup radioGroup;
    @BindView(R.id.layout_filter_btn) Button layoutFilterBtn;
    @BindView(R.id.layout_filter_rangeTv) TextView rangeTv;
    @BindView(R.id.layout_filter_refresh_img) ImageView refreshImg;

    FirebaseAuth mAuth;
    Integer minValue = 0, maxValue = 500;
    String category = "all";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filter, container, false);
        unbinder = ButterKnife.bind(this, v);
        mAuth = FirebaseAuth.getInstance();

        getPointRange();
        initRadioGroup();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    dismiss();
                    return true;
                } else return false;
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog d = super.onCreateDialog(savedInstanceState);
        // view hierarchy is inflated after dialog is shown
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                FrameLayout bottomSheet = d.findViewById(android.support.design.R.id.design_bottom_sheet);
                // Right here!
                final BottomSheetBehavior behaviour = BottomSheetBehavior.from(bottomSheet);
                behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        });
        return d;
    }

    private void getPointRange() {
        filterSeekbar.setIndicatorTextDecimalFormat("0");//없으면 에러난다
        if (SharedPreferencesHelper.contains(getContext(), "kind")) {
            Timber.e("filter 적용");
            category = (String) SharedPreferencesHelper.get(getContext(), "kind", "all");
            minValue = (int) SharedPreferencesHelper.get(getContext(), "minValue", 0);
            maxValue = (int) SharedPreferencesHelper.get(getContext(), "maxValue", 500);
            switch (category) {
                case "all":
                    radioGroup.check(R.id.layout_filter_radio_all);
                    break;
                case "video":
                    radioGroup.check(R.id.layout_filter_radio_video);
                    break;
                case "survey":
                    radioGroup.check(R.id.layout_filter_radio_survey);
                    break;
            }
            filterSeekbar.setValue(minValue, maxValue);
            pointMinTv.setText(String.valueOf(minValue));
            if (maxValue == 500) {
                pointMaxTv.setText(String.valueOf(maxValue + "+"));
            } else {
                pointMaxTv.setText(String.valueOf(maxValue));
            }
            layoutFilterBtn.setEnabled(true);
            if (minValue == 0 && maxValue == 500) {
                refreshImg.setImageResource(R.drawable.ic_filter_refresh);
                if (mAuth.getCurrentUser() == null) {
                    rangeTv.setVisibility(View.INVISIBLE);
                }
            } else {
                refreshImg.setImageResource(R.drawable.ic_filter_refresh_color);
                if (mAuth.getCurrentUser() == null) {
                    rangeTv.setVisibility(View.VISIBLE);
                }
            }
        } else {
            layoutFilterBtn.setEnabled(true);
            filterSeekbar.setValue(0, 500);
            pointMinTv.setText(String.valueOf(0));
            pointMaxTv.setText(String.valueOf(Math.round(filterSeekbar.getMaxProgress()) + "+"));
        }

        filterSeekbar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                pointMinTv.setText(String.valueOf(Math.round(leftValue)));

                if (Math.round(rightValue) == 500) {
                    pointMaxTv.setText(String.valueOf(Math.round(rightValue) + "+"));
                } else {
                    pointMaxTv.setText(String.valueOf(Math.round(rightValue)));
                }
                minValue = Math.round(leftValue);
                maxValue = Math.round(rightValue);

                if (!(minValue == 0 && maxValue == 500)) {
                    refreshImg.setImageResource(R.drawable.ic_filter_refresh_color);
                    if (mAuth.getCurrentUser() == null) {
                        rangeTv.setVisibility(View.VISIBLE);
                    }
                } else {
                    refreshImg.setImageResource(R.drawable.ic_filter_refresh);
                    if (mAuth.getCurrentUser() == null) {
                        rangeTv.setVisibility(View.INVISIBLE);
                    }
                }


            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

    }

    private void initRadioGroup() {
        //radioGroup.check(R.id.layout_filter_radio_all);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.layout_filter_radio_all:
                        category = "all";
                        break;
                    case R.id.layout_filter_radio_video:
                        category = "video";
                        break;
                    case R.id.layout_filter_radio_survey:
                        category = "survey";
                        break;
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.layout_filter_btn, R.id.layout_filter_refresh_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_filter_btn:
                SharedPreferencesHelper.put(getContext(), "maxValue", maxValue);
                SharedPreferencesHelper.put(getContext(), "minValue", minValue);
                SharedPreferencesHelper.put(getContext(), "kind", category);
                EventBus.get().post(new Events.FilterEvent());
                dismiss();
                break;
            case R.id.layout_filter_refresh_img:
                filterSeekbar.setValue(0, 500);
                pointMinTv.setText(String.valueOf(Math.round(filterSeekbar.getMinProgress())));
                pointMaxTv.setText(String.valueOf(Math.round(filterSeekbar.getMaxProgress()) + "+"));
                minValue = 0;
                maxValue = 500;
                refreshImg.setImageResource(R.drawable.ic_filter_refresh);
                break;
        }

    }


}
