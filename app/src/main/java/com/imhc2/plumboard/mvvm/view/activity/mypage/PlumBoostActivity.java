package com.imhc2.plumboard.mvvm.view.activity.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.mvvm.model.domain.Cnstv;
import com.imhc2.plumboard.mvvm.model.domain.CnstvUser;
import com.imhc2.plumboard.mvvm.model.domain.Rcmd;
import com.imhc2.plumboard.mvvm.model.domain.RcmdUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class PlumBoostActivity extends AppCompatActivity {


    @BindView(R.id.activity_plum_boost_toolbar) Toolbar mToolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    Double firest, second, rRate, sRate,done;
    @BindView(R.id.activity_plum_boost_recommenderBtn) Button recommenderBtn;
    @BindView(R.id.activity_plum_boost_stamp_Btn) Button stampBtn;
    //Evnt evnt;
    @BindView(R.id.activity_plum_boost_sub_tv1) TextView mTv1;
    Rcmd rcmd;
    RcmdUser rcmdUser;
    Cnstv cnstv;
    CnstvUser cnstvUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plum_boost);
        ButterKnife.bind(this);
        views();
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String next = getString(R.string.PlumBoostActivity_sub1);
        mTv1.setText(Html.fromHtml(next));

    }

    @Override
    protected void onResume() {
        super.onResume();
        getRcmds();
        getCnstv();
    }

    private void getRcmds(){
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
                                        getRecommenderState();
                                    }
                                });
                    }
                });
    }
    private void getCnstv(){
        FirestoreQuerys.INSTANCE.getEventUserCnstv(firestore, mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cnstvUser = task.getResult().toObject(CnstvUser.class);

                        FirestoreQuerys.INSTANCE.getEventCnstv(firestore)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        cnstv = task1.getResult().toObject(Cnstv.class);
                                        getStampState();
                                    }
                                });
                    }
                });
    }


    private void getRecommenderState() {
        firest = Double.parseDouble(rcmd.getRatePerCt().getEnt().toString());
        second = Double.parseDouble(rcmd.getRatePerCt().getEntBy().toString());
        rRate = Double.parseDouble(rcmd.getMRate().toString());

        firest *= Long.parseLong(rcmdUser.getEvntCt().getEnt().toString());
        second *= Long.parseLong(rcmdUser.getEvntCt().getEntBy().toString());
        Double total = firest + second;
        Timber.e("추천 코드 퍼센트 = "+total);

        //값이 소수일때와 정수일떄 %처리
        Double perTotal =total *100;
        Float perTotalF =perTotal.floatValue();

        if(perTotalF == Math.floor(perTotalF)){
            recommenderBtn.setText(perTotalF.intValue()+"%");
            Timber.e("perTotal == Math.floor(perTotal) true = "+perTotalF.intValue());
        }else{
            recommenderBtn.setText(perTotalF+"%");
            Timber.e("perTotal == Math.floor(perTotal) false = "+perTotalF);
        }

//        if(total ==0.0d){
//            recommenderBtn.setText("0%");
//        }else{
//            //recommenderBtn.setText(total * 100 + "%");
//            recommenderBtn.setText(String.format("%.1f",total*100)+"%");
//
//        }


        if (total < rRate) {
            recommenderBtn.setBackgroundColor(getResources().getColor(R.color.colorPointStateError));
        } else if (total.equals(rRate)) {
            recommenderBtn.setBackgroundColor(getResources().getColor(R.color.colorPlumBoard));
        }

    }

    private void getStampState() {

        Long stamp = Long.parseLong(cnstvUser.getEvntCt().toString());
        sRate = Double.parseDouble(cnstv.getMRate().toString());
        done = Double.parseDouble(cnstv.getRatePerCt().toString()) * stamp;
        Timber.e("현재 캠페인stamp = "+stamp);
        stampBtn.setText(Math.round(done * 100) + "%");
        if (done < sRate) {
            stampBtn.setBackgroundColor(getResources().getColor(R.color.colorPointStateError));
        } else if (done.equals(sRate)) {
            stampBtn.setBackgroundColor(getResources().getColor(R.color.colorPlumBoard));
        }

    }

    private void views() {
        mToolbar.setTitle(R.string.PlumBoostActivity_toolbarTitle);
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

    @OnClick({R.id.activity_plum_boost_recommend_cl, R.id.activity_plum_boost_stamp_cl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_plum_boost_recommend_cl:
                Intent intent = new Intent(PlumBoostActivity.this, RecommenderActivity.class);
                //intent.putExtra("evnt", evnt);
                startActivity(intent);
                break;
            case R.id.activity_plum_boost_stamp_cl:
                Intent intent2 = new Intent(PlumBoostActivity.this, StampActivity.class);
                //intent2.putExtra("evnt", evnt);
                startActivity(intent2);
                break;
        }
    }

}
