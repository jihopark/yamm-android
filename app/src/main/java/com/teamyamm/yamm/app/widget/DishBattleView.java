package com.teamyamm.yamm.app.widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamyamm.yamm.app.BaseActivity;
import com.teamyamm.yamm.app.BattleFragment;
import com.teamyamm.yamm.app.pojos.DishItem;
import com.teamyamm.yamm.app.R;

/**
 * Created by parkjiho on 5/12/14.
 */
public class DishBattleView extends FrameLayout {
    private DishItem item;
    public YammImageView imageView;
    public TextView dishText;
    private int width=0, height=0;
    private Context context;
    private FrameLayout layout;
    private ImageView thumb;


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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        item = aItem;

        layout =(FrameLayout) inflater.inflate(R.layout.dish_item, parent, true);

        imageView = (YammImageView) layout.findViewById(R.id.dish_image);
        thumb = (ImageView) layout.findViewById(R.id.thumb);


        setClickable(true);
        setFocusable(true);

        setDishItemText((TextView) layout.findViewById(R.id.dish_item_text));
        setImageView();

    }

    public void showThumbsUp(int i){
        Animation thumbsUpAnimation = AnimationUtils.loadAnimation(context, R.anim.thumbs_up_animation);
        final Animation putdown = AnimationUtils.loadAnimation(context, R.anim.battle_put_down);
        final int choice = i;
        putdown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                thumb.setVisibility(View.GONE);
                Fragment fragment = ((BaseActivity) context).getSupportFragmentManager().findFragmentByTag("bfragment");
                if (fragment instanceof BattleFragment) {
                    ((BattleFragment)fragment).toggleClicks(true);
                    ((BattleFragment)fragment).loadNextItem();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        thumb.setVisibility(View.VISIBLE);
        thumbsUpAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Fragment fragment = ((BaseActivity) context).getSupportFragmentManager().findFragmentByTag("bfragment");

                if (fragment instanceof BattleFragment) {
                    ((BattleFragment) fragment).startBattleChoiceAnimation(choice);
                }
                dishText.startAnimation(putdown);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        thumb.startAnimation(thumbsUpAnimation);
    }

    ///////////////////////////////Private

    private void setImageView(){
        Log.i("DishBattleView/setImageView", "DishBattleView " + width + "x" + height + " Set Image");

        TextView tv = (TextView) layout.findViewById(R.id.dish_item_text);

        Log.i("DishBattleView/setImageView", "DishText " + tv.getText() );
        imageView.setDimension(width, height);
        imageView.setID(item.getId());
        imageView.setPath(YammImageView.BATTLE);
        imageView.loadImage();
    }

    private void measureDynamicDimension(){
        final DishBattleView div = this;
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                Log.i("DishBattleView/onPreDraw", "PreDraw");

                if (div.width == 0 && div.height == 0 ) {
                    div.width = imageView.getMeasuredWidth();
                    div.height = imageView.getMeasuredHeight();

                    //Set Image here

                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });
    }

    public int getImageWidth(){ return imageView.getImageWidth(); }
    public int getImageHeight(){ return imageView.getImageHeight(); }

    public TextView getTextView(){
        return dishText;
    }


    private void setDishItemText(TextView view){
        view.setText(item.getName());
        this.dishText = view;
    }
}
