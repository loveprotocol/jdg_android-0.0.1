package com.imhc2.plumboard.commons.adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.mvvm.model.domain.CardHistoryItem;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CardHistoryAdapter extends BaseQuickAdapter<CardHistoryItem, CardHistoryAdapter.CardHistoryViewHolder> {

    int position,i=0;
    HashMap<Integer, Object> resultMap;

    public CardHistoryAdapter(int layoutResId, @Nullable List<CardHistoryItem> data, int num, HashMap<Integer, Object> resultMap) {
        super(layoutResId, data);
        position = num;
        this.resultMap = resultMap;
    }

    @Override
    protected void convert(CardHistoryViewHolder helper, CardHistoryItem item) {
        helper.historyText.setText(item.getTitle());
        switch (item.getType()) {
            case "mC":
                Glide.with(mContext).load(R.drawable.ic_history_type_mc).apply(new RequestOptions().override(720)).into((ImageView) helper.getView(R.id.layout_card_history_item_img));
                break;
            case "sA":
                Glide.with(mContext).load(R.drawable.ic_history_type_sa).apply(new RequestOptions().override(720)).into((ImageView) helper.getView(R.id.layout_card_history_item_img));
                break;
            case "lA":
                Glide.with(mContext).load(R.drawable.ic_history_type_la).apply(new RequestOptions().override(720)).into((ImageView) helper.getView(R.id.layout_card_history_item_img));
                break;
            case "lS":
                Glide.with(mContext).load(R.drawable.ic_history_type_ls).apply(new RequestOptions().override(720)).into((ImageView) helper.getView(R.id.layout_card_history_item_img));
                break;
            case "vd":
                Glide.with(mContext).load(R.drawable.ic_history_type_vd).apply(new RequestOptions().override(720)).into((ImageView) helper.getView(R.id.layout_card_history_item_img));
                break;
            case "vr":
                Glide.with(mContext).load(R.drawable.ic_history_type_vr).apply(new RequestOptions().override(720)).into((ImageView) helper.getView(R.id.layout_card_history_item_img));
                break;
            case "dp":
                Glide.with(mContext).load(R.drawable.ic_history_type_dp).apply(new RequestOptions().override(720)).into((ImageView) helper.getView(R.id.layout_card_history_item_img));
                break;
            case "rating":
                Glide.with(mContext).load(R.drawable.ic_history_type_rating).apply(new RequestOptions().override(720)).into((ImageView) helper.getView(R.id.layout_card_history_item_img));
                break;
            case "last":
                Glide.with(mContext).load(R.drawable.ic_plum_logo_color).apply(new RequestOptions().override(720)).into((ImageView) helper.getView(R.id.layout_card_history_item_img));
                break;
        }

            if (resultMap.get(i) != null) {
                Timber.e("CardHistoryAdapter !=null , "+resultMap.get(i));
                helper.historyText.setTextColor(Color.parseColor("#3F51B5"));
                helper.historyIcon.setColorFilter(Color.parseColor("#3F51B5"), PorterDuff.Mode.SRC_IN);
            }else{
                Timber.e("CardHistoryAdapter ==null ,  "+resultMap.get(i));
                helper.historyText.setTextColor(Color.parseColor("#42000000"));
                helper.historyIcon.setColorFilter(Color.parseColor("#42000000"), PorterDuff.Mode.SRC_IN);
            }


        if (position == helper.getLayoutPosition()) {
            helper.historyLl.setBackgroundColor(Color.parseColor("#3F51B5"));
            helper.historyText.setTextColor(Color.parseColor("#ffffff"));
            helper.historyIcon.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
            helper.historycheckImg.setVisibility(View.VISIBLE);
        }
        i++;
    }

    public class CardHistoryViewHolder extends BaseViewHolder {
        @BindView(R.id.layout_card_history_item_img) ImageView historyIcon;
        @BindView(R.id.layout_card_history_item_tv) TextView historyText;
        @BindView(R.id.layout_card_history_item_ll) LinearLayout historyLl;
        @BindView(R.id.layout_card_history_item_check_img) ImageView historycheckImg;

        private CardHistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
