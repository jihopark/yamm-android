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
   // public static final int VERTICAL_SPACING = 5;
   // public static final int HORIZONTAL_SPACING = 5;
   // public static final int PADDING = 5;

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
    //    this.setVerticalSpacing(GridSelectionListView.VERTICAL_SPACING);
    //    this.setHorizontalSpacing(GridSelectionListView.HORIZONTAL_SPACING);
    //    this.setPadding(GridSelectionListView.PADDING, GridSelectionListView.PADDING, GridSelectionListView.PADDING, GridSelectionListView.PADDING);
    }

}