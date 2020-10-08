package com.imhc2.plumboard;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.vimeo.networking.VimeoClient;

import es.dmoral.toasty.Toasty;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by user on 2018-04-03.
 */

public class PlumBoardApp extends MultiDexApplication{

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        //DaggerPlumBoardAppComponent.builder().create(this).inject(this);
        Stetho.initializeWithDefaults(this);
        initVimeoToken();
        initLeakCanary();
        initTimber();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);//svg땜에 있어야함
        MultiDex.install(this);
        RxPaparazzo.register(this);
        Toasty.Config.getInstance().setTextSize(14).apply();
    }

    public static RefWatcher getRefWatcher(Context context){
        PlumBoardApp plumBoardApp = (PlumBoardApp) context.getApplicationContext();
        return plumBoardApp.refWatcher;
    }

    public FirebaseFirestore getFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        return firestore;
    }


    private void initVimeoToken() {
        com.vimeo.networking.Configuration.Builder configBuilder = new com.vimeo.networking.Configuration.Builder(getResources().getString(R.string.vimeo_token));
        configBuilder.setCacheDirectory(this.getCacheDir());
        configBuilder.enableCertPinning(false);
        //configBuilder.setLogLevel(Vimeo.LogLevel.VERBOSE);
        VimeoClient.initialize(configBuilder.build());
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        refWatcher= LeakCanary.install(this);
    }

}
