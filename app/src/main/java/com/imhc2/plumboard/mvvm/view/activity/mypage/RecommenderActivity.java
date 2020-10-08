package com.imhc2.plumboard.mvvm.view.activity.mypage;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.mvvm.model.domain.Rcmd;
import com.imhc2.plumboard.mvvm.model.domain.RcmdUser;
import com.imhc2.plumboard.mvvm.view.fragment.bottomsheets.RecommenderFragment;
import com.imhc2.plumboard.mvvm.view.fragment.bottomsheets.RecommenderShareFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class RecommenderActivity extends AppCompatActivity {

    @BindView(R.id.activity_recommender_toolbar) Toolbar mToolbar;
    FirebaseAuth mAuth;
    //Evnt evnt;
    @BindView(R.id.activity_recommender_stateBtn) Button stateBtn;
    @BindView(R.id.activity_recommender_stamp_Btn) Button stampBtn;
    @BindView(R.id.activity_recommender_state_tv) TextView stateTv;
    @BindView(R.id.activity_recommender_stamp_state_tv) TextView stampStateTv;
    @BindView(R.id.activity_recommender_sub_tv1) TextView subTv1;
    @BindView(R.id.activity_recommender_sub_tv2) TextView subTv2;
    Rcmd rcmd;
    RcmdUser rcmdUser;
    FirebaseFirestore firestore;
    private final static String RECOMMENDER = "recommender";
    private final static String RECOMMENDERSHARE = "recommenderShare";
    Double nowCode, maxCode;
    @BindView(R.id.activity_recommender_cl) ConstraintLayout recommenderCl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommender);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        initToolbar();

        String text1 = getString(R.string.RecommenderActivity_sub1);
        subTv1.setText(Html.fromHtml(text1));

        String text2 = getString(R.string.RecommenderActivity_sub2);
        subTv2.setText(Html.fromHtml(text2));

        //getEvnt();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getEvnt();
        Timber.e("onResume");
    }

    private void getEvnt() {
        FirestoreQuerys.INSTANCE.getEventRcmd(firestore)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        rcmd = task.getResult().toObject(Rcmd.class);

                        FirestoreQuerys.INSTANCE.getEventUserRcmd(firestore, mAuth.getCurrentUser().getUid())
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        rcmdUser = task1.getResult().toObject(RcmdUser.class);
                                        getState();
                                    }
                                });
                    }
                });
    }


    private void getState() {
        nowCode = rcmdUser.getEvntCt().getEnt() * rcmd.getRatePerCt().getEnt();
        maxCode = rcmd.getRatePerCt().getEnt() * rcmd.getMEvntCt().getMEnt();

        Timber.e(" rcmdUser.getEvntCt().getEnt() = " + rcmdUser.getEvntCt().getEnt() + " rcmd.getRatePerCt().getEnt() = " + rcmd.getRatePerCt().getEnt());
        Timber.e(" rcmd.getRatePerCt().getEnt() = " + rcmd.getRatePerCt().getEnt() + " rcmd.getMEvntCt().getMEnt() = " + rcmd.getMEvntCt().getMEnt());

        Double nowReco = rcmdUser.getEvntCt().getEntBy() * rcmd.getRatePerCt().getEntBy();
        Double maxReco = rcmd.getRatePerCt().getEntBy() * rcmd.getMEvntCt().getMEntBy();

        Timber.e("rcmdUser.getEvntCt().getEntBy() = " + rcmdUser.getEvntCt().getEntBy() + " rcmd.getRatePerCt().getEntBy() = " + rcmd.getRatePerCt().getEntBy());
        Timber.e("rcmd.getRatePerCt().getEntBy() = " + rcmd.getRatePerCt().getEntBy() + " rcmd.getMEvntCt().getMEntBy() = " + rcmd.getMEvntCt().getMEntBy());

        Timber.e("nowCode(추천인코드 현재) = " + nowCode + " maxCode(추천인코드 최대) = " + maxCode);
        Timber.e("nowReco(코드공유 현재) = " + nowReco + " maxReco(코드공유 최대) = " + maxReco);

        stateBtn.setText(Math.round(nowCode * 100) + "%");

        Double perNowReco = nowReco * 100;
        Float perNowRecoF = perNowReco.floatValue();

        if (perNowRecoF == Math.floor(perNowRecoF)) {
            stampBtn.setText(perNowRecoF.intValue() + "%");
        } else {
            stampBtn.setText(perNowRecoF + "%");
        }

//        if(nowReco==0.0d){
//            stampBtn.setText( "0%");
//        }else{
//            stampBtn.setText(nowReco*100 + "%");
//        }

        stateTv.setText("(" + rcmdUser.getEvntCt().getEnt() + "/" + rcmd.getMEvntCt().getMEnt() + ")");
        stampStateTv.setText("(" + rcmdUser.getEvntCt().getEntBy() + "/" + rcmd.getMEvntCt().getMEntBy() + ")");

        if (nowCode < maxCode) {
            stateBtn.setBackgroundColor(getResources().getColor(R.color.colorPointStateError));
        } else if (nowCode.equals(maxCode)) {
            stateBtn.setBackgroundColor(getResources().getColor(R.color.colorPlumBoard));
        }

        if (nowReco < maxReco) {
            stampBtn.setBackgroundColor(getResources().getColor(R.color.colorPointStateError));
        } else if (nowReco.equals(maxReco)) {
            stampBtn.setBackgroundColor(getResources().getColor(R.color.colorPlumBoard));
        }


        recommenderCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nowCode < maxCode) {
                    Fragment r1 = getSupportFragmentManager().findFragmentByTag(RECOMMENDER);
                    if (r1 == null) {
                        RecommenderFragment recommenderFragment = new RecommenderFragment();
                        recommenderFragment.show(getSupportFragmentManager(), RECOMMENDER);
                    }

                } else if (nowCode.equals(maxCode)) {
                    Toasty.normal(RecommenderActivity.this, "이미 추천인 코드 입력을 완료했습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void initToolbar() {
        mToolbar.setTitle(R.string.RecommenderActivity_toolbarTitle);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPlumBoardSub));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left , R.anim.slide_to_right);
    }

    @OnClick({ R.id.activity_recommender_stamp_cl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_recommender_stamp_cl:
                Fragment r2 = getSupportFragmentManager().findFragmentByTag(RECOMMENDERSHARE);
                if (r2 == null) {
                    RecommenderShareFragment recommenderShareFragment = new RecommenderShareFragment();
                    recommenderShareFragment.show(getSupportFragmentManager(), "recommenderShare");
                }
                break;
        }
    }
}
