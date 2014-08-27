package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


/**
 * Created by parkjiho on 5/12/14.
 */
public class BattleFragment extends Fragment{
    DishBattleView first, second;
    Button battleNoneButton;
    FrameLayout layout1, layout2;
    BattleItem item;
    LinearLayout mainLayout;

    Animation battleFromLeft, battleFromRight, battleToLeft, battleToRight, animationWithListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        Log.i("BattleFragment", "BattleFragment onCreateView Started");

        mainLayout = (LinearLayout) inflater.inflate(R.layout.fragment_battle, container, false);

      //  thumb1 = (ImageView) mainLayout.findViewById(R.id.first_thumb);
      //  thumb2 = (ImageView) mainLayout.findViewById(R.id.second_thumb);
        loadAnimations();
        return mainLayout;
    }

    public void setLayoutClickable(boolean click){
        Log.i("BattleFragment/setLayoutClickable","Layouts enabled " +click);
        layout1.setEnabled(click);
        layout2.setEnabled(click);
    }

    public void setDishItemView(BattleItem i){
        item = i;
        Log.i("BattleFragment setDishItemView", "BattleFragment setDishItem Started");
        layout1 = (FrameLayout) mainLayout.findViewById(R.id.battle_layout1);
        layout2 = (FrameLayout) mainLayout.findViewById(R.id.battle_layout2);

        layout1.removeAllViews();
        layout2.removeAllViews();

        first = new DishBattleView(getActivity(),item.getFirst(), layout1);
        second = new DishBattleView(getActivity(),item.getSecond(), layout2);

        //Set Battle Non Button
        battleNoneButton = (Button) mainLayout.findViewById(R.id.battle_none_button);
        battleNoneButton.setOnClickListener(setBattleNoneButtonClickListener());


        //Set DishItemView onClickListener
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setResult(BattleItem.FIRST);
                first.showThumbsUp();
                startBattleChoiceAnimation(0);
                Log.i("BattleFragment/onClickListener", "First Dish Selected " + item.getFirst());

            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setResult(BattleItem.SECOND);
                second.showThumbsUp();
                startBattleChoiceAnimation(1);
                Log.i("BattleFragment/onClickListener", "Second Dish Selected " + item.getSecond());
            }
        });

        startBattleIntroAnimation();
    }

    private View.OnClickListener setBattleNoneButtonClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setResult(BattleItem.NONE);
                Log.i("BattleFragment/onClickListener", "No Dish Selected");
                startBattleChoiceAnimation(2);
            }
        };
    }

    public void loadNextItem(){
        ((BattleActivity)getActivity()).loadNextItem(item);
    }

    private void loadAnimations(){
        battleFromLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.battle_from_left);
        battleFromRight = AnimationUtils.loadAnimation(getActivity(), R.anim.battle_from_right);

        battleToLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.battle_to_left);
        battleToRight = AnimationUtils.loadAnimation(getActivity(), R.anim.battle_to_right);

        animationWithListener = AnimationUtils.loadAnimation(getActivity(), R.anim.battle_to_left);

        animationWithListener.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.i("BattleFragment/onAnimationEnd","Animation Listener for NO selection");
                ((BattleActivity)getActivity()).loadNextItem(item);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void startBattleIntroAnimation(){
        first.getTextView().startAnimation(battleFromLeft);
        second.getTextView().startAnimation(battleFromRight);
    }

    private void startBattleChoiceAnimation(int win){
        if (win == 0){
            second.getTextView().startAnimation(battleToRight);
        }
        else if (win == 1){
            first.getTextView().startAnimation(battleToLeft);
        }
        else{
            second.getTextView().startAnimation(battleToRight);
            first.getTextView().startAnimation(animationWithListener);
        }

    }


}