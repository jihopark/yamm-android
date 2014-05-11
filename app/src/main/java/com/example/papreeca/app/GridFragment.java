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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class GridFragment extends Fragment {
    private GridSelectionListView listView;
    private GridSelectionListAdapter adapter;
    private Button gridButton;
    private ScrollView sv;
    private int numSelected = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        // Inflate the layout for this fragment
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.grid_fragment, container, false);

        listView = initGridSelectionListView();

        //In fragment, getActivity.findViewById won't work. Get layout directly from inflater instead

        sv = (ScrollView) layout.findViewById(R.id.grid_scroll_view);
        sv.addView(listView);

        gridButton = (Button) layout.findViewById(R.id.grid_button);

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
                    //numSelected checks the number of elements selected. changes text of grid_button according to numSelected
                    changeGridButtonText(v);
                    //Change Clicked Status ofGridItemView
                    ((GridItemView) v).toggle();
                }
            });
        return view;
    }

    /*
    * numSelected checks the number of elements selected. changes text of grid_button according to numSelected
    * */
    private void changeGridButtonText(View v){
        if (((GridItemView) v).getChecked()==true)
            numSelected--;
        else
            numSelected++;

        if (numSelected==0)
            gridButton.setText(getString(R.string.grid_selected_none));
        else
            gridButton.setText(getString(R.string.grid_selected_some));
    }
     /*
    * Adds Grid Item to GridSelectionListAdapter and to GridSelectionListView
    * */
    private GridSelectionListAdapter initiateAdapter(){
        adapter = new GridSelectionListAdapter(getActivity());
        adapter.addItem(new GridItem(1,"설렁탕"));
        adapter.addItem(new GridItem(2,"피자"));
        adapter.addItem(new GridItem(3,"해장국"));
        adapter.addItem(new GridItem(4,"물냉면"));
        adapter.addItem(new GridItem(5,"비빔냉면"));
        adapter.addItem(new GridItem(6,"라면"));
        adapter.addItem(new GridItem(7,"갈비탕"));
        adapter.addItem(new GridItem(8,"떡볶이"));
        adapter.addItem(new GridItem(9,"김밥"));
        adapter.addItem(new GridItem(10,"돈부리"));

        return adapter;
    }
}