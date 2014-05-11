package com.example.papreeca.app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by parkjiho on 5/10/14.
 */
public class GridItemView extends FrameLayout {
    private TextView itemText;
    private ImageView imageView;

    private boolean mChecked = false;
    private GridItem item;

    public GridItemView(Context context){
        super(context);
    }

    public GridItemView(Context context, GridItem aItem){
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.grid_item, this, true);
        setGridItemImage((ImageView)layout.findViewById(R.id.grid_item_image));
        item = aItem;
        setValues();
    }

    public void setValues(){
    }

    public void setChecked(boolean checked){
        mChecked = checked;
        if (checked)
            this.getChildAt(0).setBackgroundColor(Color.RED);
        else
            this.getChildAt(0).setBackgroundColor(Color.YELLOW);
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
     * @see android.widget.FrameLayout#onMeasure(int, int)
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    /////////////////////Private method
    private void setGridItemImage(ImageView view){
        view.setImageDrawable(getResources().getDrawable(R.drawable.image_placeholer));
    }
}