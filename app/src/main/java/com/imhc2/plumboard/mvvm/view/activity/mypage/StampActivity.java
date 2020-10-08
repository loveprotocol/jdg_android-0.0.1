package com.imhc2.plumboard.mvvm.view.activity.mypage;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.mvvm.model.domain.Cnstv;
import com.imhc2.plumboard.mvvm.model.domain.CnstvUser;
import com.xw.repo.BubbleSeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StampActivity extends AppCompatActivity {


    @BindView(R.id.activity_stamp_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_stamp_stateBtn) Button stateBtn;
    @BindView(R.id.activity_stamp_state_tv) TextView stateTv;
    @BindView(R.id.activity_stamp_seekbar) BubbleSeekBar seekbar;
    @BindView(R.id.activity_stamp_sub_tv1) TextView subTv1;
    @BindView(R.id.activity_stamp_sub_tv2) TextView subTv2;
    @BindView(R.id.activity_stamp_sub_tv3) TextView subTv3;
    @BindView(R.id.activity_stamp_dayTv) TextView dayTv;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp);
        ButterKnife.bind(this);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        initToolbar();

        getEvnt();

        String text1 = getString(R.string.StampActivity_sub1);
        subTv1.setText(Html.fromHtml(text1));
        String text2 = getString(R.string.StampActivity_sub2);
        subTv2.setText(Html.fromHtml(text2));
        String text3 = getString(R.string.StampActivity_sub3);
        subTv3.setText(Html.fromHtml(text3));

    }

    private void getEvnt() {
        FirestoreQuerys.INSTANCE.getEventUserCnstv(firestore, mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CnstvUser cnstvUser = task.getResult().toObject(CnstvUser.class);

                        FirestoreQuerys.INSTANCE.getEventCnstv(firestore)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Cnstv cnstv = task1.getResult().toObject(Cnstv.class);
                                        getStampState(cnstvUser,cnstv);
                                    }
                                });
                    }
                });

    }

    private void initToolbar() {
        mToolbar.setTitle(R.string.StampActivity_toolbarTitle);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPlumBoardSub));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getStampState(CnstvUser cnstvUser,Cnstv cnstv) {
        seekbar.setEnabled(false);
        seekbar.setProgress(cnstvUser.getPrgs().size());
        Timber.e("seekbar size = " + cnstvUser.getPrgs().size());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);  //어제 구하기(오늘 날짜에서 하루를 뺌.)
        String date = sdf.format(calendar.getTime());

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE, 0);  //오늘
        String date2 = sdf.format(calendar2.getTime());

        Timber.e("cnstvUser.getPrgs() = " + cnstvUser.getPrgs());
        Timber.e("yesterday = " + date);
        Timber.e("today = " + date2);
        Timber.e("cnstvUser.getPrgs().size() = " + cnstvUser.getPrgs().size());
        Timber.e("cnstvUser.getPrgs().contains(date) = " + cnstvUser.getPrgs().contains(date));
        Timber.e("cnstvUser.getPrgs().contains(date2) = " + cnstvUser.getPrgs().contains(date2));


        if (cnstvUser.getPrgs().size() == 0 || cnstvUser.getPrgs().contains(date) || cnstvUser.getPrgs().contains(date2)) {
            seekbar.setBubbleColor(Color.parseColor("#30FCAD"));
            seekbar.setSecondTrackColor(Color.parseColor("#30FCAD"));
            seekbar.setThumbColor(Color.parseColor("#30FCAD"));


            switch (cnstvUser.getPrgs().size()) {
                case 1:
                    Timber.e("dayTv = 1 이다");
                    dayTv.setText("1일차 진행 중");
                    break;
                case 2:
                    Timber.e("dayTv = 2 이다");
                    dayTv.setText("2일차 진행 중");
                    break;
                case 3:
                    Timber.e("dayTv = 3 이다");
                    dayTv.setText("3일차 완료");
                    break;
                default:
                    Timber.e("else 이다");
                    break;
            }

        } else {
            if (cnstvUser.getPrgs().size() > 1 && !cnstvUser.getPrgs().contains(date) || !cnstvUser.getPrgs().contains(date2)) {
                dayTv.setText("다음 캠페인 완료 시 초기화 됩니다");
                seekbar.setBubbleColor(Color.parseColor("#9E9E9E"));
                seekbar.setSecondTrackColor(Color.parseColor("#9E9E9E"));
                seekbar.setThumbColor(Color.parseColor("#9E9E9E"));
            }
        }


        Long stamp = Long.parseLong(cnstvUser.getEvntCt().toString());
        Double done = Double.parseDouble(cnstv.getRatePerCt().toString()) * stamp;
        stateBtn.setText(Math.round(done * 100) + "%");

        Double sRate = Double.parseDouble(cnstv.getMRate().toString());

        stateTv.setText("(" + cnstvUser.getPrgs().size() + "/" + "3" + ")");
        if (done < sRate) {
            stateBtn.setBackgroundColor(getResources().getColor(R.color.colorPointStateError));
        } else if (done.equals(sRate)) {
            stateBtn.setBackgroundColor(getResources().getColor(R.color.colorPlumBoard));
        }

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
    }
}
