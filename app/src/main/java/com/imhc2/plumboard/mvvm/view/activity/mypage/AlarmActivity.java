package com.imhc2.plumboard.mvvm.view.activity.mypage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.imhc2.plumboard.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmActivity extends AppCompatActivity {

    @BindView(R.id.activity_notification_toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.AlarmActivity_toolbarTitle);
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
