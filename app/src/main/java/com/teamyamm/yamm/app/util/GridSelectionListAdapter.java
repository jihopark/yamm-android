package com.teamyamm.yamm.app.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.teamyamm.yamm.app.pojos.GridItem;
import com.teamyamm.yamm.app.widget.GridItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by parkjiho on 5/10/14.
 */
public class GridSelectionListAdapter extends BaseAdapter {
    private List<GridItem> items = new ArrayList<GridItem>();
    private Context mContext;
    private HashMap<Integer, ArrayList<GridItemView>> itemViews;

    public GridSelectionListAdapter(Context context){
        mContext = context;
        itemViews = new HashMap<Integer, ArrayList<GridItemView>>();
    }

    /*
    * Returns list count
    * */
    public int getCount(){
        return items.size();
    }

    public View getView(int position, View convertView, ViewGroup parent){
        GridItemView item;
        if (convertView == null) {
            item = new GridItemView(mContext, getItem(position), position);

        }
        else{
            item = (GridItemView) convertView;
        }
        item.setGridItem(getItem(position), position);

        ArrayList<GridItemView> list = itemViews.get(position);
        if (list==null) {
            list = new ArrayList<GridItemView>();
            list.add(item);
            itemViews.put(position, list);
        }
        else{
            list.add(item);
        }

        return item;
    }


    public void addItem(GridItem item){
        items.add(item);
    }

    public GridItem getItem(int p){
        return items.get(p);
    }

    public GridItemView getItemView(int p, int id){
        List<GridItemView> list = itemViews.get(p);
        for (GridItemView v : list){
            Log.i("GridSelectionListAdapter/getItemView","Looking for " + id + ", and this one is " + v.getGridItem().getId());
            if (v.getGridItem().getId() == id)
                return v;
        }
        return null;
    }

    public long getItemId(int p){
        return items.get(p).getId();
    }
}