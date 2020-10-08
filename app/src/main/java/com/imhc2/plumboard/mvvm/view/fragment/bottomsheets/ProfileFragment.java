package com.imhc2.plumboard.mvvm.view.fragment.bottomsheets;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.mvvm.view.activity.mypage.MyProfileActivity;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.Options;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends BottomSheetDialogFragment {

    Unbinder unbinder;
    Options options;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    private String basicImg ="https://firebasestorage.googleapis.com/v0/b/plumboard-aacb2.appspot.com/o/tImg%2Fdefault%2Fdefault.png?alt=media&token=f139835d-5f55-48e4-a3c7-9367a08073f4";
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        options = new Options();
        firestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        //options.setToolbarColor(getResources().getColor(R.color.colorPlumBoard));
        options.setAspectRatio(25, 25);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.fragment_profile_camera_ll, R.id.fragment_profile_camera_gallery, R.id.fragment_profile_human_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_profile_camera_ll:
                RxPaparazzo
                        .single(getActivity())
                        .crop(options)
                        .usingCamera()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response.resultCode() != RESULT_OK) {//오류시
                                //response.targetUI().showUserCanceled();
                            } else {
                                //response.targetUI().loadImage(response.data());
                                String userImagePath = response.data().getFile().getAbsolutePath();
                                EventBus.post(new Events.Gallery(userImagePath));
                                //Log.e("data",response.data().toString());
                                Timber.e("camera:userImagePath : "+userImagePath);
                            }
                        });

                dismiss();
                break;
            case R.id.fragment_profile_camera_gallery:
                RxPaparazzo
                        .single(getActivity())
                        .crop(options)
                        .usingGallery()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            // See response.resultCode() doc
                            if (response.resultCode() != RESULT_OK) {
                                //response.targetUI().showUserCanceled();
                                return;
                            } else {
                                String userImagePath = response.data().getFile().getAbsolutePath();
                                EventBus.post(new Events.Gallery(userImagePath));
                                //Glide.with(this).load(userImagePath).into(boardUploadImage1);
                                Timber.e("gallery:userImagePath : "+userImagePath);
                            }
                        });
                    dismiss();
                break;
            case R.id.fragment_profile_human_ll:
                FirestoreQuerys.INSTANCE.updateMyImage(FirestoreQuerys.INSTANCE.getUserUsr(firestore, mAuth.getCurrentUser().getUid()),basicImg)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            EventBus.get().post(new Events.Gallery(null));
                            startActivity(new Intent(getActivity(),MyProfileActivity.class));
                            dismiss();
                        }
                    }
                });

                break;
        }
    }
}
