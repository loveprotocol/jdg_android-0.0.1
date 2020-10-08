package com.imhc2.plumboard.mvvm.view.activity.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.functions.FunctionEvents;
import com.imhc2.plumboard.commons.util.TextInputLayoutAdapter;
import com.imhc2.plumboard.mvvm.view.activity.MainActivity;
import com.imhc2.plumboard.mvvm.view.fragment.dialog.ProgressDialogHelper;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class AuthPwSignUpActivity extends AppCompatActivity implements Validator.ValidationListener {
    FirebaseAuth auth;

    String deepLink, email, kind;
    @BindView(R.id.pwSignUpToolbar) Toolbar pwSignUpToolbar;
    @BindView(R.id.pwSignUpPwEt) TextInputEditText pwSignUpPwEt;
    @BindView(R.id.pwSignUpPwCheckEt) TextInputEditText pwSignUpPwCheckEt;

    @NotEmpty(trim = true, message = "비어있습니다")
    @Password(min = 6, scheme = Password.Scheme.ALPHA_NUMERIC, message = "숫자&영문 조합 6자리 이상을 입력해주세요")
    @BindView(R.id.pwSignUpPwTIL) TextInputLayout pwSignUpPwTIL;

    @NotEmpty(trim = true, message = "비어있습니다")
    @ConfirmPassword(message = "비밀번호가 일치하지 않습니다")
    @BindView(R.id.pwSignUpPwCheckTIL) TextInputLayout pwSignUpPwCheckTIL;
    ProgressDialogHelper helper;
    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_pw_sign_up);
        //ActivityAuthPwSignUpBinding binding=DataBindingUtil.setContentView(this, R.layout.activity_auth_pw_sign_up);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        auth.signOut();
        helper = new ProgressDialogHelper(this);
        initValidate();
        initLayout();

        Intent intent = getIntent();
        deepLink = intent.getStringExtra("link");
        email = intent.getStringExtra("email");
        kind = intent.getStringExtra("kind");

    }

    @Override
    protected void onStop() {
        super.onStop();
        helper.dismiss();
    }

    private void initLayout() {
        setSupportActionBar(pwSignUpToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    private void initValidate() {
        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutAdapter());
        validator.setViewValidatedAction(view -> {
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError("");
                ((TextInputLayout) view).setErrorEnabled(false);
            }
        });
    }

    @OnClick(R.id.floatingActionButton)
    public void onViewClicked() {
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        helper.show();
        if (auth.isSignInWithEmailLink(deepLink)) {
            auth.signInWithEmailLink(email, deepLink)//링크로 로긴
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                AuthResult result = task.getResult();
                                FirebaseUser user = result.getUser();
                                user.updatePassword(pwSignUpPwCheckEt.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (kind.equals("findPw")) {
                                                        helper.dismiss();
                                                        Intent intent = new Intent(AuthPwSignUpActivity.this, MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        Toasty.normal(AuthPwSignUpActivity.this, getString(R.string.toast_AuthPwSignUpActivity_changePw), Toast.LENGTH_SHORT).show();
                                                    } else if (kind.equals("signUp")) {
                                                        FunctionEvents functionEvents = new FunctionEvents();

                                                        Map<String, String> data = new HashMap<>();
                                                        data.put("uid", auth.getCurrentUser().getUid());
                                                        data.put("email", email);

                                                        functionEvents.signUpUsr(data).addOnCompleteListener(task12 -> {
                                                            helper.dismiss();
                                                            if (task12.isSuccessful()) {
                                                                Boolean isSuccess = (Boolean) task12.getResult().get("success");
                                                                if (isSuccess) {
                                                                    Intent intent = new Intent(AuthPwSignUpActivity.this, MainActivity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                } else {
                                                                    String message = (String) task12.getResult().get("message");
                                                                    if (AuthPwSignUpActivity.this.isFinishing()) {
                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(AuthPwSignUpActivity.this);
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
                                                                if (AuthPwSignUpActivity.this.isFinishing()) {
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(AuthPwSignUpActivity.this);
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
                                                    }
                                                }else{
                                                    Timber.e("updatePassword fail");
                                                }
                                            }
                                        });

                            } else {
                                Timber.e("signInWithEmailLink = " + task.getException().toString());
                            }
                        }
                    });
        }

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        errors.get(0).getView().requestFocus();
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError(message);
                ((TextInputLayout) view).setErrorEnabled(true);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            initDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        initDialog();
    }

    private void initDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AuthPwSignUpActivity.this);
        builder.setMessage(R.string.dialog_title_progressCancel);
        builder.setPositiveButton(R.string.dialog_positive_check, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(AuthPwSignUpActivity.this, AuthMainActivity.class));
                auth.signOut();
                finish();
            }
        }).setNegativeButton(R.string.dialog_negative_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
