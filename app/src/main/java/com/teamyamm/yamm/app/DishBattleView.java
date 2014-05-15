package com.teamyamm.yamm.app;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by parkjiho on 5/12/14.
 */
public class DishBattleView extends FrameLayout {
    private DishItem item;
    public ImageView imageView;
    public TextView textView;
    private int width=0, height=0;
    private final int TEXT_TO_HEIGHT_RATIO = 12;

    public DishBattleView(Context context){
        super(context);
    }

    public DishBattleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DishBattleView(Context context, DishItem aItem, ViewGroup parent) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        item = aItem;
        setClickable(true);
        setFocusable(true);

        FrameLayout layout =(FrameLayout) inflater.inflate(R.layout.dish_item, parent, true);

        setDishItemImage((ImageView) layout.findViewById(R.id.dish_item_image));
        Log.v("DishItemView", "Dish Image Set");
        setDishItemText((TextView) layout.findViewById(R.id.dish_item_text));
        Log.v("DishItemView", "Dish Text Set"+ " " + ((TextView) layout.findViewById(R.id.dish_item_text)).getText());

        //Measures Width Dynamically
        measureDynamicDimension();


    }

    ///////////////////////////////Private
    private void measureDynamicDimension(){
        final DishBattleView div = this;
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (div.width == 0 && div.height == 0) {
                    div.width = imageView.getMeasuredWidth();
                    div.height = imageView.getMeasuredHeight();
                    Log.v("Listener", "DishItemView Dim: " + div.width + "x" + div.height + " Set Image");

                    //Set Image here
                    setImage(div.width, div.height);
                    //Set Text Size
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, div.height / TEXT_TO_HEIGHT_RATIO);
                    Log.v("Listener", "DishItemView Text: " + div.height / TEXT_TO_HEIGHT_RATIO);

                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });
    }
    /*
    * Sets image on ImageView, return true if succeed else false
    * */
    private boolean setImage(int w, int h){
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.example_image));
        return true;
    }

    private void setDishItemText(TextView view){
        view.setText(item.getName());
        this.textView = view;
    }

    private void setDishItemImage(ImageView view){
        int width, height;
  //      view.setImageDrawable(getResources().getDrawable(R.drawable.example_image));
        view.setBackgroundColor(Color.YELLOW);
        this.imageView = view;


    }
}
