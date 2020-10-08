package com.imhc2.plumboard.mvvm.view.activity.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.functions.FunctionEvents;
import com.imhc2.plumboard.mvvm.view.activity.MainActivity;
import com.imhc2.plumboard.mvvm.view.activity.mypage.LegalsWebViewActivity;
import com.imhc2.plumboard.mvvm.view.fragment.bottomsheets.FindIdPwFragment;
import com.imhc2.plumboard.mvvm.view.fragment.dialog.ProgressDialogHelper;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class AuthMainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 10;
    @BindView(R.id.activity_auth_main_toolbar) Toolbar AuthMainToolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    GoogleSignInClient mGoogleSignInClient;
    @BindView(R.id.activity_auth_mainAuthTv) TextView authTv;
    @BindView(R.id.activity_auth_mainLegalTv) TextView legalTv;
    @BindView(R.id.activity_auth_mainInfoTv) TextView infoTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActivityAuthMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_auth_main);
        setContentView(R.layout.activity_auth_main);
        //binding.setAuthMain(this);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        initLayout();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    private void initLayout() {
        setSupportActionBar(AuthMainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        authTv.setPaintFlags(authTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        legalTv.setPaintFlags(legalTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        infoTv.setPaintFlags(infoTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void googleBtn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void startEmailLogin() {
        startActivity(new Intent(this, AuthEmailSignInActivity.class));
    }

    public void startEmailSignUp() {
        startActivity(new Intent(this, AuthEmailSignUpActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ProgressDialogHelper progress = new ProgressDialogHelper(AuthMainActivity.this);
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                progress.show();
                                Map<String, String> data = new HashMap<>();
                                data.put("uid", mAuth.getCurrentUser().getUid());
                                data.put("email", mAuth.getCurrentUser().getEmail());
                                FunctionEvents events = new FunctionEvents();
                                events.signUpUsr(data).addOnCompleteListener(task12 -> {
                                    progress.dismiss();
                                    if (task12.isSuccessful()) {
                                        Boolean isSuccess = (Boolean) task12.getResult().get("success");
                                        if (isSuccess) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Intent intent = new Intent(AuthMainActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            String message = (String) task12.getResult().get("message");
                                            if (!AuthMainActivity.this.isFinishing()) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(AuthMainActivity.this);
                                                builder.setMessage(message);
                                                builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                builder.create().show();
                                                Timber.e("signUpUsr fail: " + task.getException().getMessage());
                                            }
                                        }


                                    } else {
                                        if (!AuthMainActivity.this.isFinishing()) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(AuthMainActivity.this);
                                            builder.setMessage(getString(R.string.dialog_function_error));
                                            builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.create().show();
                                            Timber.e("signUpUsr fail: " + task.getException().getMessage());
                                        }
                                    }

                                });
                            } else {
                                Timber.e("getAdditionalUserInfo().isNewUser() " + "else");
                                Intent intent = new Intent(AuthMainActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }


                        } else {
                            Timber.e("firebaseAuthWithGoogle " + "fail = " + task.getException().getMessage());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("emailError", e.getMessage());
            }
        });

    }


    @OnClick({R.id.activity_auth_main_googleBtn, R.id.activity_auth_main_emailSignInBtn, R.id.activity_auth_main_emailSignUpBtn, R.id.activity_auth_mainAuthTv, R.id.activity_auth_mainLegalTv, R.id.activity_auth_mainInfoTv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_auth_main_googleBtn:
                googleBtn();
                break;
            case R.id.activity_auth_main_emailSignInBtn:
                startEmailLogin();
                break;
            case R.id.activity_auth_main_emailSignUpBtn:
                startEmailSignUp();
                break;
            case R.id.activity_auth_mainAuthTv:
//                FindIdPwFragment filterFragment = new FindIdPwFragment();
//                filterFragment.show(getSupportFragmentManager(), "bottom sheet");
                FindIdPwFragment.newInstance("계정 찾기").show(getSupportFragmentManager(),"findId");
                break;
            case R.id.activity_auth_mainLegalTv:
                Intent intent = new Intent(this, LegalsWebViewActivity.class);
                intent.putExtra("address", getString(R.string.termsService));
                startActivity(intent);
                break;
            case R.id.activity_auth_mainInfoTv:
                Intent intent2 = new Intent(this, LegalsWebViewActivity.class);
                intent2.putExtra("address", getString(R.string.privacyPolicy));
                startActivity(intent2);
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