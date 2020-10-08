package com.imhc2.plumboard.mvvm.view.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.imhc2.plumboard.R;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthAlreadySignedUpActivity extends AppCompatActivity{

    @BindView(R.id.authAlreadyToolbar) Toolbar authAlreadyToolbar;

    @NotEmpty(trim = true, message = "비어있습니다")
    @Password(min = 6, scheme = Password.Scheme.ALPHA_NUMERIC, message = "최소6자리이상 영문,숫자로 해주세요")

    @BindString(R.string.dynamic_link) String dynamicLink;
    private String email;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_already_signed_up);
        ButterKnife.bind(this);
        mAuth=FirebaseAuth.getInstance();
        initLayout();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
    }

    private void initLayout(){
        setSupportActionBar(authAlreadyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    @OnClick({R.id.authAlreadyLoginBtn, R.id.authAlreadySendBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.authAlreadySendBtn:
                //sendEmail();
                Intent intent = new Intent(AuthAlreadySignedUpActivity.this,AuthEmailSendDoneActivity.class);
                intent.putExtra("email",email);
                intent.putExtra("findPw",true);
                startActivity(intent);
                break;
            case R.id.authAlreadyLoginBtn:
                Intent intent1 = new Intent(AuthAlreadySignedUpActivity.this,AuthMainActivity.class);
                startActivity(intent1);
                break;
        }
    }


    public void showEmailApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        startActivity(Intent.createChooser(intent, getString(R.string.intent_emailApp)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
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
