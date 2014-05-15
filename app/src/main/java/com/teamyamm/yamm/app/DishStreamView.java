package com.teamyamm.yamm.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by parkjiho on 5/15/14.
 */
public class DishStreamView extends FrameLayout {

    public DishStreamView(Context context){
        super(context);
    }

    public DishStreamView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DishStreamView(Context context, DishItem aItem, ViewGroup parent) {
        super(context);


    }
}
