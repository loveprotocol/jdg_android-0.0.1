package com.imhc2.plumboard.commons.util;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class CustomImageView43 extends AppCompatImageView {
    public CustomImageView43(Context context) {
        super(context);
    }

    public CustomImageView43(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView43(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height=(width * 3) / 4;
        setMeasuredDimension(width, height);
    }

}
