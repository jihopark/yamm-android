package com.teamyamm.yamm.app;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by parkjiho on 5/15/14.
 */
public class DishStreamView extends FrameLayout {
    private DishItem item;
    private TextView textView;
    private ImageView imageView;
    private static double ratio = 0.5;

    public DishStreamView(Context context){
        super(context);
    }

    public DishStreamView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DishStreamView(Context context, DishItem aItem, ViewGroup parent) {
        super(context);
        Log.v("DishStreamView/constructor", "constructor started - " + aItem.getName());
        item = aItem;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.dish_stream, this, true);
        Log.v("DishStreamView/constructor", "dish stream xml inflated");
        textView = (TextView) layout.findViewById(R.id.dish_stream_text);
        textView.setText(item.getName());
        Log.v("DishStreamView/constructor", "textview set");

        imageView = (ImageView) layout.findViewById(R.id.dish_stream_image);
        setStreamImage(imageView);
    }

    public void setDishItem(DishItem a){
        item = a;
    }

    public void setStreamImage(ImageView view){
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int)(ratio*width);
        Log.v("DishStreamView/onMeasure", "Width " + width + " Height " + height );
        setMeasuredDimension(width, height);
    }
}
