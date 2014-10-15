package com.teamyamm.yamm.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.teamyamm.yamm.app.network.MixpanelController;

/**
 * Created by parkjiho on 10/15/14.
 */
public class YammFragment extends Fragment {

    private RelativeLayout main_layout;
    private ImageButton friendPickButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main_layout = (RelativeLayout) inflater.inflate(R.layout.fragment_yamm, container, false);

        setFriendPickButton();

        return main_layout;
    }

    @Override
    public void onStart(){
        super.onStart();
        friendPickButton.setEnabled(true);
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