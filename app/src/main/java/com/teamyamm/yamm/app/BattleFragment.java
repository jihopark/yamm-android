package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


/**
 * Created by parkjiho on 5/12/14.
 */
public class BattleFragment extends Fragment{
    DishBattleView first, second;
    Button battleNoneButton;
    FrameLayout layout1, layout2;
    BattleItem item;
    RelativeLayout mainLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        Log.i("BattleFragment", "BattleFragment onCreateView Started");

        mainLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_battle, container, false);

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
                Log.i("BattleFragment/onClickListener", "First Dish Selected " +item.getFirst());
                ((BattleActivity)getActivity()).loadNextItem(item);

            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                second.showThumbsUp();
                item.setResult(BattleItem.SECOND);
                Log.i("BattleFragment/onClickListener", "Second Dish Selected " +item.getSecond());
                ((BattleActivity)getActivity()).loadNextItem(item);
            }
        });
    }

    private View.OnClickListener setBattleNoneButtonClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setResult(BattleItem.NONE);
                Log.i("BattleFragment/onClickListener", "No Dish Selected");
                ((BattleActivity)getActivity()).loadNextItem(item);
            }
        };
    }



}
