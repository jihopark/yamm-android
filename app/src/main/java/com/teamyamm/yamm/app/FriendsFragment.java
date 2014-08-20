package com.teamyamm.yamm.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        newSpan.setSpan(new NameSpan(getActivity().getApplicationContext()),
                        0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        selectedItemsTextView.append(newSpan);
    }

    private void removeSelectedItemView(YammItem yammItem){
        int count = 0;

        if (selectedItems.size()==1)
            selectedItemsLayout.setVisibility(View.GONE);

        selectedItemsTextView.setText("");
        for (YammItem i : selectedItems){
            if (i!=yammItem){
                Spannable newSpan = new SpannableString(i.getName());
                newSpan.setSpan(new NameSpan(getActivity().getApplicationContext()),
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

    private static class NameSpan extends ReplacementSpan {

        private int squareSize, textSize;
        private float x_padding, y_padding, round;
        private float lineSpacing, height;
        private Resources r;

        public NameSpan(Context context) {
            r = context.getResources();
            x_padding = r.getDimension(R.dimen.selected_item_x_padding);
            y_padding = r.getDimension(R.dimen.selected_item_y_padding);
            round = r.getDimension(R.dimen.selected_item_round);
            lineSpacing = r.getDimension(R.dimen.selected_item_line_spacing_plus_padding);
            height = r.getDimension(R.dimen.selected_item_height);
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            textSize = (int)paint.measureText(text, start, end);
            squareSize = (int) (textSize + x_padding*2);
            return squareSize;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            RectF rect = new RectF((int) x, top,
                    (int) (x + textSize + 2 * x_padding), bottom - lineSpacing + y_padding*2);

            paint.setColor(r.getColor(R.color.selected_item_color));

            canvas.drawRoundRect(rect, round, round, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText(text, start, end, x + x_padding , y + y_padding , paint);

        }

    }
}
