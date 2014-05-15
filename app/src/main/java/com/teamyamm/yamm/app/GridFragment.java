package com.teamyamm.yamm.app;

/**
 * Created by parkjiho on 5/10/14.
 */

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;

public class GridFragment extends Fragment {
    private GridSelectionListView listView;
    private GridSelectionListAdapter adapter;
    private ScrollView sv;
    private CheckBox checkbox;
    private ArrayList<GridItem> selectedItems = new ArrayList<GridItem>();
    private GridItem vegi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        // Inflate the layout for this fragment
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.grid_fragment, container, false);

        listView = initGridSelectionListView();

        //In fragment, getActivity.findViewById won't work. Get layout directly from inflater instead

        //Set Checkbox
        checkbox = (CheckBox) layout.findViewById(R.id.grid_checkbox);
        checkbox.setChecked(false);
        checkbox.setOnCheckedChangeListener(initCheckBoxChangeListener());
        vegi = new GridItem(getResources().getInteger(R.integer.grid_vegi_id),"채식");

        //Set ScrollView & ListView
        sv = (ScrollView) layout.findViewById(R.id.grid_scroll_view);
        sv.addView(listView);

        //This stretches Scroll View according to its child
        sv.setFillViewport(true);

        return layout;
    }


    public ArrayList<GridItem> getSelectedItems(){
        return selectedItems;
    }


    public int getNumOfSelectedItems(){
        return selectedItems.size();
    }

    ////////////////////////////////////Private Method
    /*
    * Initiate CheckBox OnclickListener
    * */
    private CompoundButton.OnCheckedChangeListener initCheckBoxChangeListener(){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("GridFragment/onCheckedChanged","isChecked "+ isChecked + ", add " + vegi);
                if (isChecked)
                    selectedItems.add(vegi);
                else
                    selectedItems.remove(vegi);
            }
        };
    }

    /*
    * Initiate GridSelectionListView
    * */
    private GridSelectionListView initGridSelectionListView(){
        GridSelectionListView view = new GridSelectionListView(getActivity());
        view.setAdapter(initiateAdapter());
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    //Change Clicked Status ofGridItemView
                    ((GridItemView) v).toggle();
                    if (((GridItemView) v).getChecked())
                        selectedItems.add(((GridItemView) v).getGridItem());
                    else
                        selectedItems.remove(((GridItemView) v).getGridItem());
                }
            });
        return view;
    }

     /*
    * Adds Grid Item to GridSelectionListAdapter and to GridSelectionListView
    * */
    private GridSelectionListAdapter initiateAdapter(){
        adapter = new GridSelectionListAdapter(getActivity());
        TypedArray array = getResources().obtainTypedArray(R.array.grid_items);

        for (int i=1;i<=array.length();i++)
            adapter.addItem(new GridItem(i,array.getString(i-1)));

        return adapter;
    }

}