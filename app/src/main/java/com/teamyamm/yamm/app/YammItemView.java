package com.teamyamm.yamm.app;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;



/**
 * Created by parkjiho on 5/19/14.
 */
public class YammItemView extends LinearLayout {
    private YammItem item;
    private TextView itemNameText, itemSelectedText;
    private FriendsFragment fragment;

    public YammItemView(Context context) {
        super(context);
    }

    public YammItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YammItemView(Context context, YammItem i) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        YammItemView layout = (YammItemView) inflater.inflate(R.layout.yamm_item_view, this, true);

        itemNameText = (TextView) layout.findViewById(R.id.yamm_item_name_text);
        itemSelectedText = (TextView) layout.findViewById(R.id.yamm_item_selected_text);

        fragment = (FriendsFragment) ((FriendActivity)context).getSupportFragmentManager().findFragmentByTag(FriendActivity.FRIEND_FRAGMENT);


        setItem(i);
        Log.i("YammItemView",i.getName() + "created");

        //On Touch Listener that toggles view
        //if dummy item, make non selectable
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.isDummy()) {
                    toggle();
                }
            }
        });
    }

    public void setItem(YammItem f){
        item = f;
        itemNameText.setText(item.getName());
        itemNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP,getResources().getDimension(R.dimen.yamm_item_text_size));

        if (isSelected())
            itemSelectedText.setVisibility(TextView.VISIBLE);
        else
            itemSelectedText.setVisibility(TextView.GONE);
    }

    public boolean isSelected(){
        return item.getSelected();
    }

    public void toggle(){
        item.toggle();

        //Change Visibility of Selected Text
        if (item.getSelected())
            itemSelectedText.setVisibility(TextView.VISIBLE);
        else
            itemSelectedText.setVisibility(TextView.GONE);

        //Put Selected Item to List
        if (fragment == null){
            Log.e("YammItemView/constructor","Cannot find friendfragment");
        }
        else{
            if (item.getSelected())
                fragment.addSelectedItem(item);
            else
                fragment.removeSelectedItem(item);
        }
    }
}