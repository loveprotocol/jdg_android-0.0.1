package com.imhc2.plumboard.mvvm.view.activity.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.adapter.NoticeActivityAdapter;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.mvvm.model.domain.Notice;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class NoticeActivity extends AppCompatActivity {

    @BindView(R.id.activity_notice_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_notice_rv) RecyclerView mRv;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ButterKnife.bind(this);
        firestore = FirebaseFirestore.getInstance();
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        initToolbar();
        getDate();
    }

    private void getDate() {
        FirestoreQuerys.INSTANCE.getNotice(firestore)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Timber.e("admPub = "+task.getResult());
                            List<Notice> notice = task.getResult().toObjects(Notice.class);
                            NoticeActivityAdapter noticeActivityAdapter = new NoticeActivityAdapter(R.layout.layout_notice_item, notice);
                            noticeActivityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                    Intent intent = new Intent(NoticeActivity.this,NoticeDetailActivity.class);
                                    intent.putExtra("notice",notice.get(position));
                                    startActivity(intent);
                                }
                            });
                            mRv.setAdapter(noticeActivityAdapter);
                        }

                    }
                });
    }

    private void initToolbar() {
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPlumBoardSub));
        mToolbar.setTitle(R.string.NoticeActivity_toolbarTitle);
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
