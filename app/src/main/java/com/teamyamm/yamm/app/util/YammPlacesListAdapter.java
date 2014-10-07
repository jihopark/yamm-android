package com.teamyamm.yamm.app.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.teamyamm.yamm.app.pojos.YammPlace;
import com.teamyamm.yamm.app.widget.YammPlaceView;

import java.util.List;

/**
 * Created by parkjiho on 10/7/14.
 */
public class YammPlacesListAdapter extends BaseAdapter {
    private Context context;
    private List<YammPlace> places;

    public YammPlacesListAdapter(Context context, List<YammPlace> places){
        this.context = context;
        this.places = places;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        YammPlaceView view = null;

        if (convertView == null) {
            view = new YammPlaceView(context, getItem(position));
        }
        else{
            view = (YammPlaceView) convertView;
            view.setItem(getItem(position));
        }
        return view;
    }

    public YammPlace getItem(int p){
        return places.get(p);
    }

    public long getItemId(int p){
        if (p <= places.size())
            return places.get(p).id;
        return 0;
    }

    public int getCount(){
        return places.size();
    }

}
