package com.teamyamm.yamm.app;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by parkjiho on 5/19/14.
 */
public class FriendsListAnimation  extends Animation {

    public final static int COLLAPSE = 1;
    public final static int EXPAND = 0;

    private float k;
    private View mView;
    private int mType;
    private MainFragment fragment;

    public FriendsListAnimation(View view, int duration, int type, MainFragment f, float kay) {

        setDuration(duration);
        mView = view;
        mType = type;
        fragment = f;
        k = kay;

        if(mType == EXPAND) {
            fragment.setYammAndStreamLayoutWeights(k,1f-k);
            fragment.setYammLayout2Weights(1,0,1);
        } else {
            fragment.setYammAndStreamLayoutWeights(1-k/2f, k/2f);
            fragment.setYammLayout2Weights(1f/7f, 5f/7f, 1f/7f);
        }
        view.setVisibility(View.VISIBLE);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        if (interpolatedTime < 1.0f) {
            if(mType == EXPAND) {
                fragment.setYammAndStreamLayoutWeights(k+(7f/8f-k)*interpolatedTime, 1f - (k+(7f/8f-k)*interpolatedTime) );
                fragment.setYammLayout2Weights(1f/(2f+5*interpolatedTime), 1f - 2f/(2f+5*interpolatedTime), 1f/(2f+5*interpolatedTime));
            } else {
                fragment.setYammAndStreamLayoutWeights(1-(k/2)+(1f-3f/2f*k)*interpolatedTime, (k/2)+(1f-3f/2f*k)*interpolatedTime);
                fragment.setYammLayout2Weights(1f/(7f-5*interpolatedTime), 1f - 2f/(7f-5*interpolatedTime), 1f/(7f-5*interpolatedTime));
            }
        } else {
            if(mType == EXPAND) {
                fragment.setYammAndStreamLayoutWeights(1-k/2f, k/2f);
                fragment.setYammLayout2Weights(1f/7f, 5f/7f, 1f/7f);
            }else{
                fragment.setYammAndStreamLayoutWeights(k,1f-k);
                fragment.setYammLayout2Weights(1,0,1);
            }
        }
        mView.requestLayout();

    }
}