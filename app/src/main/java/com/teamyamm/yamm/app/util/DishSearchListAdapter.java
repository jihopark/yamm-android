package com.teamyamm.yamm.app.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.teamyamm.yamm.app.pojos.DishItem;
import com.teamyamm.yamm.app.widget.SearchDishItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 10/16/14.
 */
public class DishSearchListAdapter extends ArrayAdapter<DishItem> {

    private Context context;
    private List<DishItem> items;
    private ArrayList<DishItem> suggestions;

    public DishSearchListAdapter(Context context) {
        super(context, 0);
        this.context = context;
        this.suggestions = new ArrayList<DishItem>();
        loadDishes();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchDishItemView view = null;

        if (convertView == null) {
            view = new SearchDishItemView(context, getItem(position));
        }
        else{
            view = (SearchDishItemView) convertView;
            view.setItem(getItem(position));
        }
        return view;
    }

    private void loadDishes(){
        items = new ArrayList<DishItem>();
        items.add(new DishItem(181, "팟타이","맛있는"));
        items.add(new DishItem(182, "팟죽","맛있는"));
        items.add(new DishItem(183, "피자","맛있는"));
        items.add(new DishItem(184, "함박스테이크","맛있는"));

    }

    public DishItem getItem(int p){
        return suggestions.get(p);
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((DishItem)(resultValue)).getName();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (DishItem dish : items) {
                    if(dish.getName().startsWith(constraint.toString())){
                        Log.d("DishSearchListAdapter/performFiltering","Match " + constraint + " with " + dish.getName());
                        suggestions.add(dish);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<DishItem> filteredList = (ArrayList<DishItem>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (DishItem i : filteredList) {
                    add(i);
                    Log.d("DishSearchListAdapter/publishResults","Publish " + i.getName());
                }
                notifyDataSetChanged();
            }
        }
    };
}
