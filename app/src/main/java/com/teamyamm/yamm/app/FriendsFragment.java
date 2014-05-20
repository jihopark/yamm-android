package com.teamyamm.yamm.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 5/19/14.
 */
public class FriendsFragment extends Fragment {
    public ListView yammTeamList, yammFriendsList;
    public YammItemsListAdapter yammFriendsListAdapter, yammTeamListAdapter;
    public LinearLayout layout, teamLayout, friendsLayout;
    public AutoCompleteTextView searchText;
    public GestureDetector detector;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.friends_fragment, container, false);

        teamLayout = (LinearLayout) layout.findViewById(R.id.yam_team_layout);
        friendsLayout = (LinearLayout) layout.findViewById(R.id.yam_friends_layout);
        yammTeamList = (ListView) layout.findViewById(R.id.yamm_team_list);
        yammFriendsList = (ListView) layout.findViewById(R.id.yamm_friends_list);
        searchText = (AutoCompleteTextView) layout.findViewById(R.id.yamm_item_search_text);

        //Sets Gesture Detector to close soft keyboard of search text
        setGestureDetector();

        setYammTeamList();
        setYammFriendsList();

        return layout;
    }
    /*
    * Closes keyboard if other lists is scrolled
    * */
    private void setGestureDetector(){
        detector = new GestureDetector(getActivity(), new FriendsGestureListener());
        yammTeamList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return getActivity().onTouchEvent(event);
            }
        });
        yammFriendsList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return getActivity().onTouchEvent(event);
            }
        });
    }

    private class FriendsGestureListener extends GestureDetector.SimpleOnGestureListener {
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            closeSoftKeyboard();
            return false;
        }

        public boolean onDown(MotionEvent e){
            closeSoftKeyboard();
            return false;
        }

        private void closeSoftKeyboard(){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
        }
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
        friendsList.add(new Friend(5,"방소정"));
        friendsList.add(new Friend(6,"황준식"));
        friendsList.add(new Friend(7,"임창균"));
        friendsList.add(new Friend(8,"한고은"));
        friendsList.add(new Friend(9,"한지은"));
        friendsList.add(new Friend(10,"박성호"));
        friendsList.add(new Friend(11,"박민선"));
        friendsList.add(new Friend(12,"임아람"));
        friendsList.add(new Friend(13,"김미정"));

        return friendsList;
    }

    private List<YammItem> loadTeamList(){
        ArrayList<YammItem> teamList = new ArrayList<YammItem>();


        teamList.add(new Team(1,"가족"));
        teamList.add(new Team(2,"얌팀"));
        teamList.add(new Team(3,"민사12기"));
        teamList.add(new Team(4,"맛집투어"));


        return teamList;
    }
}
