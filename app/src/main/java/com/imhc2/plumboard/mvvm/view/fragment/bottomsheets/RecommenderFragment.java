package com.imhc2.plumboard.mvvm.view.fragment.bottomsheets;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.functions.FunctionEvents;
import com.imhc2.plumboard.mvvm.view.activity.mypage.RecommenderActivity;
import com.imhc2.plumboard.mvvm.view.fragment.dialog.ProgressDialogHelper;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class RecommenderFragment extends DialogFragment {


    @BindView(R.id.fragment_recommender_et) EditText mEt;
    @BindView(R.id.fragment_recommender_btn) Button mBtn;
    @BindView(R.id.fragment_recommender_tll) TextInputLayout mTll;
    Unbinder unbinder;
    FirebaseAuth mAuth;
    ProgressDialogHelper helper;
    public RecommenderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recommender, container, false);
        unbinder = ButterKnife.bind(this, view);
        mAuth = FirebaseAuth.getInstance();
        helper = new ProgressDialogHelper(getContext());
        mEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEt.getText().toString().equals("")) {
                    mBtn.setBackgroundColor(Color.parseColor("#9E9E9E"));
                } else {
                    mBtn.setBackgroundColor(getResources().getColor(R.color.colorPlumBoard));
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
    }

    @Override
    public void onStop() {
        super.onStop();
        helper.dismiss();
    }

    @OnClick(R.id.fragment_recommender_btn)
    public void onViewClicked() {
        if (!mEt.getText().toString().equals("")) {
            mTll.setError("");
            helper.show();
            new FunctionEvents()
                    .recommendUser(mEt.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Map<Object, Object>>() {
                        @Override
                        public void onComplete(@NonNull Task<Map<Object, Object>> task) {
                            helper.dismiss();
                            if (task.isSuccessful()) {
                                Boolean isSuccess = (Boolean) task.getResult().get("success");
                                if (isSuccess) {
                                    startActivity(new Intent(getActivity(),RecommenderActivity.class));
                                    Toasty.normal(getActivity(), getString(R.string.toast_RecommenderFragment_inputDone), Toast.LENGTH_SHORT).show();
                                    dismiss();
                                } else {
                                    String message = (String) task.getResult().get("message");
                                    if(mTll !=null){
                                        mTll.setError(message);
                                    }

                                }
                            } else {
                                Toasty.normal(getActivity(), getString(R.string.dialog_function_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            mTll.setError("코드를 입력해 주세요");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
