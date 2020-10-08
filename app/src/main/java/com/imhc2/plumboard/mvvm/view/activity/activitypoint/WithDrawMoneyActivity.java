package com.imhc2.plumboard.mvvm.view.activity.activitypoint;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.eventbus.Subscribe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.functions.FunctionEvents;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.commons.util.TextInputLayoutAdapter;
import com.imhc2.plumboard.mvvm.view.fragment.bottomsheets.BankFragment;
import com.imhc2.plumboard.mvvm.view.fragment.dialog.ProgressDialogHelper;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class WithDrawMoneyActivity extends AppCompatActivity implements Validator.ValidationListener {


    @BindView(R.id.activity_with_draw_money_toolbar) Toolbar mToolbar;

    @BindView(R.id.activity_with_draw_money_bank_et) TextInputEditText bankEt;
    @BindView(R.id.activity_with_draw_money_money_et) TextInputEditText moneyEt;
    @BindView(R.id.activity_with_draw_money_bank_num_et) TextInputEditText bankNumEt;
    @BindView(R.id.activity_with_draw_money_name_et) TextInputEditText nameEt;

    @NotEmpty(trim = true, message = "금액을 입력해주세요")
    @BindView(R.id.activity_with_draw_money_money_til) TextInputLayout moneyTil;
    @NotEmpty(trim = true, message = "은행명을 선택해주세요")
    @BindView(R.id.activity_with_draw_money_bank_til) TextInputLayout bankTil;
    @NotEmpty(trim = true, message = "이름을 입력해주세요")
    @BindView(R.id.activity_with_draw_money_name_til) TextInputLayout nameTil;
    @NotEmpty(trim = true, message = "계좌번호를 입력해주세요")
    @BindView(R.id.activity_with_draw_money_banknum_til) TextInputLayout banknumTil;

    @BindView(R.id.activity_with_draw_money_bottom_ll) LinearLayout bottomLl;
    @BindView(R.id.activity_with_draw_money_point_tv) TextView pointTv;
    boolean focus;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    Double point = 0d;
    @BindView(R.id.activity_with_draw_money_sub_tv1) TextView subTv1;
    @BindView(R.id.activity_with_draw_money_sub_tv2) TextView subTv2;
    @BindView(R.id.activity_with_draw_money_sub_tv3) TextView subTv3;
    @BindView(R.id.activity_with_draw_money_sub_tv4) TextView subTv4;
    @BindView(R.id.activity_with_draw_money_sub_tv5) TextView subTv5;
    @BindView(R.id.activity_with_draw_money_btn) Button moneyDrawBtn;
    private Validator validator;

    private final static String BANK = "bank";

    @Override
    public void onStart() {
        super.onStart();
        EventBus.get().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw_money);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        Views();

        initValidation();
        keyboardEvent();
        getPoint();

    }

    private void getPoint() {
        if (mAuth.getCurrentUser() != null) {
            FirestoreQuerys.INSTANCE.getUserCrd(firestore, mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                point = Double.parseDouble(document.get(FieldPath.of("jdg", "pt", "crtTot")).toString());
                                pointTv.setText(NumberFormat.getInstance().format(point.intValue()));
                            }
                        }
                    });
        }
    }

    private void initValidation() {
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

    private void keyboardEvent() {
        moneyEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.equals("") && s.length() > 0) {
                    Integer num = Integer.parseInt(s.toString());
                    if (point.intValue() >= num) {
                        if (10000 <= point.intValue() && point.intValue() >= 50000) {

                        } else {
                            //moneyEt.setText(String.valueOf(point));
                        }
                    } else {
                        moneyEt.setText(String.valueOf(point.intValue()));
                    }
                } else {

                }


            }
        });


        moneyEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    bottomLl.setVisibility(View.VISIBLE);
                    focus = true;
                } else {
                    bottomLl.setVisibility(View.GONE);
                    focus = false;
                }
            }
        });

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    if (focus) {
                        bottomLl.setVisibility(View.VISIBLE);
                    }

                    if (!(TextUtils.isEmpty(bankEt.getText()) && TextUtils.isEmpty(moneyEt.getText()) && TextUtils.isEmpty(bankNumEt.getText()) && TextUtils.isEmpty(nameEt.getEditableText()))) {
                        moneyDrawBtn.setBackgroundColor(getResources().getColor(R.color.colorPlumBoard));
                    } else {
                        moneyDrawBtn.setBackgroundColor(Color.parseColor("#9E9E9E"));
                    }

                } else {
                    bottomLl.setVisibility(View.GONE);
                }

            }
        });
    }

    private void Views() {
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorPlumBoardSub));
        mToolbar.setTitle(R.string.WithDrawMoneyActivity_toolbarTitle);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String sb1 = getString(R.string.WithDrawMoneyActivity_sub1);
        subTv1.setText(Html.fromHtml(sb1));

        String sb2 = getString(R.string.WithDrawMoneyActivity_sub2);
        subTv2.setText(Html.fromHtml(sb2));

        String sb3 = getString(R.string.WithDrawMoneyActivity_sub3);
        subTv3.setText(Html.fromHtml(sb3));

        String sb4 = getString(R.string.WithDrawMoneyActivity_sub4);
        subTv4.setText(Html.fromHtml(sb4));

        String sb5 = getString(R.string.WithDrawMoneyActivity_sub5);
        subTv5.setText(Html.fromHtml(sb5));

    }

    @Subscribe
    public void bank(Events.Bank bank) {
        bankEt.setText(bank.getBankName());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.get().unregister(this);
    }

    @OnClick({R.id.activity_with_draw_money_bank_et, R.id.activity_with_draw_money_btn, R.id.activity_with_draw_money_1_btn, R.id.activity_with_draw_money_5_btn, R.id.activity_with_draw_money_all_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_with_draw_money_bank_et:
                Fragment f = getSupportFragmentManager().findFragmentByTag(BANK);
                if (f == null) {
                    BankFragment bankFragment = new BankFragment();
                    bankFragment.show(getSupportFragmentManager(), BANK);
                }

                break;
            case R.id.activity_with_draw_money_1_btn:
                moneyCheck(10000);
                break;
            case R.id.activity_with_draw_money_5_btn:
                moneyCheck(50000);
                break;
            case R.id.activity_with_draw_money_all_btn:
                moneyEt.setText(String.valueOf(point.intValue()));
                break;
            case R.id.activity_with_draw_money_btn:
                validator.validate();
                break;
        }
    }

    private void moneyCheck(int money) {
        if (!moneyEt.equals("") && moneyEt.length() > 0) {
            int num = Integer.parseInt(moneyEt.getText().toString());

            num += money;
            if (num < point.intValue()) {
                moneyEt.setText(String.valueOf(num));
            } else {
                moneyEt.setText(String.valueOf(point.intValue()));
            }
        } else {
            int num = money;
            if (num < point.intValue()) {
                moneyEt.setText(String.valueOf(num));
            } else {
                moneyEt.setText(String.valueOf(point.intValue()));
            }
        }
    }

    @Override
    public void onValidationSucceeded() {
        if (10000 <= Integer.parseInt(moneyEt.getText().toString()) && Integer.parseInt(moneyEt.getText().toString()) <= 50000) {
            ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(WithDrawMoneyActivity.this);
            progressDialogHelper.show();
            new FunctionEvents()
                    .requestWithdraw(Integer.valueOf(moneyEt.getText().toString()), bankEt.getText().toString(), bankNumEt.getText().toString(), nameEt.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Map<Object, Object>>() {
                        @Override
                        public void onComplete(@NonNull Task<Map<Object, Object>> task) {
                            progressDialogHelper.dismiss();
                            if (task.isSuccessful()) {
                                Boolean isSuccess = (Boolean) task.getResult().get("success");
                                if (isSuccess) {
                                    Toasty.normal(getApplicationContext(), getString(R.string.toast_WithDrawMoneyActivity_success), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    String message = (String) task.getResult().get("message");
                                    //Toasty.normal(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    if (WithDrawMoneyActivity.this.isFinishing()) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(WithDrawMoneyActivity.this);
                                        builder.setMessage(message);
                                        builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.create().show();
                                        Timber.e("requestWithdraw fail: " + message);
                                    }
                                }
                            } else {
                                if (WithDrawMoneyActivity.this.isFinishing()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(WithDrawMoneyActivity.this);
                                    builder.setMessage(getString(R.string.dialog_function_error));
                                    builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                    Timber.e("requestWithdraw fail: " + task.getException().getMessage());
                                }
                            }

                        }
                    });
        } else {
            Timber.e("WithDrawMoney = " + moneyEt.getText().toString());
            if (Integer.parseInt(moneyEt.getText().toString()) < 10000) {
                moneyTil.setError("출금 신청 최소 금액은 1만원 입니다");
            }
            if (Integer.parseInt(moneyEt.getText().toString()) > 50000) {
                moneyTil.setError("출금 신청 최대 금액은 5만원 입니다");
            }

            //Toasty.normal(getApplicationContext(), getString(R.string.toast_WithDrawMoneyActivity_moneyCheck), Toast.LENGTH_SHORT).show();
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
}
