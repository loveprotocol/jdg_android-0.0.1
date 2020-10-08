package com.imhc2.plumboard.mvvm.view.activity.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.imhc2.plumboard.commons.adapter.FaqAdapter;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.mvvm.model.domain.Faq;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FaqActivity extends AppCompatActivity {

    @BindView(R.id.activity_faq_toolbar) Toolbar mToolbar;
    FirebaseFirestore firestore;
    @BindView(R.id.activity_faq_Rv) RecyclerView mRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);
        firestore = FirebaseFirestore.getInstance();
        mRv.setLayoutManager(new LinearLayoutManager(this));
        getData();
        initToolbar();
    }

    private void getData() {
        FirestoreQuerys.INSTANCE.getFaq(firestore)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Faq> list = task.getResult().toObjects(Faq.class);
                            FaqAdapter faqAdapter = new FaqAdapter(R.layout.layout_faq_item, list);
                            faqAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                    Intent intent = new Intent(FaqActivity.this, FaqDetailActivity.class);
                                    intent.putExtra("faq", list.get(position));
                                    startActivity(intent);
                                }
                            });
                            mRv.setAdapter(faqAdapter);
                        } else {

                        }
                    }
                });


    }

    private void initToolbar() {
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPlumBoardSub));
        mToolbar.setTitle(R.string.FaqActivity_toolbarTitle);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_from_left , R.anim.slide_to_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
