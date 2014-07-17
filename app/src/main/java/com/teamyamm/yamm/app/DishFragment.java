package com.teamyamm.yamm.app;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by parkjiho on 7/17/14.
 */
public class DishFragment extends Fragment {

    private LinearLayout main_layout;
    private DishItem item;
    private int itemID;
    private Button searchMap, pokeFriend;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main_layout = (LinearLayout) inflater.inflate(R.layout.fragment_dish, container, false);

        loadDish();
        setButton();

        TextView textView = (TextView) main_layout.findViewById(R.id.dish_fragment_text);
        textView.setText("Dish Id " + itemID);

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
        pokeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment pokeMethodDialog = new PokeMethodDialog();
                pokeMethodDialog.show(getChildFragmentManager(), "pokeMethod");
            }
        });
    }
}