package com.imhc2.plumboard.mvvm.view.fragment.activityfragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.imhc2.plumboard.PlumBoardApp;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;
import com.imhc2.plumboard.commons.util.WeeksGetMillisecondUtil;
import com.imhc2.plumboard.mvvm.model.domain.Cnstv;
import com.imhc2.plumboard.mvvm.model.domain.CnstvUser;
import com.imhc2.plumboard.mvvm.model.domain.Evnt;
import com.imhc2.plumboard.mvvm.model.domain.Rcmd;
import com.imhc2.plumboard.mvvm.model.domain.RcmdUser;
import com.imhc2.plumboard.mvvm.view.activity.mypage.PlumBoostActivity;
import com.rd.PageIndicatorView;
import com.squareup.leakcanary.RefWatcher;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import timber.log.Timber;

public class ActivityGraphFragment extends Fragment {

    Unbinder unbinder;

    @BindView(R.id.fragment_activity_graph_viewpager) ViewPager viewpager;
    @BindView(R.id.fragment_activity_graph_indicatorView) PageIndicatorView indicatorView;
    @BindView(R.id.fragment_activity_columnChartView) ColumnChartView chartView;
    @BindView(R.id.fragment_activity_graph_avg_point_tv) TextView fragmentActivityGraphAvgPointTv;
    @BindView(R.id.fragment_activity_graph_campaign_tv) TextView fragmentActivityGraphCampaignTv;
    @BindView(R.id.fragment_activity_graph_avg_rating_tv) TextView fragmentActivityGraphAvgRatingTv;
    @BindView(R.id.fragment_activity_graph_time_tv) TextView fragmentActivityGraphTimeTv;
    @BindView(R.id.fragment_activity_graph_boost_img_ll) LinearLayout boostImgLL;
    @BindView(R.id.fragment_activity_graph_boost_progress) ProgressBar mProgress;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    Map<Integer, Double> totalPoint = new HashMap<Integer, Double>() {{
        put(1, 0d);
        put(2, 0d);
        put(3, 0d);
        put(4, 0d);
        put(5, 0d);
        put(6, 0d);
        put(7, 0d);
    }};

    CnstvUser cnstvUser = null;
    Cnstv cnstv = null;
    Rcmd rcmd = null;
    RcmdUser rcmdUser = null;
    Evnt evnt = null;

    public ActivityGraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity_graph, container, false);
        unbinder = ButterKnife.bind(this, view);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            getMaxProgress();
            getData();
            initViewPager();
            getBoostData();
        }

        boostImgLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlumBoostActivity.class);
                startActivity(intent);
            }

        });

        return view;
    }


    private void getBoostData() {

        FirestoreQuerys.INSTANCE.getEventUserCnstv(firestore, mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cnstvUser = task.getResult().toObject(CnstvUser.class);
                        FirestoreQuerys.INSTANCE.getEventCnstv(firestore)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        cnstv = task1.getResult().toObject(Cnstv.class);
                                    }
                                });
                    }
                });

        FirestoreQuerys.INSTANCE.getEventRcmd(firestore)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        rcmd = task.getResult().toObject(Rcmd.class);
                        FirestoreQuerys.INSTANCE.getEventUserRcmd(firestore, mAuth.getCurrentUser().getUid())
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        rcmdUser = task1.getResult().toObject(RcmdUser.class);
                                    }
                                });
                    }
                });

    }

    private void getMaxProgress() {
        FirestoreQuerys.INSTANCE.getUserCrd(firestore, mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Double totalProgress = 0d;
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                totalProgress = Double.parseDouble(task.getResult().get(FieldPath.of("jdg", "pt", "iPct")).toString());
                                mProgress.setProgress((int) (totalProgress * 100));

                                Timber.e("currentProgress=" + totalProgress);
                            }
                        }
                    }
                });


//        totalProgress += (Long) snapshot.get(FieldPath.of("jdg", "pt", "iPct"));
//        mProgress.setProgress((int) (totalProgress * 100));

//        FirestoreQuerys.INSTANCE.getEventCnstv(firestore)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            if (task.getResult().exists()) {
//                                Cnstv cnstv = task.getResult().toObject(Cnstv.class);
//
//                                maxProgress = Double.parseDouble(cnstv.getMRate().toString());
//
//                                FirestoreQuerys.INSTANCE.getEventRcmd(firestore)
//                                        .get()
//                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                if (task.isSuccessful()) {
//                                                    if (task.getResult().exists()) {
//                                                        Rcmd rcmd = task.getResult().toObject(Rcmd.class);
//
//                                                        maxProgress += Double.parseDouble(rcmd.getMRate().toString());
//                                                        mProgress.setMax((int) (maxProgress * 100));
//                                                        Timber.e("progress max" + (int) (maxProgress * 100));
//                                                    }
//                                                }
//                                            }
//                                        });
//
//                            }
//                        }
//                    }
//                });

        FirestoreQuerys.INSTANCE.getRate(firestore)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Double rate = 0D;
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                if (snapshot.exists()) {
                                    if (snapshot.get("mRate") != null) {
                                        rate += Double.parseDouble(snapshot.get("mRate").toString());
                                    }
                                }
                            }
                            mProgress.setMax((int) (rate * 100));
                            Timber.e("maxProgress=" + rate);
                        } else {

                        }
                    }
                });
    }

    //    int seconds = (int) (milliseconds / 1000) % 60 ;            //초
//    int minutes = (int) ((milliseconds / (1000*60)) % 60);  //분
//    int hours   = (int) ((milliseconds / (1000*60*60)) % 24);//시
    private void getData() {
        WeeksGetMillisecondUtil getWeeks = new WeeksGetMillisecondUtil();

        FirestoreQuerys.INSTANCE.getUserDailyFirstTime(firestore, mAuth.getCurrentUser().getUid(), getWeeks.getMondayMillisecond())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Double p0 = 0d, p1 = 0d, p2 = 0d, p3 = 0d, p4 = 0d, totalTime = 0d, totalCamp = 0d, avg = 0D;
                        int count = 1;
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                if (snapshot.exists()) {
                                    p0 += Double.parseDouble(snapshot.get("jdg.pt.tutPt.acq").toString());
                                    p1 += Double.parseDouble(snapshot.get("jdg.pt.aPt.acq").toString());
                                    p2 += Double.parseDouble(snapshot.get("jdg.pt.iPtA.acq").toString());
                                    p3 += Double.parseDouble(snapshot.get("jdg.pt.iPtP.acq").toString());
                                    p4 += Double.parseDouble(snapshot.get("jdg.pt.pPt.acq").toString());

                                    totalPoint.put(getWeeks.getDayOfWeek(Long.parseLong(snapshot.get("inf.cA").toString())), Double.parseDouble(snapshot.get("jdg.pt.tutPt.acq").toString()) + Double.parseDouble(snapshot.get("jdg.pt.aPt.acq").toString()) +Double.parseDouble(snapshot.get("jdg.pt.iPtA.acq").toString()) + Double.parseDouble(snapshot.get("jdg.pt.iPtP.acq").toString())+ Double.parseDouble(snapshot.get("jdg.pt.pPt.acq").toString()));

                                    //int num = (p1.intValue()+p2.intValue()+p3.intValue()+p4.intValue())/getWeeks.getDayOfWeek();
                                    avg += Double.parseDouble(snapshot.get("jdg.cmp.rtgCum").toString());
                                    totalCamp += Double.parseDouble(snapshot.get("jdg.cmp.ct").toString());
                                    totalTime += (Double.parseDouble(snapshot.get("jdg.cmp.tET").toString()));
                                    count++;
                                }
                            }
                            Timber.e("totalPoint = " + totalPoint);
                            initChartTest();


                            Double avgNum = (p0 + p1 + p2 + p3 + p4)/* / getWeeks.getDayOfWeek(null)*/;
                            fragmentActivityGraphAvgPointTv.setText(String.valueOf(Math.round(avgNum)));

                            Double avgRating = avg / totalCamp;

                            Timber.e("avgRating = " + avgRating + " = avg : " + avg + "/ totalCamp : " + totalCamp);
                            //fragmentActivityGraphAvgRatingTv.setText(String.valueOf(Math.round(avgRating)));

                            if (avgRating.isNaN()) {
                                fragmentActivityGraphAvgRatingTv.setText(String.valueOf("0"));
                            } else {
                                DecimalFormat format = new DecimalFormat("#.##");
                                String num = format.format(avgRating);
                                Timber.e("format =" + Double.parseDouble(String.format("%.1f", avgRating)));
                                fragmentActivityGraphAvgRatingTv.setText(num);
                            }

                            try {

                                long avgTime = Math.round(totalTime) /*/ totalCamp.intValue()*/;
                                long minutes = ((avgTime / (1000 * 60)) % 60);  //분
                                Timber.e("totalTime = " + totalTime + "avgTime = " + avgTime + "minutes =" + minutes);

                                fragmentActivityGraphTimeTv.setText(NumberFormat.getInstance().format(minutes));
                                fragmentActivityGraphCampaignTv.setText(String.valueOf(totalCamp.intValue()));
                            } catch (Exception e) {

                            }

                        } else {
                            Timber.e("실패다");
                        }
                    }
                });


    }

    private void initChartTest() {
        String[] months = new String[]{"월", "", "화", "", "수", "", "목", "", "금", "", "토", "", "일",};
        //String[] months = new String[]{"월", "화", "수", "목", "금", "토", "일",};
        List<Float> num = null;
        num = Arrays.asList(totalPoint.get(1).floatValue(), 0f, totalPoint.get(2).floatValue(), 0F, totalPoint.get(3).floatValue(), 0F, totalPoint.get(4).floatValue(), 0F, totalPoint.get(5).floatValue(), 0F, totalPoint.get(6).floatValue(), 0F, totalPoint.get(7).floatValue());
        //num = Arrays.asList(totalPoint.get(1).floatValue(), totalPoint.get(2).floatValue(), totalPoint.get(3).floatValue(), totalPoint.get(4).floatValue(), totalPoint.get(5).floatValue(), totalPoint.get(6).floatValue(), totalPoint.get(7).floatValue());
        Timber.e("totalPoints" + num.toString());

        int numSubcolumns = 1;
        int numColumns = months.length;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        ColumnChartData columnData;
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
                if (num.get(i) == 0f) {
                    values.add(new SubcolumnValue(num.get(i), Color.parseColor("#2E3141")));
                } else if (months[i].equals(WeeksGetMillisecondUtil.getDayOfWeekString())) {
                    values.add(new SubcolumnValue((float) num.get(i), Color.parseColor("#3F51B5")));
                } else {
                    values.add(new SubcolumnValue((float) num.get(i), Color.parseColor("#00BCD4")));
                }
            }

            axisValues.add(new AxisValue(i).setLabel(months[i]));
            columns.add(new Column(values).setHasLabelsOnlyForSelected(true).setHasLabels(false));//setHasLabelsOnlyForSelected = 눌렀을때 라벨표시/ setHasLabels = 라벨 표시
        }


        columnData = new ColumnChartData(columns);

        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(false));
        columnData.setAxisYLeft(null);

        chartView.setColumnChartData(columnData);
        chartView.setValueSelectionEnabled(true); //숫자가 보이도록
        // Set value touch listener that will trigger changes for chartTop.
        //chartView.setOnValueTouchListener(new ValueTouchListener());

        // Set selection mode to keep selected month column highlighted.
        chartView.setZoomEnabled(false);// 줌 안되게

//        Viewport v = new Viewport(chartView.getMaximumViewport());
//        v.left = -80f;
//        v.right = 80f;
//        chartView.setCurrentViewport(v);

        Viewport v = new Viewport(chartView.getMaximumViewport());
        float dx = 0.1f * v.width();
        float dy = 0.1f * v.height();
        v.inset(-dx, -dy);
        chartView.setMaximumViewport(v);
        chartView.setCurrentViewport(v);
    }

    private void initViewPager() {
        viewpager.setAdapter(new FragmentPagerAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return ActivityGraphChildFragment.newInstance("오늘");
                    case 1:
                        return ActivityGraphChildFragment.newInstance("누적");
                }
                return null;
            }
        });
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = PlumBoardApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
