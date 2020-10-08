package com.imhc2.plumboard.mvvm.view.fragment.activityfragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;


public class ActivityGraphChildFragment extends Fragment {
    private static final String TITLE = "title";
    private static final String FIRST = "first";
    private static final String SECOND = "second";
    @BindView(R.id.fragment_activity_graph_child_title) TextView mTitle;
    @BindView(R.id.fragment_activity_graph_child_progress_in) CircularProgressBar mProgressIn;
    @BindView(R.id.fragment_activity_graph_child_progress_save_tv) TextView mProgressSaveTv;
    @BindView(R.id.fragment_activity_graph_child_progress_plus_tv) TextView mProgressPlusTv;
    @BindView(R.id.fragment_activity_graph_child_progress_total_tv) TextView mProgressTotalTv;
    @BindView(R.id.fragment_activity_graph_child_progress_img1) ImageView mImg1;
    @BindView(R.id.fragment_activity_graph_child_progress_img2) ImageView mImg2;
    @BindView(R.id.fragment_activity_graph_child_progress_img3) ImageView mImg3;
    @BindView(R.id.fragment_activity_graph_child_name2) TextView mNameTv2;
    @BindView(R.id.fragment_activity_graph_child_name3) TextView mNameTv3;
    Unbinder unbinder;

    private String title;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    public ActivityGraphChildFragment() {
        // Required empty public constructor
    }

    public static ActivityGraphChildFragment newInstance(String param1) {
        ActivityGraphChildFragment fragment = new ActivityGraphChildFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity_graph_child, container, false);
        unbinder = ButterKnife.bind(this, view);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData(); //mytodo : 디비 업데이트 후 풀기
    }

    private void initData() {
        mTitle.setText(title);
        if (title.equals(getString(R.string.ActivityGraphChildFragment_today))) {
            getChildTodayData(getString(R.string.ActivityGraphChildFragment_today));
            mImg1.setBackgroundColor(getResources().getColor(R.color.colorChildGraphSave));
            mImg2.setBackgroundColor(getResources().getColor(R.color.colorChildGraphPlus));
            mImg3.setBackgroundColor(getResources().getColor(R.color.colorPlumBoard));
            mProgressIn.setBackgroundColor(getResources().getColor(R.color.colorChildGraphPlus));

            mNameTv2.setText(R.string.ActivityGraphChildFragment_add);
            mNameTv3.setText(R.string.ActivityGraphChildFragment_total);

        } else if (title.equals(getString(R.string.ActivityGraphChildFragment_cumulative))) {
            getChildTotalData(getString(R.string.ActivityGraphChildFragment_cumulative));
            mImg1.setBackgroundColor(getResources().getColor(R.color.colorPlumBoard));
            mImg2.setBackgroundColor(getResources().getColor(R.color.colorChildGraphUse));
            mImg3.setBackgroundColor(getResources().getColor(R.color.colorChildGraphSave));

            mProgressIn.setBackgroundColor(getResources().getColor(R.color.colorChildGraphUse));

            mNameTv2.setText(R.string.ActivityGraphChildFragment_use);
            mNameTv3.setText(R.string.ActivityGraphChildFragment_now);

        }
    }


    private void initView(int first, int second, int kind, String what) {

        NumberFormat nf = NumberFormat.getInstance();
        mProgressSaveTv.setText(nf.format(first));
        mProgressPlusTv.setText(nf.format(second));
        if (what.equals(getString(R.string.ActivityGraphChildFragment_today))) {
            mProgressIn.setProgressMax(kind);
            mProgressIn.setProgress(first);
            mProgressTotalTv.setText(nf.format(kind));
        } else if (what.equals(getString(R.string.ActivityGraphChildFragment_cumulative))) {
//            mProgressIn.setProgressMax(second + (first + second));
//            mProgressIn.setProgress((first + second));
//            mProgressTotalTv.setText(nf.format(kind));
            mProgressIn.setProgressMax(first);
            mProgressIn.setProgress(kind);
            mProgressTotalTv.setText(nf.format(kind));
        }
    }

    private void getChildTotalData(String what) {
        try {
            FirestoreQuerys.INSTANCE.getUserCrd(firestore, mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    Double total =
                                            Double.parseDouble(task.getResult().get("jdg.pt.tutPt.acq").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.aPt.acq").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.pPt.acq").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.iPtP.acq").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.iPtA.acq").toString());
                                    Double use =
                                            Double.parseDouble(task.getResult().get("jdg.pt.tutPt.usg").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.tutPt.wdl").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.tutPt.pnd").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.aPt.usg").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.aPt.wdl").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.aPt.pnd").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.pPt.usg").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.pPt.wdl").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.pPt.pnd").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.iPtP.usg").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.iPtP.wdl").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.iPtP.pnd").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.iPtA.usg").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.iPtA.wdl").toString()) +
                                                    Double.parseDouble(task.getResult().get("jdg.pt.iPtA.pnd").toString());
                                    Double left =
                                            Double.parseDouble(task.getResult().get("jdg.pt.crtTot").toString());

                                    initView(total.intValue(), use.intValue(), left.intValue(), what);
                                }
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }


    private void getChildTodayData(String what) {
        String day = null;
        try {
            day = convertDate(System.currentTimeMillis(), "yyyyMMdd");

            FirestoreQuerys.INSTANCE.getUserCrdDaily(firestore, mAuth.getCurrentUser().getUid(), day)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    Double tutPt = Double.parseDouble(task.getResult().get("jdg.pt.tutPt.acq").toString());
                                    Double acq1 = Double.parseDouble(task.getResult().get("jdg.pt.aPt.acq").toString());
                                    Double acq2 = Double.parseDouble(task.getResult().get("jdg.pt.pPt.acq").toString());

                                    Double acqP1 = Double.parseDouble(task.getResult().get("jdg.pt.iPtP.acq").toString());
                                    Double acqP2 = Double.parseDouble(task.getResult().get("jdg.pt.iPtA.acq").toString());

                                    Double acqTotal1 = Double.parseDouble(task.getResult().get("jdg.pt.tutPt.acq").toString());
                                    Double acqTotal2 = Double.parseDouble(task.getResult().get("jdg.pt.aPt.acq").toString());
                                    Double acqTotal3 = Double.parseDouble(task.getResult().get("jdg.pt.pPt.acq").toString());
                                    Double acqTotal4 = Double.parseDouble(task.getResult().get("jdg.pt.iPtP.acq").toString());
                                    Double acqTotal5 = Double.parseDouble(task.getResult().get("jdg.pt.iPtA.acq").toString());


                                    Double save = acq1 + tutPt + acq2;
                                    int first = save.intValue();
                                    Double plus = acqP1 + acqP2;
                                    int second = plus.intValue();

                                    Double last = acqTotal1 + acqTotal2 + acqTotal3 + acqTotal4 + acqTotal5;

                                    initView(first, second, last.intValue(), what);
                                }
                            }

                        }
                    });
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public String convertDate(Long dateInMilliseconds, String dateFormat) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.KOREA);
        String dateString = formatter.format(new Date(dateInMilliseconds));
        return dateString;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
