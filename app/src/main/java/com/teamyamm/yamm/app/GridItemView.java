package com.teamyamm.yamm.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by parkjiho on 5/10/14.
 */
public class GridItemView extends FrameLayout {
    private TextView itemText;
    private YammImageView imageView;
    private TextView selectedText;

    private boolean mChecked = false;
    private GridItem item;
    private Context context;

    public GridItemView(Context context){
        super(context);
    }

    public GridItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GridItemView(Context context,  GridItem aItem){
        super(context);
        item = aItem;
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.grid_item, this, true);
        setGridItemImage();
        itemText = (TextView) findViewById(R.id.grid_item_text);
        setGridItemText(itemText);
        selectedText = (TextView)findViewById(R.id.grid_item_selected);
    }

    public void setChecked(boolean checked){
        mChecked = checked;
        if (checked) {
            selectedText.setVisibility(View.VISIBLE);
            selectedText.bringToFront();
            selectedText.invalidate();
        }
        else
            selectedText.setVisibility(View.INVISIBLE);
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
        item = new GridItem(i);
        setGridItemText(itemText);
        imageView.setID(i.getId());
        imageView.loadImage();
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
    private void setGridItemText(TextView view){
        view.bringToFront();
        view.invalidate();
        view.setText(item.getName());
    }

    private void setGridItemImage(){
        imageView = new YammImageView(context, YammImageView.GRID, 100, 100, item.getId());
        addView(imageView, 0);
    }
}