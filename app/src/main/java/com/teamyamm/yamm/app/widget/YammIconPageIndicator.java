package com.teamyamm.yamm.app.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by parkjiho on 9/3/14.
 */
public class YammIconPageIndicator extends LinearLayout implements PageIndicator {


    private final LinearLayout mIconsLayout;

    private ViewPager mViewPager;
    private OnPageChangeListener mListener;
    private Runnable mIconSelector;
    private int mSelectedIndex;

    public YammIconPageIndicator(Context context) {
        this(context, null);
    }

    public YammIconPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);

        mIconsLayout = new LinearLayout(context);
        mIconsLayout.setOrientation(HORIZONTAL);
        addView(mIconsLayout, new LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER));
    }

   /* private void animateToIcon(final int position) {
        final View iconView = mIconsLayout.getChildAt(position);
        if (mIconSelector != null) {
            removeCallbacks(mIconSelector);
        }
        mIconSelector = new Runnable() {
            public void run() {
                final int scrollPos = iconView.getLeft() - (getWidth() - iconView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mIconSelector = null;
            }
        };
        post(mIconSelector);
    }*/

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mIconSelector != null) {
            // Re-post the selector we saved
            post(mIconSelector);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mIconSelector != null) {
            removeCallbacks(mIconSelector);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(arg0);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (mListener != null) {
            mListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        setCurrentItem(arg0);
        if (mListener != null) {
            mListener.onPageSelected(arg0);
        }
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        view.setOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        mIconsLayout.removeAllViews();
        IconPagerAdapter iconAdapter = (IconPagerAdapter) mViewPager.getAdapter();
        int count = iconAdapter.getCount();
        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT, 1.0f);
            ImageView view = new ImageView(getContext());
            view.setLayoutParams(param);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setImageResource(iconAdapter.getIconResId(i));
            view.setOnClickListener(new PageIndicatorClickListener(i));

            mIconsLayout.addView(view);
        }
        if (mSelectedIndex > count) {
            mSelectedIndex = count - 1;
        }
        setCurrentItem(mSelectedIndex);
        requestLayout();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mSelectedIndex = item;
        mViewPager.setCurrentItem(item);

        int tabCount = mIconsLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            View child = mIconsLayout.getChildAt(i);
            boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                //animateToIcon(item);
            }
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }

    public class PageIndicatorClickListener implements OnClickListener{
        private int position;

        public PageIndicatorClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(position);
        }
    }
}
