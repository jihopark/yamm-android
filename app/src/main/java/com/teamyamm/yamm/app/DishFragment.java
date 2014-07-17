package com.teamyamm.yamm.app;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by parkjiho on 7/17/14.
 */
public class DishFragment extends Fragment {

    private LinearLayout main_layout;
    private DishItem item;
    private int itemID;
    private Button searchMap, pokeFriend;
    private boolean isGroup;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main_layout = (LinearLayout) inflater.inflate(R.layout.fragment_dish, container, false);

        isGroup = this.getArguments().getBoolean("isGroup");

        loadDish();
        setButton();

        return main_layout;
    }

    public DishItem getDishItem(){
        return new DishItem(itemID, "짜장면");
        //return item;
    }

    private void loadDish(){
        itemID = this.getArguments().getInt("dish");
        //load Dish from Server
    }

    private void setButton(){
        searchMap = (Button) main_layout.findViewById(R.id.search_map_button);

        pokeFriend = (Button) main_layout.findViewById(R.id.poke_friend_button);

        if (isGroup)
            pokeFriend.setText("메뉴 선택");
        else
            pokeFriend.setText("친구랑 같이 먹기");


        pokeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment pokeMethodDialog = new PokeMethodDialog();
                pokeMethodDialog.show(getChildFragmentManager(), "pokeMethod");
            }
        });
    }
}