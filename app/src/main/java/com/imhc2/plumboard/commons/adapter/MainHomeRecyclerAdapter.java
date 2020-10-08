package com.imhc2.plumboard.commons.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.util.CustomImageView43;
import com.imhc2.plumboard.mvvm.model.domain.CampExe;
import com.willy.ratingbar.BaseRatingBar;

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 2018-04-05.
 */

public class MainHomeRecyclerAdapter extends BaseQuickAdapter<CampExe, MainHomeRecyclerAdapter.MainHomeViewHolder> {
//    List<CampaignSmall> data;
//    Context mContext;
//
//    public MainHomeRecyclerAdapter(List<CampaignSmall> data,Context context) {
//        this.data = data;
//        mContext=context;
//    }
//
//    @NonNull
//    @Override
//    public MainHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MainHomeViewHolder holder, int position) {
//        holder.titleTv.setText(data.get(position).getTitle());
//        holder.pointTv.setText(data.get(position).getPoint());
//        holder.ratingBar.setRating(data.get(position).getAvg());
//        holder.ratingTv.setText(String.valueOf(data.get(position).getAvg()+"("+data.get(position).getCount()+")"));
//
//        Glide.with(mContext).load(data.get(position).getImage()).into(holder.mainImg);
//        if(data.get(position).getType() == 1){
//            holder.typeSubImg.setImageResource(R.drawable.ic_campaign_video);
//        }else if(data.get(position).getType() ==2){
//            holder.typeSubImg.setImageResource(R.drawable.ic_campaign_survey);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return data.size();
//    }


    private Context mContext;
    private List<CampExe> data;

    public MainHomeRecyclerAdapter(int layoutResId, @Nullable List<CampExe> data, Context context) {
        super(layoutResId, data);
        this.data = data;
        mContext = context;
    }

    @Override
    protected void convert(MainHomeViewHolder helper, CampExe item) {

        helper.titleTv.setText(item.getCamp().getTtl());

        helper.pointTv.setText(NumberFormat.getInstance().format(item.getCamp().getBgt().getJdg()));

        helper.ratingBar.setRating(item.getCamp().getRtg().getAvg());

        helper.ratingTv.setText((Math.round(item.getCamp().getRtg().getAvg() *10)/10.0)+ "(" + item.getCamp().getRtg().getCt() + ")");

        Glide.with(mContext).load(item.getCamp().getImg()).apply(new RequestOptions().placeholder(R.drawable.ic_campaign_empty).optionalCenterCrop()).into(helper.mainImg);
        if (item.getCamp().getType() == 1) {
            helper.typeSubImg.setImageResource(R.drawable.ic_campaign_video);
        } else if (item.getCamp().getType() == 2) {
            helper.typeSubImg.setImageResource(R.drawable.ic_campaign_survey);
        }else if(item.getCamp().getType() ==3){
            helper.typeSubImg.setImageResource(R.drawable.ic_campaign_free);
        }
    }


    public static class MainHomeViewHolder extends BaseViewHolder {
        @BindView(R.id.layout_main_home_image) CustomImageView43 mainImg;
        @BindView(R.id.layout_main_home_type_img) ImageView typeSubImg;
        @BindView(R.id.layout_main_home_title) TextView titleTv;
        @BindView(R.id.layout_main_home_point) TextView pointTv;
        @BindView(R.id.layout_main_home_rating_bar) BaseRatingBar ratingBar;
        @BindView(R.id.layout_main_home_rating_num) TextView ratingTv;

        public MainHomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
