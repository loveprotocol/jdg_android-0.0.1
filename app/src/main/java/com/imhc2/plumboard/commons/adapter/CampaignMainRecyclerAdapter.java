package com.imhc2.plumboard.commons.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.util.CustomImageView169;
import com.imhc2.plumboard.mvvm.model.domain.CampExe;
import com.willy.ratingbar.BaseRatingBar;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 2018-04-05.
 */

public class CampaignMainRecyclerAdapter extends BaseQuickAdapter<CampExe, CampaignMainRecyclerAdapter.CampaignRecyclerViewHolder> {

    private Map<String, Integer> myParticipationList;

    public CampaignMainRecyclerAdapter(int layoutResId, @Nullable List<CampExe> data, Map<String, Integer> myParticipationList) {
        super(layoutResId, data);
        this.myParticipationList = myParticipationList;
    }

    @Override
    protected void convert(CampaignRecyclerViewHolder holder, CampExe model) {
        holder.setItemData(model);
        holder.ratingBar.setRating(model.getCamp().getRtg().getAvg());
        holder.ratingTv.setText((Math.round(model.getCamp().getRtg().getAvg() * 10)) / 10.0 + "(" + model.getCamp().getRtg().getCt() + ")");

        if (model.getCamp().getBgt().getJdg() != 0) {
            holder.pointTv.setText(NumberFormat.getInstance().format(model.getCamp().getBgt().getJdg()));
        } else {
            holder.pointTv.setText("포인트 지급 완료");
        }
        holder.campaignTitle.setText(model.getCamp().getTtl());

        if (model.getCamp().getType() == 1) {
            holder.timeTv.setText("0:00");
        }
        Glide.with(holder.itemView).load(model.getCamp().getImg()).thumbnail(0.1f).apply(new RequestOptions().optionalCenterCrop().placeholder(R.drawable.ic_campaign_empty)).into((CustomImageView169) holder.mainImg);
        Glide.with(holder.itemView).load(model.getPrj().getTImg()).apply(new RequestOptions().override(960, 540).placeholder(R.drawable.ic_campaign_empty).circleCrop()).into(holder.companyImg);


        if (myParticipationList != null) {
            if (myParticipationList.get(model.getId()) == 1) {
                holder.kindImg.setImageResource(R.drawable.ic_campaign_doing);
            } else if (myParticipationList.get(model.getId()) == 2) {
                holder.kindImg.setImageResource(R.drawable.ic_campaign_done);
            }

        } else {
            if (model.getCamp().getType() == 1) {
                Glide.with(holder.itemView).load(R.drawable.ic_campaign_video).apply(new RequestOptions().centerInside()).into(holder.kindImg);
                //holder.kindImg.setImageResource(R.drawable.ic_campaign_video);
            } else if (model.getCamp().getType() == 2) {
                holder.kindImg.setImageResource(R.drawable.ic_campaign_survey);
            } else if (model.getCamp().getType() == 3) {
                holder.kindImg.setImageResource(R.drawable.ic_campaign_free);
            }
        }

    }


    public static class CampaignRecyclerViewHolder extends BaseViewHolder {
        @BindView(R.id.layout_campaign_main_img) CustomImageView169 mainImg;
        @BindView(R.id.layout_campaign_kind_img) ImageView kindImg;
        @BindView(R.id.layout_campaign_rating_bar) BaseRatingBar ratingBar;
        @BindView(R.id.layout_campaign_rating_tv) TextView ratingTv;
        @BindView(R.id.layout_campaign_point) TextView pointTv;
        @BindView(R.id.layout_campaign_title) TextView campaignTitle;
        @BindView(R.id.layout_campaign_time_tv) TextView timeTv;
        @BindView(R.id.layout_campaign_company_img) ImageView companyImg;
        public CampExe campExe;

        private CampaignRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //mainImg = itemView.findViewById(R.id.layout_campaign_main_img);
        }

        private void setItemData(CampExe campExe) {
            this.campExe = campExe;
            //mainImg
        }
    }

}
