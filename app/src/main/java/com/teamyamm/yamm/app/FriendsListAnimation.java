package com.teamyamm.yamm.app;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
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

        setInterpolator(new AccelerateInterpolator());
        setDuration(duration);
        mView = view;
        mType = type;
        fragment = f;
        k = kay;

        if(mType == EXPAND) {
            fragment.setYammAndStreamLayoutWeights(k,1f-k);
            fragment.setYammLayout2Weights(1,0,1);
        } else {
            fragment.setYammAndStreamLayoutWeights(1, 0);
            fragment.setYammLayout2Weights(1, 7, 0);
        }
        view.setVisibility(View.VISIBLE);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        if (interpolatedTime < 1.0f) {
            if(mType == EXPAND) {
                fragment.setYammAndStreamLayoutWeights(k+ (1f-k)*interpolatedTime, 1f - (k+(1f-k)*interpolatedTime) );
                fragment.setYammLayout2Weights((1f/8f)*4f/(1+3*interpolatedTime), interpolatedTime*7f/8f*4f/(1+3*interpolatedTime), (1- interpolatedTime)/2f/(1+3*interpolatedTime));
            } else {
                fragment.setYammAndStreamLayoutWeights(1f-(1-k)*interpolatedTime, (1-k)*interpolatedTime);
                fragment.setYammLayout2Weights(1f/8f*4f/(4f-3*interpolatedTime), 7f/8f*(1-interpolatedTime)*4f/(4f-3*interpolatedTime), interpolatedTime*1f/8f*4f/(4f-3*interpolatedTime));
            }
        } else {
            if(mType == EXPAND) {
                fragment.setYammAndStreamLayoutWeights(1, 0);
                fragment.setYammLayout2Weights(1, 7, 0);
            }else{
                fragment.setYammAndStreamLayoutWeights(k,1f-k);
                fragment.setYammLayout2Weights(1,0,1);
            }
        }
        mView.requestLayout();

    }
}