package com.imhc2.plumboard.mvvm.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.util.SharedPreferencesHelper;
import com.imhc2.plumboard.commons.video.AdaptiveTrackSelection;
import com.vimeo.networking.VimeoClient;
import com.vimeo.networking.callbacks.ModelCallback;
import com.vimeo.networking.model.Picture;
import com.vimeo.networking.model.PictureCollection;
import com.vimeo.networking.model.Video;
import com.vimeo.networking.model.VideoFile;
import com.vimeo.networking.model.error.VimeoError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import static com.imhc2.plumboard.commons.video.AdaptiveTrackSelection.DEFAULT_MAX_INITIAL_BITRATE;

public class ExoPlayerActivity extends AppCompatActivity {

    @BindView(R.id.activity_exo_player_view) PlayerView exoPlayer;
    @BindView(R.id.exo_progress) DefaultTimeBar timeBar;
    @BindView(R.id.exo_controller_FullScreenBtn) ImageView fullScreenBtn;
    @BindView(R.id.exo_loading) ProgressBar exoLoading;
    @BindView(R.id.exo_pause) ImageButton exoPauseBtn;
    @BindView(R.id.exo_play) ImageButton exoPlayBtn;

    //exoPlayer
    SimpleExoPlayer player;
    DefaultTrackSelector trackSelector;
    MappingTrackSelector.MappedTrackInfo mappedTrackinfo;
    //AdaptiveTrackSelection.Factory videoSelectionFactory;
    AdaptiveTrackSelection.Factory videoSelectionFactory;
    AlertDialog.Builder builder;
    TrackGroupArray trackGroups;
    MappingTrackSelector.SelectionOverride override;
    DefaultBandwidthMeter bandwidthMeter;

    int rendererIndex = 0;
    List<String> qualityList = new ArrayList<>(); //360p,720p,...등
    int dialogNum = 0;
    String url;
    Integer nowPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);
        ButterKnife.bind(this);

        Intent intent= getIntent();
        url = intent.getStringExtra("videoUrl");
        nowPage = intent.getIntExtra("nowPage",0);
        vimeoGetVideo(getString(R.string.vimeoAddress)+url);
        checkWifiChangeQuality();
        initTimeBarEvent();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
//        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void initTimeBarEvent() {
//        SharedPreferences pref = getSharedPreferences("videoWatchCheck", MODE_PRIVATE);
//        Boolean videoCheck = pref.getBoolean("videoCheck", false);

        Boolean videoCheck = (Boolean) SharedPreferencesHelper.get(ExoPlayerActivity.this,url+nowPage,false);

        timeBar.addListener(new TimeBar.OnScrubListener() {//timebar event
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {
                if (videoCheck) {
                    timeBar.setEnabled(true);
                } else {
                    timeBar.setEnabled(false);
                    Toasty.normal(getApplicationContext(), getString(R.string.toast_ExoPlayerActivity_cantSkip), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                if (videoCheck) {
                    timeBar.setEnabled(true);
                } else {
                    timeBar.setEnabled(false);
                    Toasty.normal(getApplicationContext(), getString(R.string.toast_ExoPlayerActivity_cantSkip), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);//동영상 시작
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);//동영상 정지
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }


    private void vimeoGetVideo(String url) {
        VimeoClient.getInstance().fetchNetworkContent(url, new ModelCallback<Video>(Video.class) {
            @Override
            public void success(Video video) {
                //Play play = video.getPlay(); 추후 라이브러리 버전 업 후 사용 가능
                //video.
                for (VideoFile file : video.files) {
                    int type = Util.inferContentType(Uri.parse(file.getLink()));
                    switch (type) {
                        case C.TYPE_HLS:
                            playExoStream(file.getLink(), type);
                            break;
                        case C.TYPE_DASH:
                            playExoStream(file.getLink(), type);
                            break;
                    }
                }
                PictureCollection pictures = video.pictures;
                Picture picture = pictures.pictureForWidth(640);
                String adress = picture.link;

            }

            @Override
            public void failure(VimeoError error) {
                Toast.makeText(ExoPlayerActivity.this, "fail", Toast.LENGTH_SHORT).show();
                //Timber.e("error", error.toString());
            }

        });
    }

    private void showListDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_quality);
        builder.setSingleChoiceItems(qualityList.toArray(new String[qualityList.size()]), dialogNum, (dialog, which) -> {
            dialogNum = which;
            Log.e("DialogCount", "" + which);
            trackSelect(which);
        });
        builder.setPositiveButton(R.string.dialog_positive_check, (dialog, which) -> {
            overrideTrackSelection();
            player.setPlayWhenReady(true);
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void checkWifiChangeQuality() {
        Boolean check = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    //Toast.makeText(this, "3G/LTE 등으로 재생 시 데이터 사용료가 발생할 수 있습니다", Toast.LENGTH_SHORT).show();
                    check = true;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    //Toasty.normal(this, getString(R.string.toast_ExoplayerActivity_dataUse), Toast.LENGTH_SHORT).show();
                    check = false;
                    break;
            }
        }
        if (check) {
            DEFAULT_MAX_INITIAL_BITRATE = Integer.MAX_VALUE;
            dialogNum = 3;
        } else {
            DEFAULT_MAX_INITIAL_BITRATE = 800000;
            dialogNum = 0;
        }
    }

    private void trackSelect(int num) {
        //isDisabled =trackSelector.getRendererDisabled(rendererIndex);
        override = new MappingTrackSelector.SelectionOverride(videoSelectionFactory, 0, num);
    }

    private void overrideTrackSelection() {
        //trackSelector.setRendererDisabled(rendererIndex,isDisabled);
        if (override != null) {
            trackSelector.setSelectionOverride(rendererIndex, mappedTrackinfo.getTrackGroups(0), override);
        } else {
            trackSelector.clearSelectionOverrides(rendererIndex);
        }
    }

    //스트리밍 셋팅
    private void playExoStream(@NonNull String url, int type) {
        initExoPlayer();
        DataSource.Factory DataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "test"), bandwidthMeter);
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "test"), bandwidthMeter);
        MediaSource mediaSource = null;
        switch (type) {
            case C.TYPE_HLS:
                mediaSource = new HlsMediaSource.Factory(DataSourceFactory).createMediaSource(Uri.parse(url), null, null);
                break;
            case C.TYPE_DASH:
                mediaSource = new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(DataSourceFactory), mediaDataSourceFactory).createMediaSource(Uri.parse(url), null, null);
                break;
        }
        if (mediaSource != null) {
            player.prepare(mediaSource);
            player.setPlayWhenReady(true);//시작시 자동재생
        }
    }

    private void initExoPlayer() {
        //link : https://github.com/google/ExoPlayer
        bandwidthMeter = new DefaultBandwidthMeter();//네트워크 용량 측정을 위한 것
        videoSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        exoPlayer.setPlayer(player);
        //exoplayer debug
//        DebugTextViewHelper debugTextViewHelper = new DebugTextViewHelper(player, mainNum);
//        debugTextViewHelper.start();
        //event
        player.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
/*                switch (playbackState){
                    case Player.STATE_BUFFERING:
                        exoLoading.setVisibility(View.VISIBLE);
                        exoPauseBtn.setVisibility(View.INVISIBLE);
                        exoPlayBtn.setVisibility(View.INVISIBLE);
                        break;
                    case Player.STATE_READY:
                        exoLoading.setVisibility(View.INVISIBLE);
                        break;

                    case Player.STATE_ENDED:
                        Log.e("STATE_ENDED@@","@@");
                        videoWatchCheck=true;
                        break;
                }*/
                if (playbackState == Player.STATE_ENDED) {
                    //동영상 시청완료

                    SharedPreferencesHelper.put(ExoPlayerActivity.this,url+nowPage,true);

                    finish();
//                    List<String> data = new ArrayList<>();
//                    data.add("true");
                    //RxEventBus.getInstance().sendEvent(data);
                    EventBus.get().post(new Events.CardResult("true"));

                } else if (playbackState == Player.STATE_BUFFERING) {
                    exoLoading.setVisibility(View.VISIBLE);
                    exoPauseBtn.setVisibility(View.INVISIBLE);
                    exoPlayBtn.setVisibility(View.INVISIBLE);
                } else {
                    exoLoading.setVisibility(View.INVISIBLE);
                }

            }

        });

    }

    @OnClick({R.id.exo_controller_QualityBtn, R.id.exo_controller_FullScreenBtn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.exo_controller_QualityBtn:
                mappedTrackinfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackinfo != null) {
                    for (int i = 0; i < mappedTrackinfo.length; i++) {
                        trackGroups = mappedTrackinfo.getTrackGroups(i);
                        Log.e("trackGroups", mappedTrackinfo.getTrackGroups(i).toString());
                        int rendererType = player.getRendererType(i);
                        if (trackGroups.length != 0 && rendererType == C.TRACK_TYPE_VIDEO) {
                            rendererIndex = i;
                            Log.e("rendererIndex", rendererIndex + "");
                            for (int j = 0; j < trackGroups.length; j++) {//보통 0이다
                                TrackGroup trackGroup = trackGroups.get(j);
                                Log.e("trackGroup", trackGroups.get(j).toString());
                                qualityList.clear();
                                for (int k = 0; k < trackGroup.length; k++) {
                                    Format format = trackGroup.getFormat(k);
                                    Log.e("format", format.toString());
                                    qualityList.add(trackGroup.getFormat(k).height + "p");
                                }
                            }
                        }
                    }
                } else {
                    Log.e("trackSelecotor", "null........");
                }
                showListDialog();
                player.setPlayWhenReady(false);
                break;

            case R.id.exo_controller_FullScreenBtn:
                if (player.getPlayWhenReady()) {//풀레이중인지 확인
                    player.setPlayWhenReady(true);
                }
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fullScreenBtn.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.exo_controls_fullscreen_enter));
                } else if (orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    fullScreenBtn.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.exo_controls_fullscreen_exit));
                }
                break;
        }

    }



    @Override
    public void onBackPressed() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            super.onBackPressed();
        }

    }
}
