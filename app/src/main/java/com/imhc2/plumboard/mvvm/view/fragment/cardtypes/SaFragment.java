package com.imhc2.plumboard.mvvm.view.fragment.cardtypes;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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
import com.imhc2.plumboard.mvvm.model.domain.Sa;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class SaFragment extends Fragment {

    private static final String SA = "sa";
    Unbinder unbinder;
    @BindView(R.id.fragment_type_sa_et) TextInputEditText saEt;
    @BindView(R.id.fragment_type_sa_title) TextView saTitle;
    @BindView(R.id.fragment_type_sa_content) TextView saContent;
    @BindView(R.id.fragment_type_sa_img) CustomImageView169 saImg;
    @BindView(R.id.fragment_type_sa_text_count) TextView saTextCount;

    private Sa sa;
    View view;

    public SaFragment() {
        // Required empty public constructor
    }

    public static SaFragment newInstance(Sa sa) {
        SaFragment fragment = new SaFragment();
        Bundle args = new Bundle();
        args.putSerializable(SA, sa);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sa = (Sa) getArguments().getSerializable(SA);
            if (sa.getOrd()==0&&!sa.getReq()) {//필수여부
                EventBus.get().post(new Events.CardResult(""));
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_type_sa, container, false);
        unbinder = ButterKnife.bind(this, view);
//        if (sa.getReq()) {//필수여부
//            RxEventBus.getInstance().sendEvent("");
//        }

        saTitle.setText(sa.getTtl());
        if (sa.getDescA()) {
            saContent.setText(sa.getDesc());
        }
        if(sa.getImgA()){
            Glide.with(getActivity())
                    .applyDefaultRequestOptions(new RequestOptions().optionalCenterCrop().placeholder(R.drawable.ic_campaign_empty))
                    .load(sa.getImg())
                    .into(saImg);
        }else{
            saImg.setVisibility(View.GONE);
        }

        if(sa.getTLM() !=null){
            saTextCount.setText(String.valueOf(0 + "/" + sa.getTLM()));
        }

        if (sa.getTLA()) {//최대수
            saEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(sa.getTLM())});
        }

        saEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Timber.e("SaFragment afterTextChanged= editable  ="+s);
                if(sa.getTLM() !=null){
                    saTextCount.setText(String.valueOf(s.length() + "/" + sa.getTLM()));
                }
                if (!s.toString().isEmpty()) {
                    EventBus.get().post(new Events.CardResult(saEt.getText().toString()));
                } else {
                    List<String> list = new ArrayList<>();
                    list.clear();
                    EventBus.get().post(new Events.CardResult(list));
                }
            }
        });

        saEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard(v);
                }
            }
        });
        return view;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible){
            try{
                hideKeyboard(view);
            }catch (Exception e){

            }
            if(sa !=null){
                if (!sa.getReq()) {//필수여부
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
