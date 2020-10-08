package com.imhc2.plumboard.mvvm.view.activity.mypage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.eventbus.Subscribe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.mvvm.view.fragment.bottomsheets.ProfileFragment;
import com.imhc2.plumboard.mvvm.view.fragment.dialog.ProgressDialogHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class MyProfileActivity extends AppCompatActivity {

    @BindView(R.id.activity_my_profile_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_my_profile_myImg) ImageView myImgview;
    @BindView(R.id.activity_my_profile_nickName) TextView nickName;
    @BindView(R.id.activity_my_profile_et) EditText mEt;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    String myNickName, myImg;
    String galleryUrl = null;
    ProgressDialogHelper progressDialogHelper;
    private String basicImg ="https://firebasestorage.googleapis.com/v0/b/plumboard-aacb2.appspot.com/o/tImg%2Fdefault%2Fdefault.png?alt=media&token=f139835d-5f55-48e4-a3c7-9367a08073f4";
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.get().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressDialogHelper.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.get().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        initToolbar();
        profileImgCheck();
        progressDialogHelper = new ProgressDialogHelper(MyProfileActivity.this);
    }

    @Subscribe
    public void getEvent(Events.Gallery gallery) {
        galleryUrl = gallery.getImgUrl();
        if(galleryUrl ==null){
            Glide.with(MyProfileActivity.this).load(basicImg).thumbnail(0.1f).apply(new RequestOptions().override(960, 540).circleCrop()).into(myImgview);
        }else{
            Glide.with(MyProfileActivity.this).load(galleryUrl).thumbnail(0.1f).apply(new RequestOptions().override(960, 540).circleCrop()).into(myImgview);
        }
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_x);
    }

    private void profileImgCheck() {

        FirestoreQuerys.INSTANCE.getUserUsr(firestore, mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                myNickName = (String) task.getResult().get(FieldPath.of("jdg", "nm"));
                                myImg = (String) task.getResult().get(FieldPath.of("jdg", "tImg"));
                            }

                            if (myNickName == null) {
                                nickName.setText(mAuth.getCurrentUser().getEmail());
                                mEt.setText(mAuth.getCurrentUser().getEmail());
                            } else {
                                nickName.setText(myNickName);
                                mEt.setText(myNickName);
                            }

                            if (myImg == null) {
                                Glide.with(MyProfileActivity.this).load(basicImg).thumbnail(0.1f).into(myImgview);
                            } else {
                                Glide.with(MyProfileActivity.this).load(myImg).thumbnail(0.1f).apply(new RequestOptions().override(960, 540).circleCrop()).into(myImgview);
                            }

                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.myprofile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.myprofile_check) {
            if (!mEt.getText().toString().equals("")) {
                progressDialogHelper.show();
                if (galleryUrl != null) {
                    upLoad(galleryUrl);
                } else {
                    FirestoreQuerys.INSTANCE.updateNickName(FirestoreQuerys.INSTANCE.getUserUsr(firestore, mAuth.getCurrentUser().getUid()), mEt.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialogHelper.dismiss();
                                        finish();
                                    } else {
                                        Toasty.normal(MyProfileActivity.this, getString(R.string.toast_MyProfileActivity_uploadFail), Toast.LENGTH_SHORT).show();
                                        progressDialogHelper.dismiss();
                                    }
                                }
                            });
                }

            } else {
                Toasty.normal(this, getString(R.string.toast_MyProfileActivity_inputNickName), Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_up , R.anim.slide_to_down);
    }

    private void upLoad(String uri) {
        StorageReference storageReference = storage.getReferenceFromUrl(getString(R.string.storage_address));
        Uri file = Uri.fromFile(new File(uri));
        StorageReference riversRef = storageReference.child("tImg/jdg/" + mAuth.getCurrentUser().getUid() + "/" + mAuth.getCurrentUser().getUid()+".jpg");
        UploadTask uploadTask = riversRef.putFile(file);


        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult();

                    File f = new File(uri);
                    if (f.exists()) {
                        f.delete();
                    }
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));

                    FirestoreQuerys.INSTANCE.updateNickNameImage(FirestoreQuerys.INSTANCE.getUserUsr(firestore, mAuth.getCurrentUser().getUid()), mEt.getText().toString(), downloadUrl.toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialogHelper.dismiss();
                                    if (task.isSuccessful()) {
//                                        Intent intent = new Intent(MyProfileActivity.this, ProfileMainActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        gotoActivity(intent);
                                        progressDialogHelper.dismiss();
                                        finish();
                                    } else {
                                        Toasty.normal(MyProfileActivity.this, getString(R.string.toast_MyProfileActivity_uploadFail), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    progressDialogHelper.dismiss();
                    Toasty.normal(MyProfileActivity.this, getString(R.string.toast_MyProfileActivity_uploadFail), Toast.LENGTH_SHORT).show();
                }
            }
        });


//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Timber.e("uploadTask = " + e.getMessage());
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Uri downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().getResult();
//
//
//
//            }
//        });

    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.activity_my_profile_myImg)
    public void onViewClicked() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEachCombined(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)
                .subscribe(permission -> {
                    if (permission.granted) {//모든 권한 수락
                        Fragment f1 =getSupportFragmentManager().findFragmentByTag("myProfile");
                        if(f1 ==null){
                            ProfileFragment profileFragment = new ProfileFragment();
                            profileFragment.show(getSupportFragmentManager(), "myProfile");
                        }


                    } else if (permission.shouldShowRequestPermissionRationale) {
                        //권한거부
                        Toasty.normal(this, getString(R.string.toast_MyProfileActivity_checkPermission), Toast.LENGTH_SHORT).show();
                    } else {
                        //권한 다시보지 않기 체크 시
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
    }
}
