package com.teamyamm.yamm.app;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

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
        return item;
    }

    private void loadDish(){
        String s = this.getArguments().getString("dish");
        item = new Gson().fromJson(s, DishItem.class);

        TextView name = (TextView) main_layout.findViewById(R.id.dish_name_text);
        name.setText(item.getName());

        TextView comment = (TextView) main_layout.findViewById(R.id.dish_comment_text);
        comment.setText(item.getComment());

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

        searchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToYammFacebook();
            }
        });
    }

    private void goToYammFacebook(){
        Intent intent;

        try {
            getActivity().getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            Log.i("tried", "facebook");
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://page/251075981744124")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/yammapp")); //catches and opens a url to the desired page
        }
        startActivity(intent);
    }
}