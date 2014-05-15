package com.teamyamm.yamm.app;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by parkjiho on 5/15/14.
 */
public class DishStreamView extends FrameLayout {
    private DishItem item;
    private TextView textView;

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
       inflater.inflate(R.layout.dish_stream, this, true);
       Log.v("DishStreamView/constructor", "dish stream xml inflated");

       textView = (TextView) findViewById(R.id.dish_stream_text);
       textView.setText(item.getName());
       Log.v("DishStreamView/constructor", "textview set");

    }

    public void setDishItem(DishItem a){
        item = a;
    }
}
