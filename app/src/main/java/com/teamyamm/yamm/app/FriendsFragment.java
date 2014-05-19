package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 5/19/14.
 */
public class FriendsFragment extends Fragment {
    ListView yammTeamList, yammFriendsList;
    YammItemsListAdapter yammFriendsListAdapter, yammTeamListAdapter;
    LinearLayout layout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.friends_fragment, container, false);

        yammTeamList = (ListView) layout.findViewById(R.id.yamm_team_list);
        yammFriendsList = (ListView) layout.findViewById(R.id.yamm_friends_list);
        setYammTeamList();
        setYammFriendsList();



        return layout;
    }

    private void setYammTeamList(){
        TextView tv = (TextView) layout.findViewById(R.id.team_list_empty_text);

        //Set Empty TextView
        tv.setText(getString(R.string.team_list_empty));
        yammTeamList.setEmptyView(tv);

        //Set Team Adapter
        yammTeamListAdapter = new YammItemsListAdapter(getActivity(),loadTeamList());
        yammTeamList.setAdapter(yammTeamListAdapter);

    }

    private void setYammFriendsList(){
        TextView tv = (TextView) layout.findViewById(R.id.friends_list_empty_text);

        //Set Empty TextView
        tv.setText(getString(R.string.friends_list_empty));
        yammFriendsList.setEmptyView(tv);

        //Set Friend Adapter
        yammFriendsListAdapter = new YammItemsListAdapter(getActivity(),loadFriendsList());
        yammFriendsList.setAdapter(yammFriendsListAdapter);
    }

    private List<YammItem> loadFriendsList(){
        ArrayList<YammItem> friendsList = new ArrayList<YammItem>();

        friendsList.add(new Friend(1,"양영직"));
        friendsList.add(new Friend(2,"박지호"));
        friendsList.add(new Friend(3,"이찬"));
        friendsList.add(new Friend(4,"고서우"));

        return friendsList;
    }

    private List<YammItem> loadTeamList(){
        ArrayList<YammItem> teamList = new ArrayList<YammItem>();

        return teamList;
    }
}
