package com.imhc2.plumboard.commons.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.mvvm.model.domain.ProfileList;

import java.util.List;

/**
 * Created by user on 2018-07-11.
 */

public class ProfileMainAdapter extends BaseQuickAdapter<ProfileList,BaseViewHolder>{


    public ProfileMainAdapter(int layoutResId, @Nullable List<ProfileList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProfileList item) {
        helper.setText(R.id.layout_profile_main_title,item.getTitle())
                .setImageResource(R.id.layout_profile_main_img,item.getImageResource());

        if(item.getTitle().equals("알림")){
            helper.setVisible(R.id.layout_profile_divide,true);
        }
    }
}
