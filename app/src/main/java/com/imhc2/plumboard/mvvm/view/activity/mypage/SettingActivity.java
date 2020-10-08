package com.imhc2.plumboard.mvvm.view.activity.mypage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.commons.util.SharedPreferencesHelper;
import com.imhc2.plumboard.mvvm.view.activity.MainActivity;
import com.imhc2.plumboard.mvvm.view.activity.auth.AuthEmailSendDoneActivity;
import com.imhc2.plumboard.mvvm.view.fragment.dialog.AccountFreezeFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.activity_setting_push_switch) SwitchCompat pushSwitch;
    @BindView(R.id.activity_setting_event_push_switch) SwitchCompat eventPushSwitch;
    @BindView(R.id.activity_setting_versionNum) TextView versionTv;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @BindView(R.id.activity_setting_toolbar) Toolbar mToolbar;
    Boolean isEventPushState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initViews();

        events();

        try {
            String device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionTv.setText("v" + device_version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void events() {
        Boolean enablePush = (Boolean) SharedPreferencesHelper.get(this, "pushEnable", false);
        pushSwitch.setChecked(enablePush);
        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesHelper.put(SettingActivity.this, "pushEnable", true);
                    FirebaseMessaging.getInstance().subscribeToTopic("test");
                    Toasty.normal(SettingActivity.this, getString(R.string.toast_SettingActivity_pushOn), Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferencesHelper.put(SettingActivity.this, "pushEnable", false);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("test");
                    Toasty.normal(SettingActivity.this, getString(R.string.toast_SettingActivity_pushOff), Toast.LENGTH_SHORT).show();
                }
            }
        });


        FirestoreQuerys.INSTANCE.getUserUsr(firestore, auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            isEventPushState = (Boolean) task.getResult().get("jdg.mkt.agree");
                            Timber.e("isEventPushState =" + isEventPushState);
                            if (isEventPushState != null && isEventPushState) {
                                eventPushSwitch.setChecked(true);
                            } else {
                                eventPushSwitch.setChecked(false);
                            }
                            eventPushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                                        builder.setTitle("마케팅 정보 수신 동의 안내")
                                                .setMessage("전송자 : 플럼보드\n동의일시 : " + getTime() + "\n처리내용 : 수신동의 처리완료")
                                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).create().show();

                                        FirestoreQuerys.INSTANCE.getUserUsr(firestore,auth.getCurrentUser().getUid())
                                                .update("jdg.mkt.agree", true,
                                                        "jdg.mkt.at", System.currentTimeMillis());
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                                        builder.setTitle("마케팅 정보 수신 거부 안내")
                                                .setMessage("전송자 : 플럼보드\n동의일시 : " + getTime() + "\n처리내용 : 수신거부 처리완료")
                                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).create().show();

                                        FirestoreQuerys.INSTANCE.getUserUsr(firestore,auth.getCurrentUser().getUid())
                                                .update("jdg.mkt.agree", false,
                                                        "jdg.mkt.at", System.currentTimeMillis());
                                    }
                                }
                            });
                        }
                    }
                });


    }

    private String getTime() {
        Date date = new Date();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년 MM월 dd일 HH시", Locale.KOREA);
        sdf2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String leftDate = sdf2.format(date);
        return leftDate;
    }

    private void initViews() {
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPlumBoardSub));
        mToolbar.setTitle(R.string.SettingActivity_toolbarTitle);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick({R.id.activity_setting_pw, R.id.activity_setting_logout, R.id.activity_setting_id_freeze})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_setting_pw:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle(R.string.dialog_title_pwReSetting)
                        .setMessage(R.string.dialog_title_pwResend)
                        .setPositiveButton(R.string.dialog_positive_check, (dialogInterface, i) -> {
                            Intent intent = new Intent(this, AuthEmailSendDoneActivity.class);
                            intent.putExtra("findPw", true);
                            intent.putExtra("email", auth.getCurrentUser().getEmail());
                            startActivity(intent);
//                            auth.sendPasswordResetEmail(auth.getCurrentUser().getEmail()).addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//                                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
//                                    startActivity(Intent.createChooser(intent, getString(R.string.intent_emailApp)));
//                                }
//                            });

                        })
                        .setNegativeButton(R.string.dialog_negative_cancel, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .create()
                        .show();

                break;
            case R.id.activity_setting_logout:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage(R.string.dialog_title_logout)
                        .setPositiveButton(R.string.dialog_positive_check, (dialogInterface, i) -> {
                            FirestoreQuerys.INSTANCE
                                    .updateMyInfo(auth.getCurrentUser().getUid(), FirestoreQuerys.INSTANCE.getUserUsr(firestore, auth.getCurrentUser().getUid()))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            auth.signOut();
                                            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    });

                        })
                        .setNegativeButton(R.string.dialog_negative_cancel, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .create()
                        .show();


                break;
            case R.id.activity_setting_id_freeze:
                AccountFreezeFragment freezeFragment = new AccountFreezeFragment();
                freezeFragment.show(getSupportFragmentManager(), "freeze");

//                builder.setTitle("계정 비활성화")
//                        .setMessage("[계정 비활성화 안내] \n\n - 계정 비활성화 신청 시점부터 7일간 유예기간이 시작됩니다 \n - 유예기간 중 해당 계정으로 로그인 시 계정을 다시 활성화 할 수 있습니다 \n - 유예기간이 지나면 계정이 영구적으로 비활성화되며,해당 계정의 포인트 정보를 포함한 모든 파트너 및 심사위원 정보가 삭제되고 복구 불가능합니다 ")
//                        .setPositiveButton("비활성화", (dialogInterfac, i) -> {
//                            new FunctionEvents().disabledAccount(auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Object>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Object> task) {
//                                    if (task.isSuccessful()) {
//                                        boolean answer = (boolean) task.getResult();
//                                        if (answer) {
//                                            Timber.e("disabledAccount : 성공!");
//                                        } else {
//                                            Timber.e(task.getException().getMessage());
//                                        }
//                                    } else {
//                                        Timber.e(task.getException().getMessage());
//                                        Toast.makeText(SettingActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        })
//                        .setNegativeButton("취소", (dialogInterface, i) -> {
//                            dialogInterface.dismiss();
//                        })
//                        .create()
//                        .show();
                break;
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
