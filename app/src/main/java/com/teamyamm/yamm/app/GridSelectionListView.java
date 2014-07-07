package com.teamyamm.yamm.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by parkjiho on 5/10/14.
 */
public class GridSelectionListView extends GridView {

    public static final int COLUMN = 4;

    public GridSelectionListView(Context context){
        super(context);
        init();
    }

    public GridSelectionListView(Context context, AttributeSet attrs){
        super(context, attrs);

        init();
    }

    private void init(){
        ViewGroup.LayoutParams scrollParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);

        this.setLayoutParams(scrollParams);
        this.setNumColumns(GridSelectionListView.COLUMN);
    }

}