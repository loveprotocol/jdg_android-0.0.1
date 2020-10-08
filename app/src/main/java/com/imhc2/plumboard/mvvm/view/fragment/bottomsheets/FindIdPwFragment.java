package com.imhc2.plumboard.mvvm.view.fragment.bottomsheets;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imhc2.plumboard.R;
import com.imhc2.plumboard.mvvm.view.activity.auth.AuthEmailSignUpActivity;
import com.imhc2.plumboard.mvvm.view.activity.auth.AuthFindIdActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;


public class FindIdPwFragment extends BottomSheetDialogFragment {

    Unbinder unbinder;
    private static final String TITLE = "title";
    String title;
    @BindView(R.id.fragment_find_id_pw_findIdTv) TextView findIdTv;
    Boolean onClickCheck=false;
    public FindIdPwFragment() {
        // Required empty public constructor
    }

    public static FindIdPwFragment newInstance(String title) {
        FindIdPwFragment fragment = new FindIdPwFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE, "아이디 찾기");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_id_pw, container, false);
        unbinder = ButterKnife.bind(this, view);
        findIdTv.setText(title);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();
        onClickCheck=false;
        Timber.e("FindIdPwFragment onPause");
    }

    @OnClick({R.id.fragment_find_id_pw_findIdTv, R.id.fragment_find_id_pw_findPwTv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_find_id_pw_findIdTv:
                startActivity(new Intent(getActivity(), AuthFindIdActivity.class));
                break;
            case R.id.fragment_find_id_pw_findPwTv:
                if(onClickCheck){
                    return;
                }else {
                    Intent intent = new Intent(getActivity(), AuthEmailSignUpActivity.class);
                    intent.putExtra("findPw", true);
                    startActivity(intent);
                    onClickCheck = true;
                }
                break;
        }
    }
}
