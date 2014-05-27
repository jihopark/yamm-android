package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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


    public ListView friendListView;
    private YammItemsListAdapter adapter;
    private TextView friendsListEmptyText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        yammItemLayout = (RelativeLayout) inflater.inflate(R.layout.friends_fragment, container, false);

        friendListView = new ListView(getActivity());
        friendsListEmptyText = new TextView(getActivity());
        friendsListEmptyText.setText(getActivity().getResources().getString(R.string.friends_list_empty));
        friendListView.setEmptyView(friendsListEmptyText);

        setSelectedItems();
        setYammItemList();

        yammItemLayout.addView(friendListView);
        yammItemLayout.addView(friendsListEmptyText);

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

    public void setConfirmButtonEnabled(boolean b){
        ((FriendActivity)getActivity()).setConfirmButtonEnabled(b);
    }

    private void setSelectedItems(){
        selectedItemsInteger = getActivity().getIntent().getIntegerArrayListExtra(FriendActivity.FRIEND_LIST);

        Log.i("FriendFragment/setSelectedItems","Previous List " + selectedItemsInteger);

        if (selectedItemsInteger.size()==0)
            setConfirmButtonEnabled(false);

        selectedItems = new ArrayList<YammItem>();

    }

    private void setYammItemList(){
        List<YammItem> list = loadFriendList();
        adapter = new YammItemsListAdapter(getActivity(), list);
        friendListView.setAdapter(adapter);
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

}
