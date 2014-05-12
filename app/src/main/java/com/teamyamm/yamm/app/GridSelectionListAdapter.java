package com.teamyamm.yamm.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 5/10/14.
 */
public class GridSelectionListAdapter extends BaseAdapter {
    private List<GridItem> items = new ArrayList<GridItem>();
    private Context mContext;

    public GridSelectionListAdapter(Context context){
        mContext = context;
    }

    /*
    * Returns list count
    * */
    public int getCount(){
        return items.size();
    }

    public View getView(int position, View convertView, ViewGroup parent){
        GridItemView item;

        if (convertView == null)
            item = new GridItemView(mContext, getItem(position));
        else{
            item = (GridItemView) convertView;
        }
        item.setGridItem(getItem(position));

        return item;
    }


    public void addItem(GridItem item){
        items.add(item);
    }

    public GridItem getItem(int p){
        return items.get(p);
    }

    public long getItemId(int p){
        return items.get(p).getId();
    }
}