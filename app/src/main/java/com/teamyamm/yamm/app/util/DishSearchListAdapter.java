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
    private List<DishItem> suggestions;

    public DishSearchListAdapter(List<DishItem> items, Context context) {
        super(context, 0);
        this.context = context;
        this.suggestions = new ArrayList<DishItem>();
        this.items = items;
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

    public DishItem checkIfDishIsPresent(String dish){
        for (DishItem i : items){
            if (dish.equals(i.getName()))
                return i;
        }
        return null;
    }

    public DishItem getItem(int p){
        try {
            return suggestions.get(p);
        }catch(IndexOutOfBoundsException e){
            Log.e("DishSearchListAdapter/getItem","Index out of bounds");
            return null;
        }
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            if (resultValue!=null) {
                String str = ((DishItem) (resultValue)).getName();
                return str;
            }
            return "";
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (DishItem dish : items) {
                    if(dish.getName().startsWith(constraint.toString()) || dish.getName().contains(constraint)){
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
