package com.teamyamm.yamm.app;

import android.view.View;

/**
 * Created by parkjiho on 9/3/14.
 */
public class LeftDrawerItem {
    public String firstText;
    public String secondText;
    public int position;
    public View.OnClickListener firstClick=null, secondClick=null, totalClick=null;

    public LeftDrawerItem(String firstText, String secondText, int position){
        this.firstText = firstText;
        this.secondText = secondText;
        this.position = position;
    }

    public LeftDrawerItem(String firstText, String secondText, int position,
                          View.OnClickListener firstClick, View.OnClickListener secondClick){
        this(firstText, secondText, position);
        this.firstClick = firstClick;
        this.secondClick = secondClick;
    }

    public LeftDrawerItem(String firstText, String secondText, int position,
                          View.OnClickListener totalClick){
        this(firstText, secondText, position);
        this.totalClick = totalClick;
    }
}
