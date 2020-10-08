package com.imhc2.plumboard.mvvm.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.eventbus.Subscribe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.adapter.MainFragmentPagerAdapter;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.functions.FunctionEvents;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.commons.util.CustomViewpager;
import com.imhc2.plumboard.commons.util.SharedPreferencesHelper;
import com.imhc2.plumboard.mvvm.model.domain.CampExe;
import com.imhc2.plumboard.mvvm.view.activity.auth.AuthMainActivity;
import com.imhc2.plumboard.mvvm.view.activity.auth.AuthPwSignUpActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

public class MainActivity extends RxAppCompatActivity {

    @BindView(R.id.activity_main_bottom_navigation_view) BottomNavigationView mainNavi;
    @BindView(R.id.activity_main_viewpager) CustomViewpager mainViewPager;
    @BindView(R.id.activity_main_toolbar) Toolbar mainToolbar;
    //@BindView(R.id.activity_main_appBarLayout) AppBarLayout appBarLayout;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    MainFragmentPagerAdapter mainFragmentPagerAdapter;

    private Disposable backPressedDisposable;
    private BehaviorSubject<Long> backPressedSubject = BehaviorSubject.createDefault(0L);
    Boolean campViewcheck = true;
    AlertDialog.Builder builder;
    boolean notUserDialogCheck = false;

    int itemId;
    Long idTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        builder = new AlertDialog.Builder(this);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        initToolbar();
        initNavigationView();
        getDynamicLink();
        initAuthState();
        mAuth.addAuthStateListener(mAuthStateListener);
        getBackPressedDisposable();

//        FirebaseUserMetadata metadata = mAuth.getCurrentUser().getMetadata();
//
//        Timber.e("CreationTimestamp() = " + convertData(metadata.getCreationTimestamp()) + " LastSignInTimestamp() = " + convertData(metadata.getLastSignInTimestamp()));
//
//        if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
//
//        }


        try {
            String device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Timber.e("device_version = " + device_version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }


    private Disposable getBackPressedDisposable() {
        return backPressedDisposable = backPressedSubject
                .buffer(2, 1) // back 버튼을 한 번 누르면, 이전에 눌렀던 시간과 방금 누른 시간 2개의 값을 발행한다.
                .map(it -> new Pair<>(it.get(0), it.get(1))) // 비교하기 쉽게 Pair로 변환
                .map(pair -> pair.second - pair.first < TimeUnit.SECONDS.toMillis(2)) // 두 번째 누른 시간이 첫 번째 누른 시간보다 2초 이내인가
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(willFinish -> {
                    if (willFinish) { // 2초 이내에 눌렀다면 종료
                        finish();
                    } else { // 아니면 toast 만 표시
                        Toasty.normal(getApplicationContext(), getString(R.string.toast_MainActivity_appClose), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void refresh() {
        //mainFragmentPagerAdapter.getItemPosition(1);
//        Observable
//                .create(emitter -> mainViewPager.getAdapter().notifyDataSetChanged())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe();
//        mainViewPager.getAdapter().instantiateItem(mainViewPager,mainViewPager.getCurrentItem());
//        Observable
//                .create(emitter -> mainViewPager.getAdapter().instantiateItem(mainViewPager,mainViewPager.getCurrentItem()))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(o -> {
//                    mainViewPager.getAdapter().notifyDataSetChanged();
//                });

    }

    public BottomNavigationView getBottomNavi() {
        return mainNavi;
    }

    private void initAuthState() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                        //최초 회원가입시
                    }

                    FirestoreQuerys.INSTANCE.updateMyInfo(user.getUid(), FirestoreQuerys.INSTANCE.getUserUsr(firestore, user.getUid()));

                    Timber.e("mAuthStateListener 실행");

                    FirestoreQuerys.INSTANCE.getUserCrd(firestore, mAuth.getCurrentUser().getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            Integer idSts = Integer.parseInt(task.getResult().get("usr.sts.reg").toString());
                                            if (task.getResult().get("usr.sts.delReqAt") != null) {
                                                idTime = (Long) task.getResult().get("usr.sts.delReqAt");//휴먼은 7일 뒤
                                            }

                                            if (idSts == 1) {
                                                FirestoreQuerys.INSTANCE.getUserUsr(firestore, mAuth.getCurrentUser().getUid())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    if (task.getResult().exists()) {
                                                                        if (task.getResult().get("jdg.mkt.at") == null) {
                                                                            initMarketingDialog();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });
                                            } else if (idSts == 2) {
                                                Long sevenDays = TimeUnit.DAYS.toMillis(7);//7days to millisecond
                                                Date date = new Date(idTime + sevenDays);

                                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd HH.mm", Locale.KOREA);
                                                sdf2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                                                String leftDate = sdf2.format(date);

                                                //(new SimpleDateFormat("mm:ss")).format(new Date(time));
                                                builder.setMessage("해당 계정은 현재 비활성화 상태이며,\n" + "[" + leftDate + "] 에 삭제될 예정입니다.\n로그인 시 계정이 다시 활성화됩니다.\n계정을 활성화 하시겠습니까?");
                                                builder.setPositiveButton("활성화", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        new FunctionEvents().cancelDisabledAccount()
                                                                .addOnCompleteListener(new OnCompleteListener<Map<Object, Object>>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Map<Object, Object>> task) {
                                                                        dialog.dismiss();
                                                                        if (task.isSuccessful()) {
                                                                            Boolean isSuccess = (Boolean) task.getResult().get("success");
                                                                            if (!isSuccess) {
                                                                                String message = (String) task.getResult().get("message");
                                                                                if (!MainActivity.this.isFinishing()) {
                                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                                                    builder.setMessage(message);
                                                                                    builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    });
                                                                                    builder.create().show();
                                                                                    Timber.e("cancelDisabledAccount fail: " + task.getException().getMessage());

                                                                                }
                                                                            }

                                                                        } else {
                                                                            if (!MainActivity.this.isFinishing()) {
                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                                                builder.setMessage(getString(R.string.dialog_function_error));
                                                                                builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        dialog.dismiss();
                                                                                    }
                                                                                });
                                                                                builder.create().show();
                                                                                Timber.e("cancelDisabledAccount fail: " + task.getException().getMessage());
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                });
                                                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                });
                                                builder.setCancelable(false);
                                                builder.create().show();

                                            }

                                        }
                                    }

                                }
                            });

                }
            }
        };
    }


    public void initMarketingDialog() {
        builder.setTitle(getString(R.string.dialog_title_marketing));
        builder.setMessage(getString(R.string.dialog_content_marketing));
        builder.setPositiveButton(getString(R.string.dialog_positive_agree), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirestoreQuerys.INSTANCE.getUserUsr(firestore, mAuth.getCurrentUser().getUid())
                        .update("jdg.mkt.agree", true, "jdg.mkt.at", System.currentTimeMillis());
            }
        });
        builder.setNegativeButton(getString(R.string.dialog_positive_Notagree), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirestoreQuerys.INSTANCE.getUserUsr(firestore, mAuth.getCurrentUser().getUid())
                        .update("jdg.mkt.agree", false, "jdg.mkt.at", System.currentTimeMillis())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                            }
                        });

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNegative.setTextColor(Color.parseColor("#C5C5C5"));
    }

    public void getDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Timber.e("getLink" + deepLink);
                            String segment = deepLink.getLastPathSegment();
                            switch (segment) {
                                case "action":
                                    String linkData = deepLink.getQueryParameter("continueUrl");
                                    String email = linkData.substring(linkData.indexOf("?") + 1, linkData.lastIndexOf("?"));
                                    String kind = linkData.substring(linkData.lastIndexOf("?") + 1);
                                    //String email = linkData.substring(linkData.indexOf("?") + 1, linkData.length());
                                    //String kind = String.valueOf(email.charAt(0));
                                    //email = email.substring(1);
                                    //if (kind.equals("1")) {//회원가입

                                    Timber.e("linkData = " + linkData + " email = " + email + " kind = " + kind);
                                    if(kind.equals("findPw")){
                                        mAuth.signOut();
                                    }

                                    Intent intent = new Intent(MainActivity.this, AuthPwSignUpActivity.class);

                                    intent.putExtra("link", deepLink.toString());
                                    intent.putExtra("email", email);
                                    intent.putExtra("kind", kind);// kind = findPw,signUp
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                    //} else if (kind.equals("2")) {//이메일비번 재설정
                                    //}
                                    break;
                                case "share":
                                    String campId = deepLink.getQueryParameter("campId");
                                    String time = deepLink.getQueryParameter("ts");
                                    FirestoreQuerys.INSTANCE.getCampExeSingle(firestore, campId)
                                            .get()
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    CampExe campExe = task.getResult().toObject(CampExe.class);
                                                    if (campExe != null) {
                                                        Timber.e("campExe != null 아님");
                                                        campExe.setId(task.getResult().getId());
                                                        if (campExe.getSts() == 1 && campExe.getStsDtl() == 1) {
                                                            Intent startDetail = new Intent(MainActivity.this, CampaignDetailActivity.class);
                                                            startDetail.putExtra("campexe", campExe);
                                                            startActivity(startDetail);
                                                        } else {
                                                            builder.setMessage("집행이 종료되었거나 일시적으로 참가할 수 없는 캠페인 입니다.");
                                                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            }).create().show();

                                                        }
                                                    } else {
                                                        Timber.e("campExe != null 이다");
                                                        Long time1 = Long.valueOf(time);
                                                        Timber.e("time1= "+time1);
                                                        Date date = new Date(time1);
                                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);
                                                        sdf2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                                                        String nowDate = sdf2.format(date);
                                                        builder.setMessage("[" + nowDate + "]시작 예정 캠페인 입니다");
                                                        builder.setPositiveButton(R.string.dialog_positive_check, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        }).create().show();
                                                    }

                                                } else {
                                                    builder.setMessage(R.string.dialog_function_error);
                                                    builder.setPositiveButton(R.string.dialog_positive_check, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    }).create().show();
                                                }
                                            });
                                    break;
                            }

                        }
                    }
                })
                .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }


    public void initToolbar() {
        //appBarLayout.setOutlineProvider(null);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mainToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeMainProfile:
                        if (mAuth.getCurrentUser() != null) {
                            gotoActivity(ProfileMainActivity.class);
                        } else {
                            builder.setMessage(getString(R.string.dialog_title_needLogin));
                            builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    gotoActivity(AuthMainActivity.class);
                                }
                            }).setNegativeButton(getString(R.string.dialog_negative_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();

                        }
                        return true;

                    case R.id.campaignMainProfile:
                        if (mAuth.getCurrentUser() != null) {
                            gotoActivity(ProfileMainActivity.class);
                        } else {
                            builder.setMessage(getString(R.string.dialog_title_needLogin));
                            builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    gotoActivity(AuthMainActivity.class);
                                }
                            }).setNegativeButton(getString(R.string.dialog_negative_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();

                        }
                        return true;

                    case R.id.campaignMainListChange:
                        if (campViewcheck) {
                            item.setIcon(R.drawable.ic_list_view_large);
                            campViewcheck = false;
                            SharedPreferencesHelper.put(MainActivity.this, "listChange", false);
                            EventBus.get().post(new Events.ListChange(false));
                        } else {
                            item.setIcon(R.drawable.ic_list_view_small);
                            campViewcheck = true;
                            SharedPreferencesHelper.put(MainActivity.this, "listChange", true);
                            EventBus.get().post(new Events.ListChange(true));
                        }
                        return true;

//                    case R.id.campaignMainAlarm:
//                        if (mAuth.getCurrentUser() != null) {
//                            gotoActivity(AlarmActivity.class);
//                        } else {
//                            builder.setMessage(getString(R.string.dialog_title_needLogin));
//                            builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    gotoActivity(AuthMainActivity.class);
//                                }
//                            }).setNegativeButton(getString(R.string.dialog_negative_cancel), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            }).create().show();
//                        }
//                        return true;

                    case R.id.myinfoProfile:
                        if (mAuth.getCurrentUser() != null) {
                            gotoActivity(ProfileMainActivity.class);
                        } else {
                            builder.setMessage(getString(R.string.dialog_title_needLogin));
                            builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    gotoActivity(AuthMainActivity.class);
                                }
                            }).setNegativeButton(getString(R.string.dialog_negative_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                            //gotoActivity(AuthMainActivity.class);
                        }
                        return true;

//                    case R.id.myinfoAlarm:
//                        if (mAuth.getCurrentUser() != null) {
//                            gotoActivity(AlarmActivity.class);
//                        } else {
//                            builder.setMessage(getString(R.string.dialog_title_needLogin));
//                            builder.setPositiveButton(getString(R.string.dialog_positive_check), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    gotoActivity(AuthMainActivity.class);
//                                }
//                            }).setNegativeButton(getString(R.string.dialog_negative_cancel), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            }).create().show();
//                        }
//                        return true;
                }
                return false;
            }
        });
    }

    public void gotoActivity(Class c) {
        startActivity(new Intent(MainActivity.this, c));
    }

    public void initNavigationView() {
        mainViewPager.setAllowedSwipeDirection(CustomViewpager.SwipeDirection.none);
        mainViewPager.setOffscreenPageLimit(3);
        mainFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());

        //fragmentPagerAdapter.instantiateItem(new CampaignMainFragment,2);
        mainViewPager.setAdapter(mainFragmentPagerAdapter);
        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //mainNavi.getMenu().getItem(position).setChecked(true);
                switch (position) {
                    case 0:
                        getSupportActionBar().show();
                        getSupportActionBar().setDisplayShowTitleEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        break;
                    case 1:
                        getSupportActionBar().show();
                        getSupportActionBar().setDisplayShowTitleEnabled(true);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        getSupportActionBar().setTitle(R.string.MainActivity_campaign_toolbarTitle);
                        break;
                    case 2:
                        getSupportActionBar().show();
                        getSupportActionBar().setTitle(R.string.MainActivity_activity_toolbarTitle);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Timber.e("getSelectedItemId ="+mainNavi.getSelectedItemId());

        mainNavi.setOnNavigationItemSelectedListener(item -> {
            itemId = mainNavi.getSelectedItemId();
            switch (item.getItemId()) {
                case R.id.mainNaviHome:
                    mainViewPager.setCurrentItem(0);
                    EventBus.get().post(new Events.ScrollDown());
                    return true;
                case R.id.mainNaviCampaign:
                    if (mainViewPager.getCurrentItem() == 1 && !notUserDialogCheck) {
                        EventBus.get().post(new Events.CampaignScrollUp());
                    }
                    mainViewPager.setCurrentItem(1);
                    notUserDialogCheck = false;
                    return true;
                case R.id.mainNaviMyInfo:
                    if (mAuth.getCurrentUser() != null) {
                        mainViewPager.setCurrentItem(2);
                    } else {
                        item.setCheckable(false);
                        builder.setMessage(R.string.dialog_title_needLogin);
                        builder.setPositiveButton(R.string.dialog_positive_check, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //item.setIntent(new Intent(MainActivity.this, AuthMainActivity.class));
                                notUserDialogCheck = true;
                                gotoActivity(AuthMainActivity.class);
                                mainNavi.setSelectedItemId(itemId);

                            }
                        }).setNegativeButton(R.string.dialog_negative_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                notUserDialogCheck = true;
                                mainNavi.setSelectedItemId(itemId);

                            }
                        }).create().show();

                    }
                    return true;
            }
            return true;
        });

    }

    @Subscribe
    private void GoToActivityPage(Events.HomeToActivityPage event) {
        mainViewPager.setCurrentItem(2);
        mainNavi.setSelectedItemId(R.id.mainNaviMyInfo);
        EventBus.get().post(new Events.ActivityToPoint());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.e("onStart()");
        EventBus.get().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        backPressedSubject.onNext(System.currentTimeMillis()); // 눌린 시간 비교를 위해 현재 시간을 넣는다
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.get().unregister(this);
        try {
            backPressedDisposable.dispose();
        } catch (Exception ignored) {
        }
    }

    public AlertDialog.Builder getBuilder() {
        return builder;
    }
}
