package com.imhc2.plumboard.commons.util;

import android.content.Context;
import android.util.AttributeSet;

public class CustomImageView169 extends android.support.v7.widget.AppCompatImageView {
    public CustomImageView169(Context context) {
        super(context);
    }

    public CustomImageView169(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView169(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height=(width * 9) / 16;
        setMeasuredDimension(width, height);
    }
}
