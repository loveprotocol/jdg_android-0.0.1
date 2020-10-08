package com.imhc2.plumboard.mvvm.view.activity.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.functions.FunctionEvents;
import com.imhc2.plumboard.commons.util.TextInputLayoutAdapter;
import com.imhc2.plumboard.mvvm.view.fragment.bottomsheets.FindIdPwFragment;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class AuthEmailSignUpActivity extends AppCompatActivity implements Validator.ValidationListener {


    @BindView(R.id.signUpToolbar) Toolbar signUpToolbar;
    @BindView(R.id.signUpEmailEt) TextInputEditText signUpEmailEt;
    @BindView(R.id.activity_auth_email_sign_up_findIdPw) TextView findIdPw;

    @NotEmpty(trim = true, message = "비어있습니다")
    @Email(message = "이메일형식으로 입력해주세요.")
    @BindView(R.id.signUpEmailTIL) TextInputLayout signUpEmailTIL;

    Validator validator;

    @BindView(R.id.signUpTv) TextView signUpTv;
    @BindView(R.id.activity_auth_email_sign_up_resend_tv) TextView resendTv;
    Boolean pw = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActivityAuthEmailSignUpBinding binding= DataBindingUtil.setContentView(this, R.layout.activity_auth_email_sign_up);
        setContentView(R.layout.activity_auth_email_sign_up);
        ButterKnife.bind(this);
        setSupportActionBar(signUpToolbar);

        Intent intent = getIntent();
        pw = intent.getBooleanExtra("findPw", false);
        if (pw) {
            signUpTv.setText(R.string.AuthEmailSignUpActivity_inputId);
            resendTv.setVisibility(View.VISIBLE);
            findIdPw.setVisibility(View.GONE);
        }

        initLayout();
        initValidator();
    }

    private void initLayout() {
        setSupportActionBar(signUpToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        findIdPw.setPaintFlags(findIdPw.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        findIdPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindIdPwFragment.newInstance("계정 찾기").show(getSupportFragmentManager(),"findId");
            }
        });
    }

    private void initValidator() {
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

    public void startAuthEmailSendDone() {
        Intent intent = new Intent(AuthEmailSignUpActivity.this, AuthEmailSendDoneActivity.class);
        intent.putExtra("email", signUpEmailEt.getText().toString());
        startActivity(intent);
    }

    public void startAuthAlreadySignedUp() {
        if(pw){
            Intent intent = new Intent(AuthEmailSignUpActivity.this, AuthEmailSendDoneActivity.class);
            intent.putExtra("email", signUpEmailEt.getText().toString());
            intent.putExtra("findPw",true);
            startActivity(intent);
        }else{
            Intent intent = new Intent(AuthEmailSignUpActivity.this, AuthAlreadySignedUpActivity.class);
            intent.putExtra("email", signUpEmailEt.getText().toString());
            startActivity(intent);
        }
    }

    @OnClick(R.id.signUpFloatingBtn)
    public void onViewClicked() {
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        //signUpFloatingBtn();
        new FunctionEvents().emailCheckFun(signUpEmailEt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Map<Object, Object>>() {
            @Override
            public void onComplete(@NonNull Task<Map<Object, Object>> task) {
                if (task.isSuccessful()) {
                    Boolean isSuccess = (Boolean) task.getResult().get("success");
                    if(isSuccess){
                        Boolean idCheck = (Boolean) task.getResult().get("exists");

                        if (idCheck) {
                            //이미 회원
                            startAuthAlreadySignedUp();
                        } else {
                            //비회원
                            startAuthEmailSendDone();
                        }
                    }else{
                        if(!AuthEmailSignUpActivity.this.isFinishing()){
                            String message = (String) task.getResult().get("message");
                            AlertDialog.Builder builder = new AlertDialog.Builder(AuthEmailSignUpActivity.this);
                            builder.setMessage(message);
                            builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            Timber.e("emailCheckFun fail: " + task.getException().getMessage());
                        }
                    }

                } else {
                    if(!AuthEmailSignUpActivity.this.isFinishing()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(AuthEmailSignUpActivity.this);
                        builder.setMessage(getString(R.string.dialog_function_error));
                        builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                        Timber.e("emailCheckFun fail: " + task.getException().getMessage());
                    }
                }
            }
        });
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
