package com.example.papreeca.app;

/**
 * Created by parkjiho on 5/10/14.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class GridFragment extends Fragment {
    private GridSelectionListView listView;
    private GridSelectionListAdapter adapter;
    private ScrollView sv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        // Inflate the layout for this fragment
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.grid_fragment, container, false);

        listView = initGridSelectionListView();

        //In fragment, getActivity.findViewById won't work. Get layout directly from inflater instead

        sv = (ScrollView) layout.findViewById(R.id.grid_scroll_view);
        sv.addView(listView);

        //This stretches Scroll View according to its child
        sv.setFillViewport(true);

        return layout;
    }

    ////////////////////////////////////Private Method
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
                }
            });
        return view;
    }

     /*
    * Adds Grid Item to GridSelectionListAdapter and to GridSelectionListView
    * */
    private GridSelectionListAdapter initiateAdapter(){
        adapter = new GridSelectionListAdapter(getActivity());
        adapter.addItem(new GridItem(1));
        adapter.addItem(new GridItem(2));
        adapter.addItem(new GridItem(3));
        adapter.addItem(new GridItem(4));
        adapter.addItem(new GridItem(5));
        adapter.addItem(new GridItem(6));
        adapter.addItem(new GridItem(7));
        adapter.addItem(new GridItem(7));
        adapter.addItem(new GridItem(7));

        return adapter;
    }
}