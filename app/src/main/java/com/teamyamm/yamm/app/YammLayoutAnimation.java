package com.teamyamm.yamm.app;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by parkjiho on 5/19/14.
 */
public class YammLayoutAnimation  extends Animation {

    public final static int COLLAPSE = 1;
    public final static int EXPAND = 0;

    private View mView;
    private int mType;
    private MainFragment fragment;
    private LinearLayout layout1, layout2;
    public YammLayoutAnimation(View view, int duration, int type, MainFragment f) {

        setDuration(duration);
        mView = view;
        mType = type;
        fragment = f;
        layout1 = (LinearLayout) fragment.getActivity().findViewById(R.id.yamm_layout1);
        layout2 = (LinearLayout) fragment.getActivity().findViewById(R.id.yamm_layout2);

        if(mType == EXPAND) {
            fragment.setYammAndStreamLayoutWeights(1f,7f);
            layout1.setVisibility(LinearLayout.GONE);
            layout2.setVisibility(LinearLayout.VISIBLE);
        } else {
            fragment.setYammAndStreamLayoutWeights(2f, 6f);

        }

        view.setVisibility(View.VISIBLE);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        if (interpolatedTime < 1.0f) {
            if(mType == EXPAND) {
                fragment.setYammAndStreamLayoutWeights((1 + interpolatedTime) / 8f, (7f - interpolatedTime) / 8f);
            } else {
                fragment.setYammAndStreamLayoutWeights((2-interpolatedTime)/8f,(6f+interpolatedTime)/8f);
            }
        } else {
            if(mType == EXPAND) {
                fragment.setYammAndStreamLayoutWeights(1f, 3f);
            }else{
                fragment.setYammAndStreamLayoutWeights(1f, 7f);
                layout1.setVisibility(LinearLayout.VISIBLE);
                layout2.setVisibility(LinearLayout.GONE);
            }
        }
        mView.requestLayout();

    }
}