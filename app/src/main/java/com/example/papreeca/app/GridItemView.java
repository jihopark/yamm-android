package com.example.papreeca.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by parkjiho on 5/10/14.
 */
public class GridItemView extends RelativeLayout {
    private TextView dishItemText;
    private ImageView dishImageView;
    private boolean mChecked = false;
    private GridItem item;

    public GridItemView(Context context){
        super(context);
    }

    public GridItemView(Context context, GridItem aItem){
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.grid_item, this, true);

        //dishItemText = (TextView)findViewById(R.id.dishItemValue);
        //dishImageView = (ImageView) findViewById(R.id.dishImageView);
        item = aItem;
        setValues();
    }

    public void setValues(){
    }

    public void setChecked(boolean checked){
        mChecked = checked;
    }

    public boolean getChecked(){
        return mChecked;
    }

    public void toggle(){
        setChecked(!mChecked);
    }

    public GridItem getGridItem(){
        return item;
    }

    public void setGridItem(GridItem i){
        item = i;
        setValues();
    }
    /*
     * To make square
     * @see android.widget.RelativeLayout#onMeasure(int, int)
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}