package com.teamyamm.yamm.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.network.YammAPIService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 10/15/14.
 */
public class YammFragment extends Fragment {
    public final static int TODAY = 0;
    public final static int LUNCH = 1;
    public final static int DINNER = 2;
    public final static int DRINK = 3;



    private RelativeLayout main_layout;
    private Button friendPickButton, todayButton;
    private Button lunchButton, dinnerButton, drinkButton;
    private boolean isLunchNew = true, isDinnerNew = true, isTodayNew = true, isAlcoholNew = true;

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

    @Override
    public void onResume(){
        super.onResume();
        checkIfNewRecommendation();
    }

    private void setButtons(){
        lunchButton = (Button) main_layout.findViewById(R.id.today_lunch_button);
        lunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToYammActivity(LUNCH, isLunchNew);
            }
        });
        dinnerButton = (Button) main_layout.findViewById(R.id.today_dinner_button);
        dinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToYammActivity(DINNER, isDinnerNew);
            }
        });
        todayButton = (Button) main_layout.findViewById(R.id.today_yamm_button);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToYammActivity(TODAY, isTodayNew);
            }
        });
        drinkButton = (Button) main_layout.findViewById(R.id.today_drink_button);
        drinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToYammActivity(DRINK, isAlcoholNew);
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
        friendPickButton = (Button) main_layout.findViewById(R.id.friends_pick_button);

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

    private void checkIfNewRecommendation(){
        YammAPIAdapter.getTokenService().checkIfNewSuggestion(new Callback<YammAPIService.RawCheck>() {
            @Override
            public void success(YammAPIService.RawCheck rawCheck, Response response) {
                isLunchNew = rawCheck.lunch;
                isDinnerNew = rawCheck.dinner;
                isAlcoholNew = rawCheck.alcohol;
                isTodayNew = rawCheck.today;
                Log.d("YammFragment/checkIfNewRecommendation", "IsLunchNew " + isLunchNew + " IsDinnerNew " + isDinnerNew);
                Log.d("YammFragment/checkIfNewRecommendation", "IsAlcoholNew " + isAlcoholNew + " IsTodayNew " + isTodayNew);

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("YammFragment/checkIfNewReommendation","Error in checking new recommendation");
            }
        });
    }


}