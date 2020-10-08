package com.imhc2.plumboard.commons.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.util.CustomImageView169;
import com.imhc2.plumboard.mvvm.model.domain.CampExe;
import com.willy.ratingbar.BaseRatingBar;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 2018-08-29.
 */

public class CampaignMainPagingAdapter extends FirestorePagingAdapter<CampExe, CampaignMainPagingAdapter.CampaignViewHolder> {

    setOnClick setOnClick;
    Integer kind;

    public CampaignMainPagingAdapter(@NonNull FirestorePagingOptions<CampExe> options, Integer kind) {
        super(options);
        this.kind = kind;
    }

    public interface setOnClick {
        void onClick(View view, CampaignViewHolder holder);
    }

    public void setOnclickListener(setOnClick onclickListener) {
        this.setOnClick = onclickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull CampaignViewHolder holder, int position, @NonNull CampExe model) {
        holder.setItemData(model);
        holder.ratingBar.setRating(model.getCamp().getRtg().getAvg());
        holder.ratingTv.setText(Math.round(model.getCamp().getRtg().getAvg() * 10 )/ 10.0 + "(" + model.getCamp().getRtg().getCt() + ")");
        holder.pointTv.setText(NumberFormat.getInstance().format(model.getCamp().getBgt().getJdg()));
        holder.campaignTitle.setText(model.getCamp().getTtl());
        if (model.getCamp().getType() == 1) {
            holder.timeTv.setText("0:00");
        }
        Glide.with(holder.itemView).load(model.getCamp().getImg()).apply(new RequestOptions().placeholder(R.drawable.ic_campaign_empty)).into(holder.mainImg);
        Glide.with(holder.itemView).load(model.getPrj().getTImg()).apply(new RequestOptions().circleCrop().placeholder(R.drawable.ic_campaign_empty)).into(holder.companyImg);
        if (model.getCamp().getType() == 1) {
            holder.kindImg.setImageResource(R.drawable.ic_campaign_video);
        } else if (model.getCamp().getType() == 2) {
            holder.kindImg.setImageResource(R.drawable.ic_campaign_survey);
        }else if(model.getCamp().getType() ==3){
            holder.kindImg.setImageResource(R.drawable.ic_campaign_free);
        }

    }

    @NonNull
    @Override
    public CampaignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (kind == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_campaign_m, parent, false);
        } else if (kind == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_campaign, parent, false);
        }
        CampaignViewHolder holder = new CampaignViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnClick.onClick(view, holder);
            }
        });
        return holder;
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
//        switch (state) {
//            case LOADING_INITIAL:
//                Timber.e("onLoadingStateChanged : " + "데이터 로딩중");
//                break;
//            case LOADED:
//                Timber.e("onLoadingStateChanged : " + "데이터 로딩함");
//                break;
//            case FINISHED:
//                Timber.e("onLoadingStateChanged : " + "데이터 로딩끝");
//                break;
//        }

    }


    public class CampaignViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout_campaign_main_img) CustomImageView169 mainImg;
        @BindView(R.id.layout_campaign_kind_img) ImageView kindImg;
        @BindView(R.id.layout_campaign_rating_bar) BaseRatingBar ratingBar;
        @BindView(R.id.layout_campaign_rating_tv) TextView ratingTv;
        @BindView(R.id.layout_campaign_point) TextView pointTv;
        @BindView(R.id.layout_campaign_title) TextView campaignTitle;
        @BindView(R.id.layout_campaign_time_tv) TextView timeTv;
        @BindView(R.id.layout_campaign_company_img) ImageView companyImg;

        public CampExe campExe;

        private CampaignViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //mainImg = itemView.findViewById(R.id.layout_campaign_main_img);
        }

        private void setItemData(CampExe campExe) {
            this.campExe = campExe;
            //Glide.with(itemView).load(campExe.getCamp().getImg()).thumbnail(0.1f).apply(new RequestOptions().optionalCenterCrop().placeholder(R.drawable.ic_campaign_empty)).into(mainImg);

        }

    }
}
