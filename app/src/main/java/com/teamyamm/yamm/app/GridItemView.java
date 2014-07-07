package com.teamyamm.yamm.app;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by parkjiho on 5/10/14.
 */
public class GridItemView extends FrameLayout {
    private ImageView imageView;

    private boolean mChecked = false;
    private GridItem item;
    private Context context;
    private int position;

    public GridItemView(Context context){
        super(context);
    }

    public GridItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GridItemView(Context context,  GridItem aItem, int position){
        super(context);
        item = aItem;
        this.context = context;
        this.position = position;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.grid_item, this, true);

        imageView = (ImageView) layout.findViewById(R.id.grid_item_image);
    }

    public void setChecked(boolean checked){
        mChecked = checked;
    }

    public boolean getChecked(){
        return mChecked;
    }

    public void toggle(){
        imageView.setSelected(!mChecked);
        setChecked(!mChecked);
    }

    public GridItem getGridItem(){
        return item;
    }

    public void setGridItem(GridItem i, int position){
        item = new GridItem(i);
        this.position = position;
        setGridItemImage();
    }
    /*
     * To make square
     * @see android.widget.FrameLayout#onMeasure(int, int)
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    /////////////////////Private method
    private void setGridItemImage(){
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[] {android.R.attr.state_selected},
                context.getResources().
                        getDrawable(getResources().getIdentifier("@drawable/hate_" + getDrawableID(position + 1) + "_sel", "drawable", context.getPackageName())));
        states.addState(new int[] { },
                context.getResources().
                        getDrawable(getResources().getIdentifier("@drawable/hate_" + getDrawableID(position + 1) + "_nml", "drawable", context.getPackageName())));
        imageView.setImageDrawable(states);
    }

    private String getDrawableID(int id){
        if (id < 10)
            return "0"+id;
        else
            return ""+id;
    }
}