package com.imhc2.plumboard.commons.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.mvvm.model.domain.Mc;
import com.imhc2.plumboard.mvvm.model.domain.McSubList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import timber.log.Timber;

/**
 * Created by user on 2018-03-12.
 */
public class McAdapter extends BaseQuickAdapter<McSubList, McAdapter.TypeViewHolder> {
    int layoutResId;
    List<String> resultList = new ArrayList<>();
    private int lastCheckedPosition = -1;
    List<String> result;
    Mc typeMc;
    private List<McSubList> originalItemList;

    public McAdapter(int layoutResId, @Nullable List<McSubList> shuffleData, List<McSubList> originalData, Mc typeMc) {
        super(layoutResId, shuffleData);
        this.layoutResId = layoutResId;
        this.result = typeMc.getRes();
        this.typeMc = typeMc;
        originalItemList = originalData;

    }

    @Override
    protected void convert(TypeViewHolder helper, McSubList item) {
        ToggleButton button = helper.type1toggleButton;
        button.setTextOff(item.getItem());
        button.setTextOn(item.getItem());
        if (typeMc.getMA() != null && typeMc.getMA()) {//다중선택
            button.setChecked(false);
            if (typeMc.getSfl() != null && typeMc.getSfl()) {//랜덤활성
                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {//누름
                            for (int i = 0; i < originalItemList.size(); i++) {
                                if (originalItemList.get(i).getItem().equals(item.getItem())) {
                                    //Log.e("index:", String.valueOf(i + 1));
                                    resultList.add(String.valueOf((i + 1)));
                                }
                            }
                            mC(helper, buttonView,item,true);
                        } else {//안누름
                            for (int i = 0; i < originalItemList.size(); i++) {
                                if (originalItemList.get(i).getItem().equals(item.getItem())) {
                                    //Log.e("index:", String.valueOf(i + 1));
                                    resultList.remove(String.valueOf(i + 1));
                                }
                            }
                            mC(helper, buttonView,item,true);
                        }
                        Timber.e("random resultList =" + resultList.toString());
                    }
                });

            } else {//랜덤아님
                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {//누름
                            resultList.add(String.valueOf((helper.getLayoutPosition()) + 1));
                            mC(helper, buttonView,item,false);
                        } else {//안누름
                            resultList.remove(String.valueOf((helper.getLayoutPosition() + 1)));
                            mC(helper, buttonView,item,false);
                        }

                        Timber.e("resultList =" + resultList.toString());
                    }

                });
            }

        } else {
            //단일선택
            if (result != null && !result.isEmpty()) {
                //button.setChecked(helper.getAdapterPosition() == lastCheckedPosition /*|| (check && helper.getAdapterPosition() == Integer.parseInt(result.get(0)))*/);
                button.setChecked(helper.getAdapterPosition() == lastCheckedPosition);
            }
            if (typeMc.getSfl() != null && typeMc.getSfl()) {//랜덤활성

                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //notifyItemChanged(lastCheckedPosition);
                            //refreshNotifyItemChanged(lastCheckedPosition);
                            lastCheckedPosition = helper.getAdapterPosition();
                            resultList.clear();
                            for (int i = 0; i < originalItemList.size(); i++) {
                                if (originalItemList.get(i).getItem().equals(item.getItem())) {
                                    //Log.e("index:", String.valueOf(i + 1));
                                    resultList.add(String.valueOf(i + 1));
                                }
                            }
                            EventBus.get().post(new Events.CardResult(resultList));
                        } else {
                            if (helper.getAdapterPosition() == lastCheckedPosition) {
                                resultList.clear();
                                EventBus.get().post(new Events.CardResult(resultList));
                            }
                        }
                    }
                });

            } else {//랜덤 비활성
                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            lastCheckedPosition = helper.getAdapterPosition();
                            resultList.clear();
                            resultList.add(0, String.valueOf((lastCheckedPosition + 1)));
                            EventBus.get().post(new Events.CardResult(resultList));
                        } else {
                            if (helper.getAdapterPosition() == lastCheckedPosition) {
                                buttonView.setChecked(false);
                                resultList.clear();
                                EventBus.get().post(new Events.CardResult(resultList));
                            }

                        }

                    }
                });

            }
        }
    }

    private void mC(TypeViewHolder helper, CompoundButton buttonView,McSubList item,boolean random) {

        if (typeMc.getML() != null) { //최소 o
            if (typeMc.getMH() != null) {//최소 o 최대 o
                if (resultList.size() <= typeMc.getMH() && resultList.size() > typeMc.getML() - 1) {
                    Timber.e("최소 o 최대 o");
                    Collections.sort(resultList);
                    EventBus.get().post(new Events.CardResult(resultList));
                } else {
                    Timber.e("최소 o 최대 o else");

                    if (!(resultList.size() > typeMc.getML() - 1)) {//최소
                        List<String> data = new ArrayList<>();
                        EventBus.get().post(new Events.CardResult(data));
                    }

                    if (!(resultList.size() <= typeMc.getMH())) {//최대
                        buttonView.setChecked(false);
                        if(random){
                            for (int i = 0; i < originalItemList.size(); i++) {
                                if (originalItemList.get(i).getItem().equals(item.getItem())) {
                                    //Log.e("index:", String.valueOf(i + 1));
                                    resultList.remove(String.valueOf(i + 1));
                                }
                            }
                        }else{
                            resultList.remove(String.valueOf((helper.getLayoutPosition() + 1)));
                        }
                    }

                }

            } else { //최소 o 최대 x
                if (resultList.size() > typeMc.getML() - 1) {
                    Timber.e("최소 o 최대 x");
                    Collections.sort(resultList);
                    EventBus.get().post(new Events.CardResult(resultList));
                } else {
                    Timber.e("최소 o 최대 x else");
                    List<String> data = new ArrayList<>();
                    EventBus.get().post(new Events.CardResult(data));
                }
            }

        } else {//최소 x
            if (typeMc.getMH() != null) {//최소 x 최대 o
                if (resultList.size() <= typeMc.getMH()) {
                    Timber.e("최소 x 최대 o");
                    Collections.sort(resultList);
                    EventBus.get().post(new Events.CardResult(resultList));
                } else {
                    Timber.e("최소 x 최대 o else");
                    Toasty.normal(mContext, "최대 "+typeMc.getMH()+"개 이하의 답변을 선택해야 합니다",Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                    if(random){
                        for (int i = 0; i < originalItemList.size(); i++) {
                            if (originalItemList.get(i).getItem().equals(item.getItem())) {
                                //Log.e("index:", String.valueOf(i + 1));
                                resultList.remove(String.valueOf(i + 1));
                            }
                        }
                    }else{
                        resultList.remove(String.valueOf((helper.getLayoutPosition() + 1)));
                    }

                }

            } else {//최소 x 최대 x
                //resultList.add(String.valueOf((helper.getLayoutPosition()) + 1));
                Timber.e("최소 x 최대 x");
            }

        }
    }


    public class TypeViewHolder extends BaseViewHolder {
        @BindView(R.id.layoutTypeMcSubListBtn) ToggleButton type1toggleButton;

        public TypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            type1toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!typeMc.getMA()) {//다중선택이 아닐시
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }
}