package com.example.papreeca.app;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by parkjiho on 5/11/14.
 */
public class MyIntroViewPager extends ViewPager {
    private boolean isGridSelectionDone = false;
    private final int GRID_PAGE = 1;

    public MyIntroViewPager(Context context){
        super(context);
    }

    public MyIntroViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return shouldScrollBeEnabled() ? super.onTouchEvent(arg0) : false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return shouldScrollBeEnabled() ? super.onInterceptTouchEvent(event) : false;
    }

    //////////////////Private Method

    private boolean shouldScrollBeEnabled(){
        return !(getCurrentItem()==GRID_PAGE && isGridSelectionDone==false);
    }




}
