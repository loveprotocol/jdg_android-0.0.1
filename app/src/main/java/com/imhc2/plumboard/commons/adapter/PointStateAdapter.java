package com.imhc2.plumboard.commons.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.mvvm.model.domain.PointState;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by user on 2018-09-10.
 */

public class PointStateAdapter extends BaseQuickAdapter<PointState, BaseViewHolder> {

    public PointStateAdapter(int layoutResId, @Nullable List<PointState> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PointState item) {
        Button button = helper.getView(R.id.layout_point_state_state_tv);
        switch (item.getState()) {
            case "접수":
                //button.setBackgroundColor(Color.parseColor("#4CAF50"));
                button.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorPointStateAccept));
                break;
            case "완료":
                //button.setBackgroundColor(Color.parseColor("#3F51B5"));
                button.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorPointStateSuccess));
                break;
            case "오류":
                //button.setBackgroundColor(Color.parseColor("#EF6C00"));
                button.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorPointStateError));
                break;
            case "취소":
                button.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorPointStateCancel));
                break;

        }
        //button.setBackgroundColor();

        helper.setText(R.id.layout_point_state_state_tv, item.getState())
                .setText(R.id.layout_point_state_date_tv, item.getDate())
                .setText(R.id.layout_point_state_point_tv, NumberFormat.getInstance().format(item.getPoint()))
                .setText(R.id.layout_point_state_what_tv, item.getWhat());
    }
}
