package com.teamyamm.yamm.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 5/19/14.
 */
public class FriendsFragment extends Fragment {
    public final static int YAMM = 1;
    public final static int CONTACT = 2;


    private LinearLayout yammItemLayout;
    public ArrayList<YammItem> selectedItems;
    public ArrayList<String> selectedItemsID;
    private List<YammItem> itemList;

    private RelativeLayout selectedItemsLayout;
    private TextView selectedItemsTextView;

    public ListView friendListView;
    private YammItemsListAdapter adapter;
    private TextView friendsListEmptyText;

    private int contentType = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initFragment();

        yammItemLayout = (LinearLayout) inflater.inflate(R.layout.fragment_friends, container, false);
        selectedItemsLayout = (RelativeLayout) yammItemLayout.findViewById(R.id.selected_items_layout);
        selectedItemsTextView = (TextView) yammItemLayout.findViewById(R.id.selected_items_textview);

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

    private void initFragment(){
        Bundle bundle =  this.getArguments();

        if (bundle!=null)
            contentType = bundle.getInt("contentType");
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
        ((FriendListInterface)getActivity()).setConfirmButtonEnabled(b, contentType);
    }

    /*
    * Adds selected item view to layout
    * */
    private void addSelectedItemView(YammItem yammItem){
        if (selectedItems.size() == 1)
            selectedItemsLayout.setVisibility(View.VISIBLE);
        else{
            Spannable newSpan = new SpannableString(" ");
            newSpan.setSpan(new BackgroundColorSpan(Color.TRANSPARENT),
                    0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            selectedItemsTextView.append(newSpan);
        }

        Spannable newSpan = new SpannableString(yammItem.getName());
        newSpan.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.default_color)),
                0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        selectedItemsTextView.append(newSpan);
    }

    private void removeSelectedItemView(YammItem yammItem){
        if (selectedItems.size()==1)
            selectedItemsLayout.setVisibility(View.GONE);

        selectedItemsTextView.setText("");
        for (YammItem i : selectedItems){
            if (i!=yammItem){
                Spannable newSpan = new SpannableString(i.getName());
                newSpan.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.default_color)),
                        0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                selectedItemsTextView.append(newSpan);

                newSpan = new SpannableString(" ");
                newSpan.setSpan(new BackgroundColorSpan(Color.TRANSPARENT),
                        0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                selectedItemsTextView.append(newSpan);
            }
        }
    }

    private void setSelectedItems(){
        selectedItemsID = new ArrayList<String>();
        selectedItems = new ArrayList<YammItem>();
    }

    private void setYammItemList(){
        itemList = ((FriendListInterface) getActivity()).getList(contentType);
        adapter = new YammItemsListAdapter(getActivity(), itemList, contentType);
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

    public static List<YammItem> setFriendListToYammItemList(List<Friend> list){
        List<YammItem> newList = new ArrayList<YammItem>();
        for (Friend i : list)
            newList.add(i);

        return newList;
    }

    public int getContentType(){
        return contentType;
    }
}
