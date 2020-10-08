package com.imhc2.plumboard.mvvm.view.activity.auth;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.util.TextInputLayoutAdapter;
import com.imhc2.plumboard.mvvm.view.activity.MainActivity;
import com.imhc2.plumboard.mvvm.view.fragment.bottomsheets.FindIdPwFragment;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class AuthEmailSignInActivity extends AppCompatActivity implements Validator.ValidationListener {
    FirebaseAuth mAuth;
    @BindView(R.id.emailEt) TextInputEditText emailEt;
    @BindView(R.id.pwEt) TextInputEditText pwEt;
    @BindView(R.id.activity_auth_email_sign_in_toolbar) Toolbar signInToolbar;

    @NotEmpty(trim = true, message = "비어있습니다")
    @Email(message = "이메일 형식으로 입력해주세요")
    @BindView(R.id.emailTIL) TextInputLayout emailTIL;

    @NotEmpty(trim = true, message = "비어있습니다")
    @Password(scheme = Password.Scheme.ALPHA_NUMERIC, message = "비밀번호가 올바르지 않습니다")
    @BindView(R.id.pwTIL) TextInputLayout pwTIL;

    Validator validator;
    @BindView(R.id.activity_auth_email_sign_in_findIdPwTv) TextView findIdPwTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActivityAuthEmailSignInBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_auth_email_sign_in);
        setContentView(R.layout.activity_auth_email_sign_in);
        //binding.setAuthEmailSignIn(this);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        initLayout();
        initValidator();

        findIdPwTv.setPaintFlags(findIdPwTv.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        initEvents();
    }

    private void initEvents(){
        findIdPwTv.setOnClickListener(view -> {
            //FindIdPwFragment filterFragment = new FindIdPwFragment();
            //filterFragment.show(getSupportFragmentManager(), "bottom sheet");
            FindIdPwFragment.newInstance("계정 찾기").show(getSupportFragmentManager(),"findId");
        });
    }

    private void initLayout() {
        setSupportActionBar(signInToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
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

    @OnClick({R.id.emailSignInFloatingBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.emailSignInFloatingBtn:
                validator.validate();
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        mAuth
                .signInWithEmailAndPassword(emailEt.getText().toString(), pwEt.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent intent = new Intent(AuthEmailSignInActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof FirebaseAuthInvalidUserException){
                    Toasty.normal(AuthEmailSignInActivity.this,getString(R.string.toast_AuthEmailSignInActivity_idFail),Toast.LENGTH_SHORT).show();
                }else if(e instanceof FirebaseAuthInvalidCredentialsException){
                    Toasty.normal(AuthEmailSignInActivity.this,getString(R.string.toast_AuthEmailSignInActivity_pwFail),Toast.LENGTH_SHORT).show();
                }else{
                    Toasty.normal(AuthEmailSignInActivity.this, getString(R.string.toast_commons_fail) + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Timber.e(message);
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
