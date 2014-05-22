package com.teamyamm.yamm.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applidium.headerlistview.HeaderListView;
import com.applidium.headerlistview.SectionAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by parkjiho on 5/19/14.
 */
public class FriendsFragment extends Fragment {
    public ListView yammItemList;
    public YammItemsListAdapter yammItemListAdapter;
    public LinearLayout layout, teamLayout;
    RelativeLayout yammItemLayout;
//    public AutoCompleteTextView searchText;
    public GestureDetector detector;

    public HeaderListView headerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.friends_fragment, container, false);

        yammItemList = (ListView) layout.findViewById(R.id.yamm_item_list);

        headerView = new HeaderListView(getActivity());
        yammItemLayout = (RelativeLayout) layout.findViewById(R.id.yamm_item_list_layout);
//      searchText = (AutoCompleteTextView) layout.findViewById(R.id.yamm_item_search_text);
//      searchText Deleted for later

        //Sets Gesture Detector to close soft keyboard of search text
        //searchText Deleted for later
//        setGestureDetector();

        setYammItemList();
        yammItemLayout.addView(headerView);

        return layout;
    }

    //searchText Deleted for later
    /*
    * Closes keyboard if other lists is scrolled
    * */
   /* private void setGestureDetector(){
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
    }*/

    /*private class FriendsGestureListener extends GestureDetector.SimpleOnGestureListener {
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
    }*/

    private void setYammItemList(){
        TextView tv = (TextView) layout.findViewById(R.id.item_list_empty_text);

        //Set Empty TextView
        tv.setText(getString(R.string.item_friends_list_empty));
        yammItemList.setEmptyView(tv);

        //Set Team Adapter
        yammItemListAdapter = new YammItemsListAdapter(getActivity(),loadItemList());
        yammItemList.setAdapter(yammItemListAdapter);

        /////////////////////////////////////////////////////////
        ArrayList<YammItem> teamList = new ArrayList<YammItem>();
        ArrayList<YammItem> friendList = new ArrayList<YammItem>();
        ArrayList<String> headerList = new ArrayList<String>();

        teamList.add(new Team(1, "가족"));
        teamList.add(new Team(2, "얌팀"));
        teamList.add(new Team(3, "민사12기"));
        teamList.add(new Team(4, "맛집투어"));
        friendList.add(new Friend(1, "양영직"));
        friendList.add(new Friend(2, "박지호"));
        friendList.add(new Friend(3, "김홍"));
        friendList.add(new Friend(4, "고서우"));
        friendList.add(new Friend(5, "방소정"));
        friendList.add(new Friend(6, "황준식"));
        friendList.add(new Friend(7, "임창균"));
        friendList.add(new Friend(8, "한고은"));
        friendList.add(new Friend(9, "한지은"));
        friendList.add(new Friend(10, "박성호"));
        friendList.add(new Friend(11, "박민선"));
        friendList.add(new Friend(12, "임아람"));
        friendList.add(new Friend(13, "김미정"));
        Collections.sort(friendList);
        headerList.add(getString(R.string.yamm_list_header_1));
        headerList.add(getString(R.string.yamm_list_header_2));

        headerView.setAdapter(new SectionAdapter(getActivity(), teamList, friendList, headerList) {
            @Override
            public int getSectionHeaderViewTypeCount() {
                return 2;
            }

            @Override
            public int getSectionHeaderItemViewType(int section) {
                return section % 2;
            }

            @Override
            public View getRowView(int section, int row, View convertView, ViewGroup parent) {
                YammItemView view = null;
                Log.i("SectionAdapter","adapter");
                if (convertView == null)
                    view = new YammItemView(context, (YammItem) getRowItem(section,row));
                else{
                    view = (YammItemView) convertView;
                }
                view.setItem((YammItem) getRowItem(section,row));

                return view;
            }

            @Override
            public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getActivity().getLayoutInflater()
                            .inflate(getActivity().getResources().getLayout(android.R.layout.simple_list_item_2), null);

                //Set Main Text
                ((TextView) convertView.findViewById(android.R.id.text1)).setText(getSectionHeaderItem(section).toString());

                //Set Sub Text if empty

                if (section == 0 && numberOfRows(section)==0) {
                    ((TextView) convertView.findViewById(android.R.id.text2))
                            .setText(getActivity().getResources().getString(R.string.item_team_list_empty));
                }
                else if (section == 1 && numberOfRows(section)==0)
                    ((TextView) convertView.findViewById(android.R.id.text2))
                            .setText(getActivity().getResources().getString(R.string.item_friends_list_empty));

                //must set BackGround Color
                convertView.setBackgroundColor(Color.WHITE);
                return convertView;


            }
        });
    }

    private List<YammItem> loadItemList(){
        ArrayList<YammItem> itemList = new ArrayList<YammItem>();

        itemList.add(new Team(1,"가족"));
        itemList.add(new Team(2,"얌팀"));
        itemList.add(new Team(3,"민사12기"));
        itemList.add(new Team(4,"맛집투어"));
        itemList.add(new Friend(1, "양영직"));
        itemList.add(new Friend(2, "박지호"));
        itemList.add(new Friend(3, "이찬"));
        itemList.add(new Friend(4, "고서우"));
        itemList.add(new Friend(5, "방소정"));
        itemList.add(new Friend(6, "황준식"));
        itemList.add(new Friend(7, "임창균"));
        itemList.add(new Friend(8, "한고은"));
        itemList.add(new Friend(9, "한지은"));
        itemList.add(new Friend(10, "박성호"));
        itemList.add(new Friend(11, "박민선"));
        itemList.add(new Friend(12, "임아람"));
        itemList.add(new Friend(13, "김미정"));

        Collections.sort(itemList);

        return itemList;
    }
}
