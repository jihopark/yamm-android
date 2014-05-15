package com.teamyamm.yamm.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 5/15/14.
 */
public class StreamListAdapter extends BaseAdapter {
    private List<DishItem> items = new ArrayList<DishItem>();
    private Context mContext;

    public StreamListAdapter(Context context){
        mContext = context;
    }

    @Override
    public long getItemId(int p){
        return items.get(p).getId();
    }

    @Override
    public DishItem getItem(int p){ return items.get(p); }

    @Override
    public int getCount(){ return items.size(); }

    public View getView(int position, View convertView, ViewGroup parent){
        DishStreamView view = null;

        return view;
    }
}
