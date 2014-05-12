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
 * Created by parkjiho on 5/12/14.
 */
public class DishItemView extends FrameLayout {
    DishItem item;

    public DishItemView(Context context){
        super(context);
    }

    public DishItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DishItemView(Context context,  DishItem aItem, ViewGroup parent) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        item = aItem;

        FrameLayout layout =(FrameLayout) inflater.inflate(R.layout.dish_item, parent, true);

        setDishItemImage((ImageView) layout.findViewById(R.id.dish_item_image));
        Log.v("DishItemView", "Dish Image Set");
        setDishItemText((TextView) layout.findViewById(R.id.dish_item_text));
        Log.v("DishItemView", "Dish Text Set"+ " " + ((TextView) layout.findViewById(R.id.dish_item_text)).getText());
    }

    ///////////////////////////////Private
    private void setDishItemText(TextView view){
        view.setText(item.getName());
    }

    private void setDishItemImage(ImageView view){
        view.setImageDrawable(getResources().getDrawable(R.drawable.example_image));
    }
}
