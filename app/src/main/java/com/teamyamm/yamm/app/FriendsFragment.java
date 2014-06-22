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
import java.util.List;

/**
 * Created by parkjiho on 5/19/14.
 */
public class FriendsFragment extends Fragment {

    private RelativeLayout yammItemLayout;
    public ArrayList<YammItem> selectedItems;
    public ArrayList<String> selectedItemsID;


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
            selectedItemsID.add(yammItem.getID());
        }
    }

    public boolean removeSelectedItem(YammItem yammItem){
        selectedItemsID.remove(yammItem.getID());
        return selectedItems.remove(yammItem);
    }

    public List<YammItem> getSelectedItems(){
        return selectedItems;
    }

    public void setConfirmButtonEnabled(boolean b){
        ((FriendActivity)getActivity()).setConfirmButtonEnabled(b);
    }

    private void setSelectedItems(){
        selectedItemsID = getActivity().getIntent().getStringArrayListExtra(FriendActivity.SELECTED_FRIEND_LIST);

        Log.i("FriendFragment/setSelectedItems", "Previous List " + selectedItemsID);

        if (selectedItemsID.size()==0)
            setConfirmButtonEnabled(false);

        selectedItems = new ArrayList<YammItem>();
    }

    private void setYammItemList(){
        List<Friend> list = ((FriendActivity) getActivity()).getFriends();

        adapter = new YammItemsListAdapter(getActivity(), setFriendListToYammItemList(list));
        friendListView.setAdapter(adapter);
    }

    private List<YammItem> setFriendListToYammItemList(List<Friend> list){
        List<YammItem> newList = new ArrayList<YammItem>();
        for (Friend i : list)
            newList.add(i);
        return newList;
    }
}
