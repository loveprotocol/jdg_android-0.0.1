package com.imhc2.plumboard.mvvm.view.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.eventbus.Subscribe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.functions.FunctionEvents;
import com.imhc2.plumboard.commons.util.CustomViewpager;
import com.imhc2.plumboard.commons.util.SharedPreferencesHelper;
import com.imhc2.plumboard.mvvm.model.domain.Auth;
import com.imhc2.plumboard.mvvm.model.domain.CampExe;
import com.imhc2.plumboard.mvvm.model.domain.Cards;
import com.imhc2.plumboard.mvvm.model.domain.Dp;
import com.imhc2.plumboard.mvvm.model.domain.La;
import com.imhc2.plumboard.mvvm.model.domain.Ls;
import com.imhc2.plumboard.mvvm.model.domain.Mc;
import com.imhc2.plumboard.mvvm.model.domain.Sa;
import com.imhc2.plumboard.mvvm.model.domain.Vd;
import com.imhc2.plumboard.mvvm.view.fragment.cardtypes.AuthFragment;
import com.imhc2.plumboard.mvvm.view.fragment.cardtypes.DpFragment;
import com.imhc2.plumboard.mvvm.view.fragment.cardtypes.LaFragment;
import com.imhc2.plumboard.mvvm.view.fragment.cardtypes.LsFragment;
import com.imhc2.plumboard.mvvm.view.fragment.cardtypes.McFragment;
import com.imhc2.plumboard.mvvm.view.fragment.cardtypes.RatingFragment;
import com.imhc2.plumboard.mvvm.view.fragment.cardtypes.SaFragment;
import com.imhc2.plumboard.mvvm.view.fragment.cardtypes.VdFragment;
import com.imhc2.plumboard.mvvm.view.fragment.cardtypes.VrFragment;
import com.imhc2.plumboard.mvvm.view.fragment.dialog.ProgressDialogHelper;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class CardMainActivity extends RxAppCompatActivity {

    @BindView(R.id.activity_card_main_toolbar) Toolbar cardMainToolbar;
    @BindView(R.id.activity_card_main_viewpager) CustomViewpager cardMainViewpager;
    @BindView(R.id.activity_card_main_floating_btn) FloatingActionButton cardMainFloatingBtn;
    @BindView(R.id.activity_card_main_progress) ProgressBar cardMainProgress;
    @BindView(R.id.activity_card_main_back_btn) ImageButton cardMainBackBtn;
    @BindView(R.id.activity_card_main_page_num) TextView cardMainPageNum;
    @BindView(R.id.activity_card_main_fl) FrameLayout mainFl;
    @BindView(R.id.activity_card_main_lotti) LottieAnimationView mLotti;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<Object> resultData = new ArrayList<>();
    private ArrayList<String> types = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Cards> cards = new ArrayList<>();
    private HashMap<Integer, Object> resultMap;
    FirebaseDatabase database;
    FirebaseAuth mAuth;

    CampExe campExe;
    AlertDialog.Builder builder;
    private Boolean isTut = null, isFree = null;
    ProgressDialogHelper dialogHelper;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.get().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dialogHelper.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.get().unregister(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_main);
        ButterKnife.bind(this);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dialogHelper = new ProgressDialogHelper(this);
        resultMap = new HashMap<>();
        builder = new AlertDialog.Builder(CardMainActivity.this);
        initToolbar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorCardBackgroundColor));
        }

        if (getIntent().getParcelableExtra("camp") != null) {
            campExe = getIntent().getParcelableExtra("camp");
            isTut = getIntent().getBooleanExtra("isTut", false);
            isFree = getIntent().getBooleanExtra("isFree", false);
            if (isTut) {
                getCards("tutData");
            } else {
                if (isFree) {
                    getCards("cData");
                } else {
                    getCards("cDataExe");
                }
            }
        }

        SharedPreferencesHelper.put(this, "startTime", System.currentTimeMillis());
        cardMainViewpager.setAllowedSwipeDirection(CustomViewpager.SwipeDirection.left);
        //getEvent();


        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    mainFl.setVisibility(View.INVISIBLE);
                    mainFl.setVisibility(View.GONE);
                } else {
                    mainFl.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    public CustomViewpager getCardMainViewpager() {
        return cardMainViewpager;
    }

    @Subscribe
    public void moveViewPager(Events.HistoryPosition position) {
        //cardMainViewpager.setAllowedSwipeDirection();
        cardMainViewpager.setCurrentItem(position.getPosition());
    }


    private void initToolbar() {
        setSupportActionBar(cardMainToolbar);
        cardMainToolbar.setTitleTextColor(getResources().getColor(R.color.colorPlumBoardSub));
        getSupportActionBar().setTitle(R.string.CardMainActivity_toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_x);
    }

    private void getCards(String child) {
        database.getReference().child(child).child(campExe.getId()).child("data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String type = (String) snapshot.child("type").getValue();
                        switch (type) {
                            case "mC":
                                Mc mc = snapshot.getValue(Mc.class);
                                fragmentList.add(McFragment.newInstance(mc));
                                types.add("mC");
                                titles.add(mc.getTtl());
                                cards.add(mc);
                                break;
                            case "sA":
                                Sa sa = snapshot.getValue(Sa.class);
                                fragmentList.add(SaFragment.newInstance(sa));
                                types.add("sA");
                                titles.add(sa.getTtl());
                                cards.add(sa);
                                break;
                            case "lA":
                                La la = snapshot.getValue(La.class);
                                fragmentList.add(LaFragment.newInstance(la));
                                types.add("lA");
                                titles.add(la.getTtl());
                                cards.add(la);
                                break;
                            case "lS":
                                Ls ls = snapshot.getValue(Ls.class);
                                fragmentList.add(LsFragment.newInstance(ls));
                                types.add("lS");
                                titles.add(ls.getTtl());
                                cards.add(ls);
                                break;
                            case "vd":
                                Vd vd = snapshot.getValue(Vd.class);
                                fragmentList.add(VdFragment.newInstance(vd));
                                types.add("vd");
                                titles.add(vd.getTtl());
                                cards.add(vd);
                                break;
                            case "dP":
                                Dp dp = snapshot.getValue(Dp.class);
                                fragmentList.add(DpFragment.newInstance(dp));
                                types.add("dp");
                                titles.add(dp.getTtl());
                                cards.add(dp);
                                break;
                            case "auth":
                                Auth auth = snapshot.getValue(Auth.class);
                                fragmentList.add(AuthFragment.newInstance(auth));
                                types.add("auth");
                                titles.add(auth.getTtl());
                                cards.add(auth);
                                break;
                        }
                    }
                }

                if (campExe.getCamp().getType() == 1) {
                    fragmentList.add(VrFragment.newInstance());
                    types.add("vr");
                    titles.add(getString(R.string.CardMainActivity_vrTitle));
                    cards.add(null);
                }

                fragmentList.add(RatingFragment.newInstance(campExe.getCamp().getBgt().getJdg()));
                types.add("rating");
                titles.add(getString(R.string.CardMainActivity_ratingTitle));
                cards.add(null);
                initViewPager();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.e("Getcards 에러" + databaseError.getMessage());
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void initViewPager() {
        cardMainPageNum.setText(String.valueOf(1 + "/" + (fragmentList.size() /*- 1*/)));
        cardMainViewpager.setOffscreenPageLimit(fragmentList.size());
        cardMainViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Timber.e("viewpager = onPageSelected = " + position);

                //cardMainPageNum.setText(String.valueOf(position + 1 + "/" + fragmentList.size()));
                //cardMainProgress.setProgress(resultMap.size());
                pagingCheck();


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        cardMainViewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        cardMainProgress.setMax(cardMainViewpager.getAdapter().getCount() - 1);

        cardMainToolbar.setVisibility(View.VISIBLE);
        cardMainViewpager.setVisibility(View.VISIBLE);
        cardMainFloatingBtn.setVisibility(View.VISIBLE);
        mLotti.setVisibility(View.GONE);
    }


    private void pagingCheck() {
        if (cardMainViewpager.getCurrentItem() == resultMap.size()) {
            cardMainViewpager.setAllowedSwipeDirection(CustomViewpager.SwipeDirection.left);//막기
        } else {
            if (types.size() == (cardMainViewpager.getCurrentItem() + 1)) {//평가페이지
                cardMainViewpager.setAllowedSwipeDirection(CustomViewpager.SwipeDirection.left);
                //Log.e("lastBefore", "마지막 전 평가!");
            } else {
                cardMainViewpager.setAllowedSwipeDirection(CustomViewpager.SwipeDirection.all);
            }

        }
    }

    @Subscribe
    public void getEvent(Events.CardResult cardResult) {

        if (resultMap != null) {
            if (cardResult.getCardData() instanceof List) {//mc타입 체크
                if (((List) cardResult.getCardData()).isEmpty()) {//빈문자열체크
                    resultMap.remove(cardMainViewpager.getCurrentItem());
                } else {
                    resultMap.put(cardMainViewpager.getCurrentItem(), cardResult.getCardData());
                }
            } else {
                resultMap.put(cardMainViewpager.getCurrentItem(), cardResult.getCardData());
            }
            pagingCheck();

        }

        Timber.e("cardMainActivity: " + "viewpager=" + cardMainViewpager.getCurrentItem() + ",받은값=" + cardResult.getCardData().toString());
        Timber.e("cardMainActivity:" + "resultMap=" + resultMap.toString() + " size=" + resultMap.size());
        Timber.e("??" + "size:" + types.size() + " currentItem:" + (cardMainViewpager.getCurrentItem() + 1));

        cardMainPageNum.setText(String.valueOf(resultMap.size() + "/" + (fragmentList.size())));
        cardMainProgress.setProgress(resultMap.size());
    }

    private void getTutResult() {

        dialogHelper.show();

        float getRating = Float.parseFloat(String.valueOf(resultMap.get(resultMap.size() - 1)));
        Integer tutNum = null;
        switch (campExe.getId()) {
            case "tut1":
                tutNum = 1;
                break;
            case "tut2":
                tutNum = 2;
                break;
        }

        new FunctionEvents().completeTutorial(tutNum, getRating)
                .addOnCompleteListener(task -> {
                    dialogHelper.dismiss();
                    if (task.isSuccessful()) {
                        Boolean isSuccess = (Boolean) task.getResult().get("success");
                        if (isSuccess) {
                            Intent intent = new Intent(CardMainActivity.this, CardFinishActivity.class);
                            intent.putExtra("point", campExe.getCamp().getBgt().getJdg());
                            intent.putExtra("isTut", true);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = (String) task.getResult().get("message");
                            if (!CardMainActivity.this.isFinishing()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CardMainActivity.this);
                                builder.setMessage(message);
                                builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                Timber.e("completeTutorial fail: " + message);
                            }
                        }

                        Timber.e("completeTutorial = success");
                    } else {
                        if (!CardMainActivity.this.isFinishing()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CardMainActivity.this);
                            builder.setMessage(R.string.dialog_function_error);
                            builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                    }

                });
    }

    private void getResult() {
        dialogHelper.show();
        if (!resultData.isEmpty()) {
            resultData.clear();
        }
        for (int i = 0; i < types.size(); i++) {
            switch (types.get(i)) {
                case "mC":
                    resultData.add(resultMap.get(i).toString());
                    break;
                case "sA":
                    resultData.add(resultMap.get(i).toString());
                    break;
                case "lA":
                    resultData.add(resultMap.get(i).toString());
                    break;
                case "lS":
                    resultData.add(resultMap.get(i).toString());
                    break;
                case "vd":
                    resultData.add(resultMap.get(i + 1).toString());
                    break;
                case "dP":
                    resultData.add(resultMap.get(i).toString());
                    break;
            }
        }
        Timber.e("resultData =" + resultData.toString());

        float getRating = Float.parseFloat(String.valueOf(resultMap.get(resultMap.size() - 1)));

        Timber.e("BeforeendCamp =" + mAuth.getCurrentUser().getUid() + " - " + campExe.getId() + " - " + getRating + " - " + resultData);

        new FunctionEvents().endCampFun(campExe.getId(), getRating, resultData, campExe.getInf().getPrjRf()).addOnCompleteListener(new OnCompleteListener<Map<Object, Object>>() {
            @Override
            public void onComplete(@NonNull Task<Map<Object, Object>> task) {
                dialogHelper.dismiss();
                if (task.isSuccessful()) {//성공
                    Boolean isSuccess = (Boolean) task.getResult().get("success");
                    if (isSuccess) {
                        Timber.e("boostPoint = " + task.getResult().get("boostPoint"));
                        Timber.e("boostPercent = " + task.getResult().get("boostPercent"));

                        Long boostPoint = Long.parseLong(task.getResult().get("boostPoint").toString());
                        Double boostPercent = Double.parseDouble(task.getResult().get("boostPercent").toString());

                        Intent intent = new Intent(CardMainActivity.this, CardFinishActivity.class);
                        intent.putExtra("point", campExe.getCamp().getBgt().getJdg());
                        intent.putExtra("boostPoint", boostPoint);
                        intent.putExtra("boostPercent", boostPercent);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = (String) task.getResult().get("message");
                        errorDialog(message);
                    }
                    //Double boostPoint =  Double.parseDouble(checkCampaign.get("boostPoint").toString());
                    //Double boostPercent = Double.parseDouble(checkCampaign.get("boostPercent").toString());
                    //pageNext();

                } else {
                    errorDialog(getString(R.string.dialog_function_error));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cardmainactivity_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isTut) {
                    setTutDialog();
                } else {
                    if (isFree) {
                        setTutDialog();
                    } else {
                        setDialog();
                    }

                }
//                if (!(types.size() == cardMainViewpager.getCurrentItem() + 1)) {
//                    setDialog();
//                } else {
//                    Intent intent = new Intent(CardMainActivity.this, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//                }
                return true;

            case R.id.card_main_activity_toolbar_history:
                startCardHistory();
                return true;

            case R.id.card_main_activity_toolbar_detail_info:
                Intent intent = new Intent(this, CampaignDetailActivity.class);
                intent.putExtra("campexe", campExe);
                intent.putExtra("cardDetailInfo", true);
                startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void startCardHistory() {
        Intent intent = new Intent(CardMainActivity.this, CardHistoryActivity.class);
        intent.putStringArrayListExtra("titles", titles);
        intent.putStringArrayListExtra("types", types);
        intent.putExtra("position", cardMainViewpager.getCurrentItem());

        intent.putExtra("campexe", campExe);
//        intent.putExtra("camp.ttl",campExe.getCamp().getTtl());
//        intent.putExtra("prj.ttl",campExe.getPrj().getTtl());
//        intent.putExtra("camp.img",campExe.getCamp().getImg());
//        intent.putExtra("prj.img",campExe.getPrj().getTImg());

        intent.putExtra("resultMap", resultMap);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_down , R.anim.slide_to_up);
        //EventBus.get().post(new Events.HistoryData(titles,types,cardMainViewpager.getCurrentItem(),campExe,resultMap));

    }

    @OnClick({R.id.activity_card_main_floating_btn, R.id.activity_card_main_back_btn, R.id.activity_card_main_progress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_card_main_floating_btn:
                //현재페이지<데이터크기 && 데이터안에 현재 페이지의 result값 있나 확인
                if (cardMainViewpager.getCurrentItem() < resultMap.size() && resultMap.containsKey(cardMainViewpager.getCurrentItem())) {
                    if (types.size() == (cardMainViewpager.getCurrentItem() + 1)) {//평가페이지(마지막전)
                        if (isTut) {
                            if (isFree) {
                                finish();
                            } else {
                                getTutResult();
                            }
                        } else {
                            if (isFree) {
                                Intent intent = new Intent(CardMainActivity.this, CardFinishActivity.class);
                                intent.putExtra("point", 0);
                                intent.putExtra("isTut", true);
                                startActivity(intent);
                                finish();
                            } else {
                                getResult();
                            }
                        }
                    } else {
                        pageNext();
                    }

                } else {
                    switch (types.get(cardMainViewpager.getCurrentItem())) {
                        case "vd":
                            Toasty.normal(getApplicationContext(), getString(R.string.toast_CardMainActivity_videoNotYetDone), Toast.LENGTH_SHORT).show();
                            break;
                        case "mC":
                            Mc mc = (Mc) cards.get(cardMainViewpager.getCurrentItem());
                            if(mc.getMA()){
                                if (mc.getML() != null && mc.getMH() == null) {
                                    Toasty.normal(this, "최소 " + mc.getML() + "개 이상의 답변을 선택해야 합니다", Toast.LENGTH_SHORT).show();
                                } else if (mc.getMH() != null && mc.getML() == null) {
                                    Toasty.normal(this, "최대 " + mc.getMH() + "개 이하의 답변을 선택해야 합니다", Toast.LENGTH_SHORT).show();
                                } else if (mc.getMH() != null && mc.getML() != null) {
                                    Toasty.normal(this, "정확히 " + mc.getMH() + "개의 답변을 선택해야 합니다", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toasty.normal(getApplicationContext(), getString(R.string.toast_CardMainActivity_answerNotYet), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        default:
                            Toasty.normal(getApplicationContext(), getString(R.string.toast_CardMainActivity_answerNotYet), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                break;

            case R.id.activity_card_main_back_btn:
                cardMainViewpager.setCurrentItem(cardMainViewpager.getCurrentItem() - 1);
                break;

            case R.id.activity_card_main_progress:
                if (cardMainViewpager.getVisibility() == View.VISIBLE) {
                    startCardHistory();
                }
                break;
        }
    }

    private void pageNext() {
        int next = cardMainViewpager.getCurrentItem() + 1;
        cardMainViewpager.setCurrentItem(next);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isTut) {
            setTutDialog();
        } else {
            if (isFree) {
                setTutDialog();
            } else {
                setDialog();
            }
        }

    }

    private void setTutDialog() {
        builder.setTitle(R.string.dialog_title_campaignCancel);
        builder.setMessage("현재까지의 진행 상태가 모두 삭제됩니다");
        builder.setPositiveButton(R.string.dialog_positive_check, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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

    private void setDialog() {
        //String[] items = {"진행 상태 저장하기"};
        //boolean[] mSelected = {false};
        builder.setTitle(R.string.dialog_title_campaignCancel);
        builder.setMessage("현재까지의 진행 상태가 모두 삭제됩니다");
        builder.setPositiveButton(R.string.dialog_positive_check, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //progressFragment.show(getSupportFragmentManager(), "progress2");
                dialogHelper.show();
                new FunctionEvents().cancelCampFun(campExe.getId())
                        .addOnCompleteListener(task -> {
                            dialogHelper.dismiss();
                            Boolean isSuccess = (Boolean) task.getResult().get("success");
                            if (task.isSuccessful()) {
                                if (isSuccess) {
                                    finish();
                                } else {
                                    String message = (String) task.getResult().get("message");
                                    errorDialog(message);
                                }
                            } else {
                                Timber.e("cancelCampFun fail: " + task.getException().getMessage());
                                errorDialog(getString(R.string.dialog_function_error));

                            }
                        });
            }
        }).setNegativeButton(R.string.dialog_negative_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        TextView messageTv = dialog.findViewById(android.R.id.message);
        messageTv.setTextColor(Color.parseColor("#F44336"));

    }

    private void errorDialog(String title) {
        if (!CardMainActivity.this.isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CardMainActivity.this);
            builder.setMessage(title);
            builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

}
