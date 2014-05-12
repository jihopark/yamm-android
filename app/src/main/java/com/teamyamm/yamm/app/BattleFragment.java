package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


/**
 * Created by parkjiho on 5/12/14.
 */
public class BattleFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        Log.v("BattleFragment", "BattleFragment onCreateView Started");

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.battle_fragment, container, false);

        return layout;
    }

    @Override
    public void onStart(){
        super.onStart();

        setDishItemView();
    }

    public void setDishItemView(){
        Log.v("BattleFragment", "BattleFragment onStart Started");
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.battle_scroll_layout);
        FrameLayout layout1 = (FrameLayout) layout.findViewById(R.id.battle_layout1);
        FrameLayout layout2 = (FrameLayout) layout.findViewById(R.id.battle_layout2);

        DishItemView first = new DishItemView(getActivity(),((BattleActivity) getActivity()).currentFirstItem.getFirst(), layout1);
        Log.v("BattleFragment", "First DishItemView made");

        DishItemView second = new DishItemView(getActivity(),((BattleActivity) getActivity()).currentFirstItem.getSecond(), layout2);
        Log.v("BattleFragment", "Second DishItemView made");
    }
}
