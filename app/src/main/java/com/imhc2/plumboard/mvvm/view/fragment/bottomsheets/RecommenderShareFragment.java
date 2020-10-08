package com.imhc2.plumboard.mvvm.view.fragment.bottomsheets;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class RecommenderShareFragment extends BottomSheetDialogFragment {


    @BindView(R.id.fragment_recommender_share_code) TextView shareCode;
    Unbinder unbinder;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    String mCode = "";

    public RecommenderShareFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommender_share, container, false);
        unbinder = ButterKnife.bind(this, view);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirestoreQuerys.INSTANCE.getEventUserRcmd(firestore, mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            mCode = task.getResult().get("code").toString();
                            if (mCode != null) {
                                if(shareCode !=null){
                                    shareCode.setText(mCode);
                                }
                            }
                        }
                    }
                });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    @OnClick({R.id.fragment_recommender_share_copyBtn, R.id.fragment_recommender_share_shareBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_recommender_share_copyBtn:
                setClipboard(getActivity(), mCode);
                Toasty.normal(getContext(),"내 추천 코드가 복사되었습니다.",Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.fragment_recommender_share_shareBtn:
                try {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.putExtra(Intent.EXTRA_TEXT, mCode);
                    intent.setType("text/plain");
                    Intent chooser = Intent.createChooser(intent, getString(R.string.intent_shareDefault));
                    startActivity(chooser);
                } catch (Exception e) {

                }
                dismiss();
                break;
        }
    }
}
