package com.teamyamm.yamm.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 5/19/14.
 */
public class FriendsListAdapter extends BaseAdapter {
    private List<Friend> friends;
    private Context context;

    public FriendsListAdapter(Context context){
        this.context = context;
        friends = new ArrayList<Friend>();
    }

    public FriendsListAdapter(Context context, List<Friend> list){
        this.context = context;
        friends = list;
    }


    public View getView(int position, View convertView, ViewGroup parent){
        FriendItemView view = null;

        if (convertView == null)
            view = new FriendItemView(context, getItem(position));
        else{
            view = (FriendItemView) convertView;
        }
        view.setFriend(getItem(position));
        return view;
    }


    public int getCount(){
        return friends.size();
    }

    public void addItem(Friend item){
        friends.add(item);
    }

    public Friend getItem(int p){
        return friends.get(p);
    }

    public long getItemId(int p){
        return friends.get(p).getID();
    }
}
