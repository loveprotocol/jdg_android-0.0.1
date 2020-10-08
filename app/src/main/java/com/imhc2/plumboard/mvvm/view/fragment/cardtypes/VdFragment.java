package com.imhc2.plumboard.mvvm.view.fragment.cardtypes;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Util;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.util.CustomImageView169;
import com.imhc2.plumboard.mvvm.model.domain.Vd;
import com.imhc2.plumboard.mvvm.view.activity.CardMainActivity;
import com.imhc2.plumboard.mvvm.view.activity.ExoPlayerActivity;
import com.vimeo.networking.VimeoClient;
import com.vimeo.networking.callbacks.ModelCallback;
import com.vimeo.networking.model.Picture;
import com.vimeo.networking.model.PictureCollection;
import com.vimeo.networking.model.Video;
import com.vimeo.networking.model.VideoFile;
import com.vimeo.networking.model.error.VimeoError;
import com.vimeo.networking.model.playback.Play;

import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class VdFragment extends Fragment {

    private static final String VD = "vd";
    Unbinder unbinder;
    @Nullable @BindView(R.id.fragment_type_vd_image) CustomImageView169 vdImageView;
    @BindView(R.id.fragment_type_vd_title) TextView mTitle;
    @BindView(R.id.fragment_type_vd_content) TextView mContent;
    private int now;
    private Vd vd;
    View view;

    public VdFragment() {
        // Required empty public constructor
    }

    public static VdFragment newInstance(Vd vd) {
        VdFragment fragment = new VdFragment();
        Bundle args = new Bundle();
        args.putSerializable(VD, vd);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vd = (Vd) getArguments().getSerializable(VD);
            if (vd.getOrd()==0&&!vd.getReq()) {
                EventBus.get().post(new Events.CardResult(""));
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_type_vd, container, false);
        unbinder = ButterKnife.bind(this, view);
        mTitle.setText(vd.getTtl());
        if (vd.getDescA()) {
            mContent.setText(vd.getDesc());
        }
//                if (((MainActivity) getActivity()).getViewpager().getCurrentItem() == 3) {
//                    RxEventBus.getInstance().sendEvent(true);
//                }
        String url = vd.getVP();
        VimeoClient.getInstance().fetchNetworkContent(url, new ModelCallback<Video>(Video.class) {
            @Override
            public void success(Video video) {
                Play play = video.getPlay();
                //video.
                for (VideoFile file : video.files) {
                    int type = Util.inferContentType(Uri.parse(file.getLink()));
                    switch (type) {
                        case C.TYPE_HLS:
                            break;
                        case C.TYPE_DASH:
                            break;
                        case C.TYPE_OTHER:
                            break;
                        case C.TYPE_SS:
                            break;
                    }
                }
                PictureCollection pictures = video.pictures;
                Picture picture = pictures.pictureForWidth(1080);

                if (vdImageView != null) {
                    Glide.with(getActivity()).load(picture.link).thumbnail(0.1f).apply(new RequestOptions().placeholder(R.drawable.ic_campaign_empty).optionalCenterCrop()).into(vdImageView);
                    //Glide.with(getActivity()).applyDefaultRequestOptions(new RequestOptions().override(1280,720)).load(picture.link).into(vdImageView);
                }
            }

            @Override
            public void failure(VimeoError error) {
                Log.e("error", error.toString());
            }

        });

        vdImageView.setOnClickListener(v -> {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        //Toast.makeText(this, "3G/LTE 등으로 재생 시 데이터 사용료가 발생할 수 있습니다", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), ExoPlayerActivity.class);
                        intent.putExtra("videoUrl", vd.getVP());
                        intent.putExtra("nowPage", now);
                        Timber.e("vd nowPage = " + now);
                        startActivity(intent);
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(getString(R.string.dialog_ExoplayerActivity_dataUse));
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getActivity(), ExoPlayerActivity.class);
                                intent.putExtra("videoUrl", vd.getVP());
                                intent.putExtra("nowPage", now);
                                Timber.e("vd nowPage = " + now);
                                startActivity(intent);
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                        break;
                }
            }


        });
        return view;
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            if (((CardMainActivity) getActivity()) != null) {
                now = ((CardMainActivity) getActivity()).getCardMainViewpager().getCurrentItem();
                Timber.e("현재페이지 = "+now);
            }

            try {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (Exception e) {

            }
            if (vd != null) {
                if (!vd.getReq()) {
                    //RxEventBus.getInstance().sendEvent("");
                    EventBus.get().post(new Events.CardResult(""));
                }
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
