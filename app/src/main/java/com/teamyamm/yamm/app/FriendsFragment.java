package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by parkjiho on 5/19/14.
 */
public class FriendsFragment extends Fragment {
    ListView yammTeamList, yammFriendsList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.friends_fragment, container, false);

        yammTeamList = (ListView) layout.findViewById(R.id.yamm_team_list);
        yammFriendsList = (ListView) layout.findViewById(R.id.yamm_friends_list);
        setYammTeamList();
        setYammFriendsList();



        return layout;
    }

    private void setYammTeamList(){
        TextView tv = new TextView(getActivity());

        //Set Empty TextView
        tv.setText(getString(R.string.team_list_empty));
        yammTeamList.setEmptyView(tv);

    }

    private void setYammFriendsList(){
        TextView tv = new TextView(getActivity());

        //Set Empty TextView
        tv.setText(getString(R.string.friends_list_empty));
        yammFriendsList.setEmptyView(tv);
    }
}
