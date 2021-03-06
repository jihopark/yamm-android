package com.teamyamm.yamm.app;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.teamyamm.yamm.app.interfaces.FriendListInterface;
import com.teamyamm.yamm.app.pojos.Friend;
import com.teamyamm.yamm.app.pojos.YammItem;
import com.teamyamm.yamm.app.util.YammItemsListAdapter;
import com.teamyamm.yamm.app.widget.IndexableListView;
import com.teamyamm.yamm.app.widget.NameSpan;
import com.teamyamm.yamm.app.widget.YammItemView;

import java.lang.reflect.Field;
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

    public IndexableListView friendListView;
    private YammItemsListAdapter adapter;
    private TextView friendsListEmptyText;

    private int contentType = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initFragment();

        yammItemLayout = (LinearLayout) inflater.inflate(R.layout.fragment_friends, container, false);
        selectedItemsLayout = (RelativeLayout) yammItemLayout.findViewById(R.id.selected_items_layout);
        selectedItemsTextView = (TextView) yammItemLayout.findViewById(R.id.selected_items_textview);

        friendListView = new IndexableListView(getActivity());
        friendListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        friendListView.setDivider(new ColorDrawable(getResources().getColor(R.color.divider_color)));
        friendListView.setDividerHeight((int) getResources().getDimension(R.dimen.line_height));

        LinearLayout emptyLayout = (LinearLayout) yammItemLayout.findViewById(R.id.empty_view);
        Button emptyInvite = (Button) emptyLayout.findViewById(R.id.empty_invite_button);
        emptyInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof BaseActivity) {
                    ((BaseActivity)getActivity()).startInviteActivity(getActivity());
                }
            }
        });
        friendListView.setEmptyView(emptyLayout);
        friendListView.setFastScrollEnabled(true);

        setYammItemList();
        setSelectedItems();

        yammItemLayout.addView(friendListView);

        return yammItemLayout;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
    * Adds selected item view to mainLayout
    * */
    private void addSelectedItemView(YammItem yammItem){
        TextSwitcher textSwitcher = new TextSwitcher(getActivity());


        if (selectedItems.size() == 1) {
            selectedItemsLayout.setVisibility(View.VISIBLE);
            //Add Dummy Item
            adapter.addDummyItem();
        }
        else{
            Spannable newSpan = new SpannableString(" ");
            newSpan.setSpan(new BackgroundColorSpan(Color.TRANSPARENT),
                    0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            selectedItemsTextView.append(newSpan);
        }

        Spannable newSpan = new SpannableString(yammItem.getName());
        newSpan.setSpan(new NameSpan(getResources(), getResources().getDimension(R.dimen.selected_item_x_padding)
                , getResources().getDimension(R.dimen.selected_item_y_padding), getResources().getDimension(R.dimen.selected_item_round),
                        getResources().getDimension(R.dimen.selected_item_line_spacing_plus_padding), getResources().getDimension(R.dimen.selected_item_height)),
                        0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        selectedItemsTextView.append(newSpan);
    }

    private void removeSelectedItemView(YammItem yammItem){
        int count = 0;

        if (selectedItems.size()==1) {
            selectedItemsLayout.setVisibility(View.GONE);
            adapter.removeDummyItem();
        }
        selectedItemsTextView.setText("");
        for (YammItem i : selectedItems){
            if (i!=yammItem){
                Spannable newSpan = new SpannableString(i.getName());
                newSpan.setSpan(new NameSpan(getResources(), getResources().getDimension(R.dimen.selected_item_x_padding)
                                , getResources().getDimension(R.dimen.selected_item_y_padding), getResources().getDimension(R.dimen.selected_item_round),
                                getResources().getDimension(R.dimen.selected_item_line_spacing_plus_padding), getResources().getDimension(R.dimen.selected_item_height)),
                        0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                selectedItemsTextView.append(newSpan);

                if (count != selectedItems.size() - 2) {
                    newSpan = new SpannableString(" ");
                    newSpan.setSpan(new BackgroundColorSpan(Color.TRANSPARENT),
                            0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    selectedItemsTextView.append(newSpan);
                }
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
