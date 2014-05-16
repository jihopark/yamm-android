package com.teamyamm.yamm.app;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by parkjiho on 5/15/14.
 */
public class DishStreamView extends FrameLayout {
    private DishItem item;
    private TextView textView;
    private ImageView imageView;
    private int width, height;
    private static double ratio = 0.5;
    private Context context;

    public DishStreamView(Context context){
        super(context);
    }

    public DishStreamView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DishStreamView(Context context, DishItem aItem, ViewGroup parent) {
        super(context);

        this.context = context;

        Log.v("DishStreamView/constructor", "constructor started - " + aItem.getName());
        item = aItem;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.dish_stream, this, true);
        Log.v("DishStreamView/constructor", "dish stream xml inflated");
        textView = (TextView) layout.findViewById(R.id.dish_stream_text);
        Log.v("DishStreamView/constructor", "textview set");

        imageView = (ImageView) layout.findViewById(R.id.dish_stream_image);
        loadViews();
    }

    public void setDishItem(DishItem a){
        item = a;
    }

    public void setStreamImage(){
        Picasso.with(context).load(BaseActivity.getDishImageURL(item.getId(),imageView.getMeasuredWidth(),imageView.getMeasuredHeight())).into(imageView);
    }

    public void loadViews(){
        setStreamImage();
        textView.setText(item.getName());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width =  MeasureSpec.getSize(widthMeasureSpec);
        height = (int)(ratio*width);
        setMeasuredDimension(width, height);
        Log.v("DishStreamView/onMeasure", "Width " + width + " Height " + height );

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View v = getChildAt(i);
            // this works because you set the dimensions of the ImageView to FILL_PARENT
            v.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
    }
}
