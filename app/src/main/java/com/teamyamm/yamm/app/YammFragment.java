package com.teamyamm.yamm.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.teamyamm.yamm.app.network.MixpanelController;

/**
 * Created by parkjiho on 10/15/14.
 */
public class YammFragment extends Fragment {
    public final static int TODAY = 1;
    public final static int LUNCH = 2;
    public final static int DINNER = 3;
    public final static int DRINK = 4;



    private RelativeLayout main_layout;
    private ImageButton friendPickButton;
    private Button lunchButton, dinnerButton, todayButton, drinkButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main_layout = (RelativeLayout) inflater.inflate(R.layout.fragment_yamm, container, false);

        setFriendPickButton();
        setButtons();
        return main_layout;
    }

    @Override
    public void onStart(){
        super.onStart();
        friendPickButton.setEnabled(true);
    }

    private void setButtons(){
        lunchButton = (Button) main_layout.findViewById(R.id.today_lunch_button);
        lunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToYammActivity(LUNCH, false);
            }
        });
        dinnerButton = (Button) main_layout.findViewById(R.id.today_dinner_button);
        dinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToYammActivity(DINNER, true);
            }
        });
        todayButton = (Button) main_layout.findViewById(R.id.today_yamm_button);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToYammActivity(TODAY, true);
            }
        });
        drinkButton = (Button) main_layout.findViewById(R.id.today_drink_button);
        drinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToYammActivity(DRINK, true);
            }
        });
    }

    private void goToYammActivity(int type, boolean b){
        Intent intent = new Intent(getActivity(), YammActivity.class);
        intent.putExtra("TYPE",type);
        intent.putExtra("LOADNEWDISHES", b);
        startActivity(intent);
    }

    private void setFriendPickButton(){
        friendPickButton = (ImageButton) main_layout.findViewById(R.id.friends_pick_button);

        friendPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFriendLoaded()) {
                    ((BaseActivity)getActivity()).makeYammToast(R.string.friend_not_loaded_message, Toast.LENGTH_LONG);
                    return;
                }
                Intent intent = new Intent(getActivity(), FriendActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                v.setEnabled(false); //To prevent double fire
                Log.i("YammFragment/onClick", "FriendActivity called");

                MixpanelController.trackEnteredGroupRecommendationMixpanel();

                startActivity(intent);
            }
        });
    }

    private boolean isFriendLoaded(){
        String value = ((BaseActivity)getActivity()).prefs.getString(getString(R.string.FRIEND_LIST),"none");

        return value != "none";
    }
}