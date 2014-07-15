package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by parkjiho on 5/19/14.
 */
public class FriendsFragment extends Fragment {

    private LinearLayout yammItemLayout;
    public ArrayList<YammItem> selectedItems;
    public ArrayList<String> selectedItemsID;
    private List<YammItem> itemList;
    private LinearLayout selectedItemsLayout;
    private HashMap<Long, TextView> selectedItemTextViews;

    public ListView friendListView;
    private YammItemsListAdapter adapter;
    private TextView friendsListEmptyText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        yammItemLayout = (LinearLayout) inflater.inflate(R.layout.fragment_friends, container, false);
        selectedItemsLayout = (LinearLayout) yammItemLayout.findViewById(R.id.selected_items_layout);
        selectedItemTextViews = new HashMap<Long, TextView>();

        friendListView = new ListView(getActivity());
        friendsListEmptyText = new TextView(getActivity());
        friendsListEmptyText.setText(getActivity().getResources().getString(R.string.friends_list_empty));
        friendListView.setEmptyView(friendsListEmptyText);

        setYammItemList();
        setSelectedItems();

        yammItemLayout.addView(friendListView);
        yammItemLayout.addView(friendsListEmptyText);

        return yammItemLayout;
    }

    public void addSelectedItem(YammItem yammItem){
        if (!selectedItems.contains(yammItem)){
            selectedItems.add(yammItem);
            selectedItemsID.add(Long.toString(yammItem.getID()));
            addSelectedItemView(yammItem);
        }
    }

    public boolean removeSelectedItem(YammItem yammItem){
        selectedItemsID.remove(Long.toString(yammItem.getID()));
        removeSelectedItemView(yammItem);
        return selectedItems.remove(yammItem);
    }

    public List<YammItem> getSelectedItems(){
        return selectedItems;
    }

    public void setConfirmButtonEnabled(boolean b){
        ((FriendActivity)getActivity()).setConfirmButtonEnabled(b);
    }

    /*
    * Adds selected item view to layout
    * */
    private void addSelectedItemView(YammItem yammItem){
        if (selectedItems.size() == 1)
            selectedItemsLayout.setVisibility(View.VISIBLE);

        TextView textView = new TextView(getActivity());
        textView.setText(yammItem.getName());

        selectedItemTextViews.put(yammItem.getID(), textView);
        selectedItemsLayout.addView(textView);
    }

    private void removeSelectedItemView(YammItem yammItem){
        if (selectedItems.size()==1)
            selectedItemsLayout.setVisibility(View.GONE);

        TextView textView = selectedItemTextViews.remove(yammItem.getID());
        selectedItemsLayout.removeView(textView);
    }

    private void setSelectedItems(){
        selectedItemsID = new ArrayList<String>();
        selectedItems = new ArrayList<YammItem>();
    }

    private void setYammItemList(){
        itemList = setFriendListToYammItemList(((FriendActivity) getActivity()).getFriends());
        adapter = new YammItemsListAdapter(getActivity(), itemList);
        friendListView.setAdapter(adapter);

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YammItemView itemView = (YammItemView)view;

                Log.i("FriendFragment/onItemclickListener", itemView.getItem().getName() + "이 눌러짐 @" + position);

                itemView.toggle();
            }
        });
    }

    private List<YammItem> setFriendListToYammItemList(List<Friend> list){
        List<YammItem> newList = new ArrayList<YammItem>();
        for (Friend i : list)
            newList.add(i);

        return newList;
    }
}
