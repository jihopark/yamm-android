package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


/**
 * Created by parkjiho on 5/12/14.
 */
public class BattleFragment extends Fragment{
    DishBattleView first, second;
    Button battleNoneButton;
    LinearLayout fragmentLayout;
    BattleItem item;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        Log.i("BattleFragment", "BattleFragment onCreateView Started");

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_battle, container, false);

        return layout;
    }

    public void setDishItemView(BattleItem i){
        item = i;
        Log.i("BattleFragment setDishItemView", "BattleFragment setDishItem Started");
        fragmentLayout = (LinearLayout) getView().findViewById(R.id.battle_fragment_layout);
        FrameLayout layout1 = (FrameLayout) fragmentLayout.findViewById(R.id.battle_layout1);
        FrameLayout layout2 = (FrameLayout) fragmentLayout.findViewById(R.id.battle_layout2);

        first = new DishBattleView(getActivity(),item.getFirst(), layout1);
        second = new DishBattleView(getActivity(),item.getSecond(), layout2);

        //Set Battle Non Button
        battleNoneButton = (Button) fragmentLayout.findViewById(R.id.battle_none_button);
        battleNoneButton.setOnClickListener(setBattleNoneButtonClickListener());


        //Set DishItemView onClickListener
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setResult(BattleItem.FIRST);
                Log.i("BattleFragment onClickListener", "First Dish Selected " +item.getFirst());
                ((BattleActivity)getActivity()).loadNextItem(item);

            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setResult(BattleItem.SECOND);
                Log.i("BattleFragment onClickListener", "Second Dish Selected " +item.getSecond());
                ((BattleActivity)getActivity()).loadNextItem(item);
            }
        });
    }

    private View.OnClickListener setBattleNoneButtonClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setResult(BattleItem.NONE);
                Log.i("BattleFragment onClickListener", "No Dish Selected");
                ((BattleActivity)getActivity()).loadNextItem(item);
            }
        };
    }

}
