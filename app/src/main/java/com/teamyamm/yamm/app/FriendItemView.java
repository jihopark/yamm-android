package com.teamyamm.yamm.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by parkjiho on 5/19/14.
 */
public class FriendItemView extends LinearLayout {
    Friend friend;
    TextView friendNameText;

    public FriendItemView(Context context) {
        super(context);
    }

    public FriendItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FriendItemView(Context context, Friend f) {
        super(context);
        friend = f;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.friend_item_view, this, true);

        friendNameText = (TextView) layout.findViewById(R.id.friend_name_text);

        friendNameText.setText(f.getName());
    }

    public void setFriend(Friend f){
        friend = f;
        friendNameText.setText(friend.getName());
    }
}