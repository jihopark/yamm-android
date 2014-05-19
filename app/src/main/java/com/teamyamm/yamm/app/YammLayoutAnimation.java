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
    private float k;

    public YammLayoutAnimation(View view, int duration, int type, MainFragment f, float kay) {

        setDuration(duration);
        mView = view;
        mType = type;
        fragment = f;
        k = kay;

        layout1 = (LinearLayout) fragment.getActivity().findViewById(R.id.yamm_layout1);
        layout2 = (LinearLayout) fragment.getActivity().findViewById(R.id.yamm_layout2);

        if(mType == EXPAND) {
            fragment.setYammAndStreamLayoutWeights(k/2f,1f-(k/2f));
            layout1.setVisibility(LinearLayout.GONE);
            layout2.setVisibility(LinearLayout.VISIBLE);
        } else {
            fragment.setYammAndStreamLayoutWeights(k, 1f-k);

        }

        view.setVisibility(View.VISIBLE);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        if (interpolatedTime < 1.0f) {
            if(mType == EXPAND) {
                fragment.setYammAndStreamLayoutWeights(k*(1f+interpolatedTime)/2f,1f - k*(1f+interpolatedTime)/2f);
            } else {
                fragment.setYammAndStreamLayoutWeights(k*(2-interpolatedTime)/2,1f-k*(2-interpolatedTime)/2);
            }
        } else {
            if(mType == EXPAND) {
                fragment.setYammAndStreamLayoutWeights(k, 1f-k);
            }else{
                fragment.setYammAndStreamLayoutWeights(k/2f,1f-(k/2f));
                layout1.setVisibility(LinearLayout.VISIBLE);
                layout2.setVisibility(LinearLayout.GONE);
            }
        }
        mView.requestLayout();

    }
}