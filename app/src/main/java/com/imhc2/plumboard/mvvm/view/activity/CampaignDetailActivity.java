package com.imhc2.plumboard.mvvm.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.imhc2.plumboard.BuildConfig;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.functions.FunctionEvents;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.commons.util.CustomImageView43;
import com.imhc2.plumboard.commons.util.SharedPreferencesHelper;
import com.imhc2.plumboard.mvvm.model.domain.CampExe;
import com.imhc2.plumboard.mvvm.model.domain.CampPart;
import com.imhc2.plumboard.mvvm.view.activity.auth.AuthMainActivity;
import com.imhc2.plumboard.mvvm.view.fragment.dialog.ProgressDialogHelper;
import com.willy.ratingbar.BaseRatingBar;

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class CampaignDetailActivity extends AppCompatActivity {

    @BindView(R.id.activity_campaign_detail_toolbar) Toolbar toolbar;
    @BindView(R.id.activity_campaign_detail_main_img) CustomImageView43 mainImg;
    @BindView(R.id.activity_campaign_detail_title_tv) TextView titleTv;
    @BindView(R.id.activity_campaign_detail_project_tv) TextView projectTv;
    @BindView(R.id.activity_campaign_detail_project_img) ImageView projectImg;
    @BindView(R.id.activity_campaign_detail_content_tv) TextView contentTv;
    @BindView(R.id.activity_campaign_detail_kind_tv) TextView kindTv;
    @BindView(R.id.activity_campaign_detail_point_tv) TextView pointTv;
    @BindView(R.id.activity_campaign_detail_body_ratingbar) BaseRatingBar bodyRatingbar;
    @BindView(R.id.activity_campaign_detail_body_ratingbar_tv) TextView bodyRatingbarTv;
    @BindView(R.id.activity_campaign_detail_bottom_point) TextView bottomPoint;
    @BindView(R.id.activity_campaign_detail_bottom_ratingbar) BaseRatingBar bottomRatingbar;
    @BindView(R.id.activity_campaign_detail_bottom_ratingbar_tv) TextView bottomRatingbarTv;
    @BindView(R.id.activity_campaign_detail_bottom_bar) LinearLayout bottomBarLL;

    @BindView(R.id.activity_campaign_detail_coll_age) LinearLayout collAge;
    @BindView(R.id.activity_campaign_detail_coll_gender) LinearLayout collGender;
    @BindView(R.id.activity_campaign_detail_coll_location) LinearLayout collLocation;
    @BindView(R.id.activity_campaign_detail_coll_empty) LinearLayout collEmpty;

    @BindView(R.id.activity_campaign_detail_startBtn) Button startBtn;
    @BindString(R.string.dynamic_link_share) String dynamicLink;

    CampExe campExe;
    FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private Boolean isTut = null, isFree = null;
    ProgressDialogHelper progressDialogHelper;
    Integer recaptchaCount = 0;
    int history = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_detail);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        progressDialogHelper = new ProgressDialogHelper(CampaignDetailActivity.this);
        initToolbar();

        getCamExeData();

        addDataIntoView();

        clickEvent();

        //status바 없애기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressDialogHelper.dismiss();
    }

    private void clickEvent() {

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BuildConfig.DEBUG) {
                    Timber.e("debug");
                    startCardActivity();
                } else {
                    Timber.e("release");

                    recaptchaCount = (Integer) SharedPreferencesHelper.get(CampaignDetailActivity.this, "recaptchaCount", 10);
                    if (recaptchaCount == 10) {
                        if (!isFree) {
                            getRecaptcha();
                        } else {
                            startCardActivity();
                        }
                        recaptchaCount = 0;
                        SharedPreferencesHelper.put(CampaignDetailActivity.this, "recaptchaCount", recaptchaCount);
                    } else {
                        recaptchaCount++;
                        SharedPreferencesHelper.put(CampaignDetailActivity.this, "recaptchaCount", recaptchaCount);
                        startCardActivity();
                    }


                }
            }
        });


//        RxView.clicks(startBtn)
//                .debounce(300, TimeUnit.MILLISECONDS)
//                .subscribe(aBoolean -> {
////                    AppCompatDialog appCompatDialog = new AppCompatDialog(this);
////                    appCompatDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////                    appCompatDialog.setContentView(R.layout.fragment_progress);
////                    appCompatDialog.setCancelable(false);
////
////                    appCompatDialog.show();
//
//                    if (mAuth.getCurrentUser() != null) {
//                        //캠페인 진행중인거 sts1 불러와서 아닐때만 function날리기
//                        firestore.collection("campPart")
//                                .whereEqualTo("sts", 1)//진행중
//                                .whereEqualTo(FieldPath.of("inf", "cB"), mAuth.getCurrentUser().getUid())//내꺼
//                                .whereEqualTo(FieldPath.of("inf", "campRf"), campExe.getId())
//                                //.whereGreaterThanOrEqualTo("scdED", System.currentTimeMillis())
//                                .get()
//                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                        //appCompatDialog.dismiss();
//                                        if (task.isSuccessful()) {
//                                            for (DocumentSnapshot snapshot : task.getResult()) {
//                                                Timber.e("campPart : " + snapshot.getData());
//                                                if (snapshot.exists()) {//있니
//                                                    doingCheck = true;
//                                                    Timber.e("campPart 있다 ");
//                                                } else {//없니
//                                                    Timber.e("campPart 없다");
//                                                }
//                                            }
//                                            Timber.e("내가 캠페인 시작을 했나 안했나 ? = " + doingCheck);
//                                            if (doingCheck) {
//                                                Intent intent = new Intent(CampaignDetailActivity.this, CardMainActivity.class);
//                                                intent.putExtra("camp", campExe);
//                                                gotoActivity(intent);
//                                            } else {
//                                                new FunctionEvents().startCampFun(campExe.getId(), mAuth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Object>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Object> task) {
//
//                                                        if (!task.isSuccessful()) {
//                                                            Timber.e("startCampFun fail = " + task.getException().getMessage());
//                                                        } else {//성공
//                                                            Timber.e("캠페인 시작 startCampFunction 실행");
//                                                            Boolean checkCampaign = (Boolean) task.getResult();
//                                                            if (checkCampaign) {
//                                                                Intent intent = new Intent(CampaignDetailActivity.this, CardMainActivity.class);
//                                                                intent.putExtra("camp", campExe);
//                                                                gotoActivity(intent);
//                                                            } else {
//                                                                Toast.makeText(CampaignDetailActivity.this, "오류!! 잠시후 다시 실행해 주세요", Toast.LENGTH_SHORT).show();
//                                                                Timber.e("checkCampaign fail:" + task.getResult());
//
//                                                            }
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    }
//                                });
//
//                    } else {
//                        //appCompatDialog.dismiss();
//                        Intent intent = new Intent(CampaignDetailActivity.this, AuthMainActivity.class);
//                        gotoActivity(intent);
//                    }
//                });
    }


    private void getRecaptcha() {
        SafetyNet.getClient(CampaignDetailActivity.this).verifyWithRecaptcha(getString(R.string.recaptchaKey))
                .addOnSuccessListener((Activity) CampaignDetailActivity.this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        // Indicates communication with reCAPTCHA service was
                        // successful.
                        String userResponseToken = response.getTokenResult();
                        if (!userResponseToken.isEmpty()) {
                            startCardActivity();
                        }
                    }
                })
                .addOnFailureListener((Activity) CampaignDetailActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.e("Recaptcha e" + e.getMessage());
                        if (e instanceof ApiException) {
                            Timber.e("Recaptcha apiException");
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            //Log.d(TAG, "Error: " + CommonStatusCodes.getStatusCodeString(statusCode));
                            Timber.e(CommonStatusCodes.getStatusCodeString(statusCode));
                        } else {
                            Timber.e("Recaptcha 실패");
                            // A different, unknown type of error occurred.
                            //Log.d(TAG, "Error: " + e.getMessage());
                            Timber.e(e.getMessage());
                        }
                    }
                });

    }


    private void startCardActivity() {
        if (mAuth.getCurrentUser() != null) {
            progressDialogHelper.show();
            if (isTut) {//튜토리얼
                progressDialogHelper.dismiss();
                Intent intent = new Intent(CampaignDetailActivity.this, CardMainActivity.class);
                intent.putExtra("camp", campExe);
                intent.putExtra("isTut", isTut);
                intent.putExtra("isFree", isFree);
                startActivity(intent);
            } else {
                if (isFree) {//미리보기 위한 적립안되는옵션
                    progressDialogHelper.dismiss();
                    Intent intent = new Intent(CampaignDetailActivity.this, CardMainActivity.class);
                    intent.putExtra("camp", campExe);
                    intent.putExtra("isTut", isTut);
                    intent.putExtra("isFree", isFree);
                    startActivity(intent);
                } else {
                    FirestoreQuerys.INSTANCE.checkMyCampPart(firestore, mAuth.getCurrentUser().getUid())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    List<CampPart> campParts = task.getResult().toObjects(CampPart.class);
                                    if (task.getResult().isEmpty()) {
                                        startCampaignFun();
                                    } else {
                                        progressDialogHelper.dismiss();
                                        if (history == 1) {
                                            progressDialogHelper.show();
                                            startCampaignFun();
                                        } else if(history==0){
                                            String docIdUid= task.getResult().getDocuments().get(0).getId();
                                            String docId = docIdUid.substring(0,docIdUid.indexOf("-"));
                                            if(docId.equals(campExe.getId())){
                                                Intent intent = new Intent(CampaignDetailActivity.this, CardMainActivity.class);
                                                intent.putExtra("camp", campExe);
                                                intent.putExtra("isTut", isTut);
                                                intent.putExtra("isFree", isFree);
                                                startActivity(intent);
                                            }else{
                                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                                builder.setMessage("이미 진행중인 캠페인이 존재합니다.새로운 캠페인에 참가하시겠습니까?(기존 진행 정보는 삭제됩니다)");
                                                builder.setPositiveButton(R.string.dialog_positive_check, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        progressDialogHelper.show();
                                                        startCampaignFun();
                                                    }
                                                }).setNegativeButton(R.string.dialog_negative_cancel, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).create().show();
                                            }

                                        }
                                    }
                                }
                            });
                }

            }

        } else {
            if (!this.isFinishing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_title_needLogin);
                builder.setPositiveButton(R.string.dialog_positive_check, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CampaignDetailActivity.this, AuthMainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                }).setNegativeButton(R.string.dialog_negative_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
        }
    }

    private void startCampaignFun() {
        //참가function
        new FunctionEvents().startCampFun(campExe.getId()).addOnCompleteListener(task -> {
            progressDialogHelper.dismiss();
            if (task.isSuccessful()) {
                Timber.e("캠페인 시작 startCampFunction 실행");
                Boolean isSuccess = (Boolean) task.getResult().get("success");
                Timber.e("campaign 시작 result = " + task.getResult());
                if (isSuccess) {
                    Intent intent = new Intent(CampaignDetailActivity.this, CardMainActivity.class);
                    intent.putExtra("camp", campExe);
                    intent.putExtra("isTut", isTut);
                    intent.putExtra("isFree", isFree);
                    startActivity(intent);
                } else {
                    String message = (String) task.getResult().get("message");
                    campaignFailDialog(message);
                    //Toast.makeText(CampaignDetailActivity.this, "오류!! 잠시후 다시 실행해 주세요", Toast.LENGTH_SHORT).show();
                    Timber.e("checkCampaign fail:" + task.getResult());
                }

            } else {
                campaignFailDialog(getString(R.string.dialog_function_error));
            }
        });
    }

    private void campaignFailDialog(String title) {
        if (!CampaignDetailActivity.this.isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CampaignDetailActivity.this);
            builder.setMessage(title);
            builder.setPositiveButton(R.string.dialog_positive_check, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Intent intent = new Intent(CampaignDetailActivity.this, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    public void addDataIntoView() {
        titleTv.setText(campExe.getCamp().getTtl());
        Glide.with(this).load(campExe.getCamp().getImg()).thumbnail(0.1f).apply(new RequestOptions()/*.override(1280, 720)*/.placeholder(R.drawable.ic_campaign_empty).optionalCenterCrop()).into(mainImg);
        contentTv.setText(campExe.getCamp().getBd());
        projectTv.setText("게시자: " + campExe.getPrj().getTtl());
        Glide.with(this).load(campExe.getPrj().getTImg()).thumbnail(0.1f).apply(new RequestOptions().override(1280, 720).placeholder(R.drawable.ic_campaign_empty).circleCrop()).into(projectImg);

        switch (campExe.getCamp().getType()) {
            case 1:
                kindTv.setText(R.string.CampaignDetailActivity_video);
                break;
            case 2:
                kindTv.setText(R.string.CampaignDetailActivity_survey);
                break;
            case 3:
                kindTv.setText(R.string.CampaignDetailActivity_free);
                break;
        }

        NumberFormat nf = NumberFormat.getInstance();
        nf.format(campExe.getCamp().getBgt().getJdg());

        pointTv.setText(nf.format(campExe.getCamp().getBgt().getJdg()));
        bodyRatingbar.setRating(Float.valueOf(campExe.getCamp().getRtg().getAvg()));
        bodyRatingbarTv.setText((Math.round(campExe.getCamp().getRtg().getAvg() * 10) / 10.0) + "(" + campExe.getCamp().getRtg().getCt() + ")");
        bottomRatingbar.setRating(Float.valueOf(campExe.getCamp().getRtg().getAvg()));
        bottomPoint.setText(nf.format(campExe.getCamp().getBgt().getJdg()));
        bottomRatingbarTv.setText((Math.round(campExe.getCamp().getRtg().getAvg() * 10) / 10.0) + "(" + campExe.getCamp().getRtg().getCt() + ")");

        if (campExe.getCamp().getCD() != null) {
            if (campExe.getCamp().getCD().getAge()) {
                collAge.setVisibility(View.VISIBLE);
            }
            if (campExe.getCamp().getCD().getGndr()) {
                collGender.setVisibility(View.VISIBLE);
            }
            if (campExe.getCamp().getCD().getLoc()) {
                collLocation.setVisibility(View.VISIBLE);
            }

            if (!campExe.getCamp().getCD().getAge() && !campExe.getCamp().getCD().getGndr() && !campExe.getCamp().getCD().getLoc()) {
                collEmpty.setVisibility(View.VISIBLE);
            }
        }

    }

    public void getCamExeData() {
        Intent intent = getIntent();
        if (intent.getParcelableExtra("campexe") != null) {
            campExe = intent.getParcelableExtra("campexe");
            isTut = intent.getBooleanExtra("isTut", false);
            isFree = intent.getBooleanExtra("isFree", false);
        }

        history = intent.getIntExtra("campHistory", 0);

        switch (history) {
            case 0://null
                if(mAuth.getCurrentUser() !=null){
                    FirestoreQuerys.INSTANCE.checkMyCampPart(firestore, mAuth.getCurrentUser().getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        if(!task.getResult().isEmpty()){
                                            String docIdUid= task.getResult().getDocuments().get(0).getId();
                                            String docId = docIdUid.substring(0,docIdUid.indexOf("-"));
                                            if(docId.equals(campExe.getId())){
                                                startBtn.setText(R.string.CampaignDetailActivity_startOn);
                                            }
                                        }
                                    }
                                }
                            });
                }
                break;
            case 1://진행중
                startBtn.setText(R.string.CampaignDetailActivity_startOn);
                break;
            case 2://진행완료
                startBtn.setEnabled(false);
                startBtn.setText(R.string.CampaignDetailActivity_evaluate);
                startBtn.setBackgroundColor(Color.parseColor("#D3D3D3"));
                break;

        }

        if (intent.getBooleanExtra("cardDetailInfo", false)) {
            bottomBarLL.setVisibility(View.GONE);
        }

    }


    public void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_x);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.campaign_detail_toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.campaign_detail_toolbar_share:
                makeLink();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void makeLink() {
        progressDialogHelper.show();
        Uri uri = Uri.parse("https://www.naver.com/" + "share" + "?" + "campId" + "=" + campExe.getId()).buildUpon().appendQueryParameter("data", "tt").build();
        //Uri uri = Uri.parse("https://www.naver.com/" + "share" + "?" + "campId" + "=" + campExe.getId());
        Timber.e("uri = " + uri);
//        Timber.e("uri Test = "+uri.buildUpon().appendPath("campaign").appendQueryParameter("campId",campExe.getId()));
//        Uri uri2 = uri.buildUpon().appendPath("campaign").appendQueryParameter("campId",campExe.getId()).build();
//        Timber.e("uri2 Test = "+ uri2.toString());

        FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(uri2)
                .setLink(uri)
                //.setDomainUriPrefix("imhc.page.link")
                .setDynamicLinkDomain("imhc.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(getPackageName())
                                .setMinimumVersion(1)
                                .build()
                )
                .buildShortDynamicLink(/*ShortDynamicLink.Suffix.SHORT*/)
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            System.out.printf("shortLink : " + shortLink.toString());
                            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            //String text = "https://imhc.page.link/?link=https://www.naver.com&apn=com.imhc.plumboard"+"&campId=" + campExe.getId();
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            intent.setType("text/plain");
                            Intent chooser = Intent.createChooser(intent, getString(R.string.intent_shareDefault));
                            startActivity(chooser);
                            progressDialogHelper.dismiss();
                        } else {
                            //Log.w(TAG, task.toString());
                            progressDialogHelper.dismiss();
                            Toasty.normal(CampaignDetailActivity.this, "실패", Toast.LENGTH_SHORT).show();
                            Timber.e("link make fail: " + task.getException().getMessage());
                        }
                    }
                });

    }

}
