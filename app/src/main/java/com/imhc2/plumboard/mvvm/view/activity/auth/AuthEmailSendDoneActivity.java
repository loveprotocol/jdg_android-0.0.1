package com.imhc2.plumboard.mvvm.view.activity.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.imhc2.plumboard.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthEmailSendDoneActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    @BindView(R.id.emailResend) Button emailResend;
    private String email;
    Uri shortLink;
    boolean pw;
    @BindView(R.id.sendDoneToolbar) Toolbar sendDoneToolbar;
    @BindString(R.string.dynamic_link) String dynamicLink;
    String dynamicLinkUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActivityAuthEmailSendDoneBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_auth_email_send_done);
        setContentView(R.layout.activity_auth_email_send_done);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        initLayout();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        pw = intent.getBooleanExtra("findPw",false);
        resendEmail(true);

    }

    private void initLayout() {
        setSupportActionBar(sendDoneToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }


    public void showEmailApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        startActivity(Intent.createChooser(intent, getString(R.string.intent_emailApp)));
    }

    public void resendEmail(boolean check) {
        //자체 dynamicLink생성시 덧붙이는 데이터가 뒤짐

//        Uri uri =Uri.parse("https://plumboard.com/"+"android"+"?"+"email"+"="+"aaa@aaa.com");
//        Timber.e(uri);
//        FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(uri)
//                .setDynamicLinkDomain("plumboard.page.link")
//                .setAndroidParameters(
//                        new DynamicLink.AndroidParameters.Builder(getPackageName())
//                                .setMinimumVersion(125)
//                                .build())
//                .buildShortDynamicLink()
//                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
//                    @Override
//                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
//                        if (task.isSuccessful()) {
//                            shortLink = task.getResult().getShortLink();
//                            try {
//                                Toast.makeText(AuthEmailSendDoneActivity.this, "링크만듬", Toast.LENGTH_SHORT).show();
//                                Timber.e(shortLink.toString());
//                                ActionCodeSettings actionCodeSettings =
//                                        ActionCodeSettings.newBuilder()
//                                                .setUrl(shortLink.toString())
//                                                // This must be true
//                                                .setHandleCodeInApp(true)
//                                                .setIOSBundleId("com.imhc.plumboard")
//                                                .setAndroidPackageName("com.imhc.plumboard",false, null)
//                                                .build();
//
//                                mAuth.sendSignInLinkToEmail(email,actionCodeSettings).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if(task.isSuccessful()){
//                                            showEmailApp(null);
//                                        }
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.e("fail",e.getMessage());
//                                    }
//                                });
//
//                            } catch (ActivityNotFoundException ignored) {
//                            }
//                        } else {
//                            //Log.w(TAG, task.toString());
//                        }
//                    }
//                });

        //콘솔에 생성된 link

//        String url = null;
//        if (kind.equals("1")) {
//            url = dynamicLink + "?" + kind + email;
//        } else if (kind.equals("2")) {
//            url = dynamicLink + "?" + kind + email;
//        }

        if(pw){//findPw
            dynamicLinkUrl = dynamicLink + "?" + email + "?"+"findPw";
        }else{//signUp
            dynamicLinkUrl = dynamicLink + "?" + email +"?"+"signUp";
        }

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl(dynamicLinkUrl)
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.imhc.plumboard")
                        .setAndroidPackageName("com.imhc.plumboard", false, null)//mytodo:출시후 true로 바꾸기
                        .build();

        mAuth.sendSignInLinkToEmail(email, actionCodeSettings).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (check) {
                        showEmailApp();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fail", e.getMessage());
            }
        });


    }

    @OnClick({R.id.emailResend, R.id.emailSendDoneStartApp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.emailResend:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.dialog_title_resend)
                        .setCancelable(false)
                        .setPositiveButton(R.string.dialog_positive_check, (dialogInterface, i) -> {
                            showEmailApp();
                        }).create().show();

                resendEmail(false);

                break;

            case R.id.emailSendDoneStartApp:
                showEmailApp();
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
