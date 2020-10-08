package com.imhc2.plumboard.commons.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.mvvm.model.domain.Faq;

import java.util.List;

public class FaqAdapter extends BaseQuickAdapter<Faq, BaseViewHolder> {
    public FaqAdapter(int layoutResId, @Nullable List<Faq> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Faq item) {
        helper.setText(R.id.layout_faq_item_tv, item.getTtl());
    }
}
