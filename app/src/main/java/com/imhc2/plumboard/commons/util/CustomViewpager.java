package com.imhc2.plumboard.commons.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import timber.log.Timber;

/**
 * Created by user on 2018-03-14.
 */

public class CustomViewpager extends ViewPager {
    public enum SwipeDirection {
        all, left, right, none
    }

    private float initialXValue;
    private SwipeDirection direction;

    public CustomViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.direction = SwipeDirection.all;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try{//카드진행중 더블터치 방지
            if(this.IsSwipeAllowed(event)&&event.getPointerCount() == 1){
                return super.onTouchEvent(event);
            }else{
                return false;
            }
        }catch (IllegalArgumentException e){
            Timber.e(e);
            return false;
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.IsSwipeAllowed(event)) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    private boolean IsSwipeAllowed(MotionEvent event) {
        if (this.direction == SwipeDirection.all) return true;

        if (direction == SwipeDirection.none)//disable any swipe
            return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            initialXValue = event.getX();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            try {
                float diffX = event.getX() - initialXValue;
                if (diffX > 0 && direction == SwipeDirection.right) {
                    // swipe from left to right detected
                    return false;
                } else if (diffX < 0 && direction == SwipeDirection.left) {
                    // swipe from right to left detected
                    return false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return true;
    }

    public void setAllowedSwipeDirection(SwipeDirection direction) {
        this.direction = direction;
    }

}
