package com.teamyamm.yamm.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private RelativeLayout yammItemLayout;
    public ArrayList<YammItem> selectedItems;
    public ArrayList<Integer> selectedItemsInteger;

    public HeaderListView headerView;
    public List<YammItem> teamList, friendList;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        yammItemLayout = (RelativeLayout) inflater.inflate(R.layout.friends_fragment, container, false);

        headerView = new HeaderListView(getActivity());
        setSelectedItems();
        setYammItemList();
        yammItemLayout.addView(headerView);

        return yammItemLayout;
    }

    public void addSelectedItem(YammItem yammItem){
        if (!selectedItems.contains(yammItem)){
            selectedItems.add(yammItem);
            selectedItemsInteger.add(yammItem.getID());
        }
    }

    public boolean removeSelectedItem(YammItem yammItem){
        selectedItemsInteger.remove((Integer)yammItem.getID());
        return selectedItems.remove(yammItem);
    }

    public List<YammItem> getSelectedItems(){
        return selectedItems;
    }


    private void setSelectedItems(){
        selectedItemsInteger = getActivity().getIntent().getIntegerArrayListExtra(FriendActivity.FRIEND_LIST);

        Log.i("FriendFragment/setSelectedItems","Previous List " + selectedItemsInteger);

        selectedItems = new ArrayList<YammItem>();

    }

    private void setYammItemList(){
        ArrayList<String> headerList = new ArrayList<String>();
        teamList = loadTeamList();
        friendList = loadFriendList();

        //Adding Dummy Item if size is 0 for any list

        if (teamList.size() == 0){
            teamList.add(new Team(-1));
        }
        else if (friendList.size() == 0)
            friendList.add(new Friend(-1));

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
                            .inflate(getActivity().getResources().getLayout(android.R.layout.simple_list_item_1), null);

                //Set Main Text
                ((TextView) convertView.findViewById(android.R.id.text1)).setText(getSectionHeaderItem(section).toString());

                //must set BackGround Color
                convertView.setBackgroundColor(Color.WHITE);
                return convertView;


            }
        });
    }

    private List<YammItem> loadFriendList(){
        ArrayList<YammItem> friendList = new ArrayList<YammItem>();
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

        //Check for previously selected items
        for (YammItem i : friendList){
            if (selectedItemsInteger.contains(i.getID())) {
                i.setSelected(true);
                if (!selectedItems.contains(i))
                    selectedItems.add(i);
                Log.i("FriendsFragment",i.getName()+" previously selected");
            }
        }
        return friendList;
    }

    private List<YammItem> loadTeamList(){
        ArrayList<YammItem> teamList = new ArrayList<YammItem>();
        teamList.add(new Team(14, "가족"));
        teamList.add(new Team(15, "얌팀"));
        teamList.add(new Team(16, "민사12기"));
        teamList.add(new Team(17, "맛집투어"));
        teamList.add(new Team(18, "맛집투어"));
        teamList.add(new Team(19, "맛집투어"));
        teamList.add(new Team(20, "맛집투어"));
        teamList.add(new Team(21, "맛집투어"));
        teamList.add(new Team(22, "맛집투어"));

        //Check for previously selected items
        for (YammItem i : teamList){
            if (selectedItemsInteger.contains(i.getID())) {
                i.setSelected(true);
                if (!selectedItems.contains(i))
                    selectedItems.add(i);
                Log.i("FriendsFragment",i.getName()+" previously selected ");
            }
        }

        return teamList;
    }

}
