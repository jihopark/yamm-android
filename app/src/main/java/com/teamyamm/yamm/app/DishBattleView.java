package com.teamyamm.yamm.app;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by parkjiho on 5/12/14.
 */
public class DishBattleView extends FrameLayout {
    private DishItem item;
    public YammImageView imageView;
    public TextView textView;
    private int width=0, height=0;
    private ImageView thumbsUp;
    private Context context;
    private FrameLayout layout;

    public DishBattleView(Context context){
        super(context);
    }

    public DishBattleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DishBattleView(Context context, DishItem aItem, ViewGroup parent) {
        super(context);

        this.context = context;
        this.width = ((BaseActivity)context).getScreenWidth();
        this.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230, context.getResources().getDisplayMetrics());

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        item = aItem;
        setClickable(true);
        setFocusable(true);

        layout =(FrameLayout) inflater.inflate(R.layout.dish_item, parent, true);


        setImageView();

        setDishItemText((TextView) layout.findViewById(R.id.dish_item_text));
        Log.i("DishItemView", width + "x" + height + "Dish Set" + " " + ((TextView) layout.findViewById(R.id.dish_item_text)).getText());
    }

    public void showThumbsUp(){
        Animation thumbsUpAnimation = AnimationUtils.loadAnimation(context, R.anim.thumbs_up_animation);

        thumbsUp = new ImageView(context);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        thumbsUp.setImageDrawable(getResources().getDrawable(R.drawable.thumbsup));
        layout.addView(thumbsUp, 0);
        thumbsUp.startAnimation(thumbsUpAnimation);
    }

    ///////////////////////////////Private

    private void setImageView(){
        imageView = new YammImageView(context, YammImageView.DISH, width, height , item.getId());
        layout.addView(imageView, 0);
    }
/*
    private void measureDynamicDimension(){
        final DishBattleView div = this;
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnGlobalLayoutistener(new ViewTreeObserver.OnGGlobalLayout   Listener() {
            public boolean onPreDraw() {
                Log.i("OnPreDraw", "PreDraw " + div.width +" "+ div.height);
                if (div.width == 0 && div.height == 0) {
                    div.width = imageView.getMeasuredWidth();
                    div.height = imageView.getMeasuredHeight();
                    Log.i("OnPreDraw", "PreDraw " + div.width +" "+ div.height);
                    //Set Image here
                    setImage(div.width, div.height);
                    //Set Text Size
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, div.height / TEXT_TO_HEIGHT_RATIO);

                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });
        Log.i("DishBattleView/measureDynamicDimension", "ViewTreeObserver Set");
    }
 */

    private void setDishItemText(TextView view){
        view.setText(item.getName());
        this.textView = view;
    }
}
