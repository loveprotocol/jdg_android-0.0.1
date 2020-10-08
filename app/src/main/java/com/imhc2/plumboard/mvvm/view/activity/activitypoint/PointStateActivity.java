package com.imhc2.plumboard.mvvm.view.activity.activitypoint;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.adapter.PointStateAdapter;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.mvvm.model.domain.HistoryMoney;
import com.imhc2.plumboard.mvvm.model.domain.PointState;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class PointStateActivity extends AppCompatActivity {

    @BindView(R.id.activity_point_state_rv) RecyclerView mRv;
    @BindView(R.id.activity_point_state_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_point_state_empty_cl) ConstraintLayout mEmptyCl;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    List<PointState> list = new ArrayList<>();
    List<HistoryMoney> historyMonies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_state);
        ButterKnife.bind(this);
        firestore = FirebaseFirestore.getInstance();

        views();

        mRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRv.addItemDecoration(new DividerItemDecoration(PointStateActivity.this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {

            FirestoreQuerys.INSTANCE.getPointState(firestore, mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                historyMonies.clear();
                                list.clear();
                                Timber.e("empty? = "+task.getResult().isEmpty());
                                if(task.getResult().isEmpty()){
                                    mEmptyCl.setVisibility(View.VISIBLE);
                                    mRv.setVisibility(View.GONE);
                                }

                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    if (snapshot.exists()) {
                                        HistoryMoney historyMoney = snapshot.toObject(HistoryMoney.class);
                                        historyMoney.setDocId(snapshot.getId());
                                        historyMonies.add(historyMoney);
                                        PointState pointState = new PointState();
                                        pointState.setDate(convertDate(historyMoney.getInf().getCA(), "yyyy-MM-dd kk:mm"));
                                        pointState.setPoint(historyMoney.getWdl().getAmt().getTot());
                                        switch ((historyMoney.getSts()).intValue()) {
                                            case 1:
                                                pointState.setState(getString(R.string.PointStateActivity_accept));
                                                break;
                                            case 2:
                                                pointState.setState(getString(R.string.PointStateActivity_done));
                                                break;
                                            case 3:
                                                pointState.setState(getString(R.string.PointStateActivity_error));
                                                break;
                                            case 4:
                                                pointState.setState(getString(R.string.PointStateActivity_cancel));
                                                break;
                                        }
                                        pointState.setWhat(getString(R.string.pointStateActivity_withdraw));
                                        list.add(pointState);
                                    }
                                }
                                PointStateAdapter pointStateAdapter = new PointStateAdapter(R.layout.layout_point_state, list);
                                pointStateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                        Intent intent = new Intent(PointStateActivity.this, PointStateDetailActivity.class);
                                        intent.putExtra("historyMonies", historyMonies.get(position));
                                        startActivity(intent);
                                        Timber.e("historyMonies" + historyMonies.get(position).getDocId());
                                    }
                                });
                                mRv.setAdapter(pointStateAdapter);
                            } else {
                                Timber.e("접수및 처리현황 에러 = " + task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    public String convertDate(Long dateInMilliseconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.KOREA);
        String dateString = formatter.format(new Date(dateInMilliseconds));
        return dateString;
    }


    private void views() {
        mToolbar.setTitle(R.string.PointStateActivity_toolbarTitle);
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
    }
}
