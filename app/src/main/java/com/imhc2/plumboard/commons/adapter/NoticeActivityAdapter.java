package com.imhc2.plumboard.commons.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.mvvm.model.domain.Notice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NoticeActivityAdapter extends BaseQuickAdapter<Notice, BaseViewHolder> {

    public NoticeActivityAdapter(int layoutResId, @Nullable List<Notice> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Notice item) {
        helper.setText(R.id.layout_notice_title, item.getTtl())
                .setText(R.id.layout_notice_time, convertDate(item.getDate(), "yyyy-MM-dd"));
    }


    private String convertDate(Long dateInMilliseconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.KOREA);
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return formatter.format(new Date(dateInMilliseconds));
    }

}
