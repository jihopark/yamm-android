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
    DishItemView first, second;
    Button battleNoneButton;
    LinearLayout fragmentLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        Log.v("BattleFragment", "BattleFragment onCreateView Started");

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.battle_fragment, container, false);

        return layout;
    }

    public void setDishItemView(BattleItem i){
        Log.v("BattleFragment setDishItemView", "BattleFragment setDishItem Started");
        fragmentLayout = (LinearLayout) getView().findViewById(R.id.battle_fragment_layout);
        FrameLayout layout1 = (FrameLayout) fragmentLayout.findViewById(R.id.battle_layout1);
        FrameLayout layout2 = (FrameLayout) fragmentLayout.findViewById(R.id.battle_layout2);

        first = new DishItemView(getActivity(),i.getFirst(), layout1);
        Log.v("BattleFragment setDishItemView", "First DishItemView made " +first);

        second = new DishItemView(getActivity(),i.getSecond(), layout2);
        Log.v("BattleFragment setDishItemView", "Second DishItemView made " +second);

        //Set Battle Non Button
        battleNoneButton = (Button) fragmentLayout.findViewById(R.id.battle_none_button);
        battleNoneButton.setOnClickListener(setBattleNoneButtonClickListener());
    }

    private View.OnClickListener setBattleNoneButtonClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BattleActivity)getActivity()).switchFragment();
            }
        };
    }

}
