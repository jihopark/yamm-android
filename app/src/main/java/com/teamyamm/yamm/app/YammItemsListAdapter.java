package com.teamyamm.yamm.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by parkjiho on 5/19/14.
 */
public class YammItemsListAdapter extends BaseAdapter {
    private List<YammItem> items;
    private Context context;
    private int contentType;

    public YammItemsListAdapter(Context context){
        this.context = context;
        items = new ArrayList<YammItem>();
    }

    public YammItemsListAdapter(Context context, List<YammItem> list, int contentType){
        this.context = context;
        items = list;
        Collections.sort(items);
        this.contentType = contentType;
    }


    public View getView(int position, View convertView, ViewGroup parent){
        YammItemView view = null;

        if (convertView == null)
            view = new YammItemView(context, getItem(position), contentType);
        else{
            view = (YammItemView) convertView;
            view.setItem(getItem(position));
        }

        return view;
    }


    public int getCount(){
        return items.size();
    }

    public void addItem(YammItem item){
        items.add(item);
    }

    public YammItem getItem(int p){
        return items.get(p);
    }

    public long getItemId(int p){
        return items.get(p).getID();
    }
}
