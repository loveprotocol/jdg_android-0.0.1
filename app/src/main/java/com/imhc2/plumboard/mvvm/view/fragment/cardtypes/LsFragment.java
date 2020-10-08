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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.util.CustomImageView169;
import com.imhc2.plumboard.mvvm.model.domain.Ls;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LsFragment extends Fragment {
    private static final String LS = "ls";
    Unbinder unbinder;

    private Ls ls;
    @BindView(R.id.fragment_type_last_title) TextView lsTitle;
    @BindView(R.id.fragment_type_last_content) TextView lsContent;
    @BindView(R.id.fragment_type_ls_img) CustomImageView169 lsImg;
    @BindView(R.id.fragment_type_ls_minus_btn) ImageView lsMinusBtn;
    @BindView(R.id.fragment_type_ls_plus_btn) ImageView lsPlusBtn;
    @BindView(R.id.fragment_type_ls_min_num_tv) TextView lsMinNumTv;
    @BindView(R.id.fragment_type_ls_max_num_tv) TextView lsMaxNumTv;
    @BindView(R.id.fragment_type_ls_min_text_tv) TextView lsMinTextTv;
    @BindView(R.id.fragment_type_ls_max_text_tv) TextView lsMaxTextTv;
    @BindView(R.id.fragment_type_ls_num_tv) TextView lsNumTv;
    @BindView(R.id.fragment_type_ls_seekBar) SeekBar lsSeekBar;
    View view;
    public LsFragment() {
        // Required empty public constructor
    }

    public static LsFragment newInstance(Ls ls) {
        LsFragment fragment = new LsFragment();
        Bundle args = new Bundle();
        args.putSerializable(LS, ls);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ls = (Ls) getArguments().getSerializable(LS);
            if (ls.getOrd()==0&&ls.getReq() != null && !ls.getReq()) {
                EventBus.get().post(new Events.CardResult(""));
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_type_ls, container, false);
        unbinder = ButterKnife.bind(this, view);

        lsTitle.setText(ls.getTtl());
        if (ls.getDescA() != null && ls.getDescA()) {
            lsContent.setText(ls.getDesc());
        }
        if (ls.getImgA() != null && ls.getImgA()) {
            Glide.with(getActivity())
                    .applyDefaultRequestOptions(new RequestOptions().optionalCenterCrop().placeholder(R.drawable.ic_campaign_empty))
                    .load(ls.getImg())
                    .into(lsImg);
        }else{
            lsImg.setVisibility(View.GONE);
        }
        if (ls.getSH() != null) {
            lsSeekBar.setMax(ls.getSH());
            lsMaxNumTv.setText(String.valueOf(ls.getSH()));
            lsSeekBar.setProgress(0);
            lsNumTv.setText(String.valueOf(0));
        }

        lsMinTextTv.setText(ls.getLL());
        lsMaxTextTv.setText(ls.getLH());
        lsMinNumTv.setText(String.valueOf(ls.getSL()));

        lsMinusBtn.setOnClickListener(v -> {
            lsSeekBar.setProgress(lsSeekBar.getProgress() - 1);
        });
        lsPlusBtn.setOnClickListener(v -> {
            lsSeekBar.setProgress(lsSeekBar.getProgress() + 1);
        });


        lsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("seekbar", "" + progress);
                //RxEventBus.getInstance().sendEvent(progress);
                EventBus.get().post(new Events.CardResult(progress));
                lsNumTv.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
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
            if (ls != null) {
                if (ls.getReq() != null && !ls.getReq()) {
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
