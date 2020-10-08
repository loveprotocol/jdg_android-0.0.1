package com.imhc2.plumboard.mvvm.view.fragment.cardtypes;


import android.app.Activity;
import android.graphics.Color;
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
import com.imhc2.plumboard.mvvm.model.domain.La;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;


public class LaFragment extends Fragment {

    private static final String LA = "la";
    Unbinder unbinder;

    private La la;
    @BindView(R.id.fragment_type_la_et) TextInputEditText laEt;
    @BindView(R.id.fragment_type_la_text_count) TextView laTvCount;
    @BindView(R.id.fragment_type_la_title) TextView laTitle;
    @BindView(R.id.fragment_type_la_content) TextView laContent;
    @BindView(R.id.fragment_type_la_img) CustomImageView169 laImg;
    View view;

    public LaFragment() {
        // Required empty public constructor
    }

    public static LaFragment newInstance(La la) {
        LaFragment fragment = new LaFragment();
        Bundle args = new Bundle();
        args.putSerializable(LA, la);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            la = (La) getArguments().getSerializable(LA);
            if (la.getOrd() == 0 && !la.getReq()) {
                EventBus.get().post(new Events.CardResult(""));
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_type_la, container, false);
        unbinder = ButterKnife.bind(this, view);

//        laEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Timber.e("edittext focus = "+hasFocus);
//                if(!hasFocus){
//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//            }
//        });

        laTitle.setText(la.getTtl());
        if (la.getDescA()) {
            laContent.setText(la.getDesc());
        }
        if (la.getImgA()) {
            Glide.with(getActivity())
                    .applyDefaultRequestOptions(new RequestOptions().optionalCenterCrop().placeholder(R.drawable.ic_campaign_empty))
                    .load(la.getImg())
                    .into(laImg);
        }else{
            laImg.setVisibility(View.GONE);
        }

        if (la.getTLA()) {//최대수
            laEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(la.getTLM())});
        }
        if (la.getTLM() != null) {
            laTvCount.setText(String.valueOf("0" + "/" + la.getTLM()));
        }

        laEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Timber.e("LaFragment afterTextChanged= editable  =" + s);
                if (la.getTLM() != null) {
                    laTvCount.setText(String.valueOf(s.length() + "/" + la.getTLM()));
                }

                if (!s.toString().isEmpty()) {
                    //RxEventBus.getInstance().sendEvent(laEt.getText().toString());
                    EventBus.get().post(new Events.CardResult(laEt.getText().toString()));
                } else {
                    List<String> list = new ArrayList<>();
                    list.clear();
                    //RxEventBus.getInstance().sendEvent(list);
                    EventBus.get().post(new Events.CardResult(list));
                }
            }
        });


        laEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }else{
                    laEt.setBackgroundColor(Color.parseColor("#16171F"));
                }
            }
        });

        return view;
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            try {
                hideKeyboard(view);
            } catch (Exception e) {

            }
            if (la != null) {
                if (!la.getReq()) {
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
