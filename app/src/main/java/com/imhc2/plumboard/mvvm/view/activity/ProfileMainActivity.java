package com.imhc2.plumboard.mvvm.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.adapter.ProfileMainAdapter;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.mvvm.model.domain.Cnstv;
import com.imhc2.plumboard.mvvm.model.domain.CnstvUser;
import com.imhc2.plumboard.mvvm.model.domain.ProfileList;
import com.imhc2.plumboard.mvvm.model.domain.Rcmd;
import com.imhc2.plumboard.mvvm.model.domain.RcmdUser;
import com.imhc2.plumboard.mvvm.view.activity.mypage.AlarmActivity;
import com.imhc2.plumboard.mvvm.view.activity.mypage.CertificationActivity;
import com.imhc2.plumboard.mvvm.view.activity.mypage.FaqActivity;
import com.imhc2.plumboard.mvvm.view.activity.mypage.GuideActivity;
import com.imhc2.plumboard.mvvm.view.activity.mypage.LegalNoticeActivity;
import com.imhc2.plumboard.mvvm.view.activity.mypage.MyProfileActivity;
import com.imhc2.plumboard.mvvm.view.activity.mypage.NoticeActivity;
import com.imhc2.plumboard.mvvm.view.activity.mypage.PlumBoostActivity;
import com.imhc2.plumboard.mvvm.view.activity.mypage.PreviewActivity;
import com.imhc2.plumboard.mvvm.view.activity.mypage.SettingActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileMainActivity extends AppCompatActivity {

    @BindView(R.id.activity_profile_main_recyclerview) RecyclerView mRecyclerview;
    @BindView(R.id.activity_profile_main_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_profile_main_my_img) ImageView myImgview;
    @BindView(R.id.activity_profile_main_my_email_tv) TextView myEmailTv;
    private List<ProfileList> mDataList = new ArrayList<>();

    private static final int[] IMG = {R.drawable.ic_profile_certification_no, R.drawable.ic_profile_certification_no, R.drawable.ic_profile_notice, R.drawable.ic_profile_faq, R.drawable.ic_profile_contact, R.drawable.ic_profile_preview, R.drawable.ic_profile_notification, R.drawable.ic_profile_guide, R.drawable.ic_profile_setting, R.drawable.ic_legal_notice};
    private static final Class<?>[] ACTIVITY = {CertificationActivity.class, PlumBoostActivity.class, NoticeActivity.class, FaqActivity.class, null, PreviewActivity.class, AlarmActivity.class, GuideActivity.class, SettingActivity.class, LegalNoticeActivity.class};
    private static final List<Integer> TITLES = Arrays.asList(R.string.CertificationActivity_toolbarTitle, R.string.PlumBoostActivity_toolbarTitle, R.string.NoticeActivity_toolbarTitle, R.string.FaqActivity_toolbarTitle, R.string.ProfileMainActivity_ask, R.string.PreviewActivity_toolbarTitle, R.string.AlarmActivity_toolbarTitle, R.string.GuideActivity_toolbarTitle, R.string.SettingActivity_toolbarTitle, R.string.LegalNoticeActivity_toolbarTitle);

    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    String myNickName, myImg;

    Double firest, second, rRate, stamp, sRate;
    boolean boostCheck = false;
    boolean stampCheck, codeCheck;
    CnstvUser cnstvUser;
    Cnstv cnstv;
    Rcmd rcmd;
    RcmdUser rcmdUser;

    private String basicImg ="https://firebasestorage.googleapis.com/v0/b/plumboard-aacb2.appspot.com/o/tImg%2Fdefault%2Fdefault.png?alt=media&token=f139835d-5f55-48e4-a3c7-9367a08073f4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_profile_main);
        ButterKnife.bind(this);
        firestore = FirebaseFirestore.getInstance();


        initToolbar();

    }


    private void getAuthState() {
        FirestoreQuerys.INSTANCE.getUserCrd(firestore, mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Boolean authCheck = (Boolean) task.getResult().get("jdg.ath.is");
                            Boolean locCheck = (Boolean) task.getResult().get("jdg.loc.is");
                            if (authCheck && locCheck) {
                                IMG[0] = R.drawable.ic_profile_certification_yes;
                            } else {
                                IMG[0] = R.drawable.ic_profile_certification_no;
                            }
//                            val authCheck = task.result?.get("jdg.ath.is") as Boolean
//                            val locCheck = task.result?.get("jdg.loc.is") as Boolean
                            initRecyclerview();
                        }
                    }
                });
    }

    private void getBoostState() {
        FirestoreQuerys.INSTANCE.getEventUserCnstv(firestore, mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cnstvUser = task.getResult().toObject(CnstvUser.class);
                        stamp = Double.parseDouble(cnstvUser.getEvntCt().toString());

                        FirestoreQuerys.INSTANCE.getEventCnstv(firestore)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        cnstv = task1.getResult().toObject(Cnstv.class);
                                        sRate = Double.parseDouble(cnstv.getMRate().toString());
                                        stamp *= Double.parseDouble(cnstv.getRatePerCt().toString());

                                        if (stamp < sRate) {
                                            stampCheck = false;
                                        } else if (stamp.equals(sRate)) {
                                            stampCheck = true;
                                        }


                                        FirestoreQuerys.INSTANCE.getEventRcmd(firestore)
                                                .get()
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        rcmd = task2.getResult().toObject(Rcmd.class);

                                                        firest = Double.parseDouble(rcmd.getRatePerCt().getEnt().toString());
                                                        second = Double.parseDouble(rcmd.getRatePerCt().getEntBy().toString());
                                                        rRate = Double.parseDouble(rcmd.getMRate().toString());

                                                        FirestoreQuerys.INSTANCE.getEventUserRcmd(firestore, mAuth.getCurrentUser().getUid())
                                                                .get()
                                                                .addOnCompleteListener(task3 -> {
                                                                    if (task3.isSuccessful()) {
                                                                        rcmdUser = task3.getResult().toObject(RcmdUser.class);
                                                                        firest *= Long.parseLong(rcmdUser.getEvntCt().getEnt().toString());
                                                                        second *= Long.parseLong(rcmdUser.getEvntCt().getEntBy().toString());
                                                                        Double total = firest + second;
                                                                        if (total < rRate) {
                                                                            codeCheck = false;
                                                                        } else if (total.equals(rRate)) {
                                                                            codeCheck = true;
                                                                        }

                                                                        if (stampCheck && codeCheck) {
                                                                            boostCheck = true;
                                                                            IMG[1] = R.drawable.ic_profile_certification_yes;
                                                                        } else {
                                                                            IMG[1] = R.drawable.ic_profile_certification_no;
                                                                        }
                                                                        getAuthState();
                                                                    }
                                                                });
                                                    }
                                                });


                                    }
                                });
                    }
                });



    }


    @Override
    protected void onResume() {
        super.onResume();
        profileImgCheck();
        getBoostState();

    }

    private void profileImgCheck() {
        FirestoreQuerys.INSTANCE.getUserUsr(firestore, mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                myNickName = (String) task.getResult().get("jdg.nm");
                                myImg = (String) task.getResult().get("jdg.tImg");
                            }

                            if (myNickName == null) {
                                myEmailTv.setText(mAuth.getCurrentUser().getEmail());
                            } else {
                                myEmailTv.setText(myNickName);
                            }

                            if (myImg == null) {
                                Glide.with(ProfileMainActivity.this).load(basicImg).thumbnail(0.1f).apply(new RequestOptions().override(720).circleCrop()).into(myImgview);
                            } else {
                                Glide.with(ProfileMainActivity.this).load(myImg).thumbnail(0.1f).apply(new RequestOptions().override(720).circleCrop()).into(myImgview);
                            }

                        }
                    }
                });

    }

    private void initToolbar() {
        mToolbar.setTitle("");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPlumBoardSub));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerview() {
        mDataList.clear();
        for (int i = 0; i < TITLES.size(); i++) {
            ProfileList profileList = new ProfileList();
            profileList.setTitle(getString(TITLES.get(i)));
//            if(IMG[i] == R.drawable.ic_plum_star_color){
//                profileList.setImageResource(R.drawable.ic_plum_logo_color);
//            }else{
//                profileList.setImageResource(IMG[i]);
//            }
            profileList.setImageResource(IMG[i]);
            profileList.setActivity(ACTIVITY[i]);
            mDataList.add(profileList);
        }

        ProfileMainAdapter adapter = new ProfileMainAdapter(R.layout.layout_profile_main_item, mDataList);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //gotoActivity(new Intent(ProfileMainActivity.this,ACTIVITY[position]));
                //mAuth.signOut();
                if (position == 4) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.ProfileMainActivity_emailAddress), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.ProfileMainActivity_emailTitle);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, R.string.ProfileMainActivity_emailContent);
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.intent_sendEmail)));
                } else {
                    Intent intent = new Intent(ProfileMainActivity.this, ACTIVITY[position]);
                    startActivity(intent);
                    //ActivityOptions.
                }
            }
        });
        mRecyclerview.setAdapter(adapter);
        //mRecyclerview.addItemDecoration(new DividerItemDecoration(this,));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        switch (item.getItemId()) {
            case R.id.profile_write:
                startActivity(new Intent(this, MyProfileActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
