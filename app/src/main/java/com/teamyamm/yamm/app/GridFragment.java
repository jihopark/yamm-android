package com.teamyamm.yamm.app;

/**
 * Created by parkjiho on 5/10/14.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GridFragment extends Fragment {
    private GridSelectionListView listView;
    private GridSelectionListAdapter adapter;
    private LinearLayout mainLayout;

    private ScrollView sv;
    private CheckBox checkbox;
    private ArrayList<GridItem> selectedItems = new ArrayList<GridItem>();
    private GridItem vegi;
    private ProgressDialog progressDialog;
    private Button gridAllButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        // Inflate the layout for this fragment
        mainLayout = (LinearLayout) inflater.inflate(R.layout.grid_fragment, container, false);

        listView = initGridSelectionListView();

        //In fragment, getActivity.findViewById won't work. Get layout directly from inflater instead

        //Set Checkbox
        checkbox = (CheckBox) mainLayout.findViewById(R.id.grid_checkbox);
        checkbox.setChecked(false);
        checkbox.setOnCheckedChangeListener(initCheckBoxChangeListener());
        vegi = new GridItem(getResources().getInteger(R.integer.grid_vegi_id),"채식");

        return mainLayout;
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
                Log.i("GridFragment/onCheckedChanged","isChecked "+ isChecked + ", add " + vegi);
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
        //GridSelectionListView view = new GridSelectionListView(getActivity());
        GridSelectionListView view = (GridSelectionListView) mainLayout.findViewById(R.id.grid_selection_list_view);

        progressDialog = ((BaseActivity)getActivity()).createProgressDialog(getActivity(),
                R.string.join_progress_dialog_title,
                R.string.join_progress_dialog_message);
        progressDialog.show();

        view.setAdapter(initiateAdapter());
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    //Change Clicked Status ofGridItemView
                    ((GridItemView) v).toggle();
                    if (((GridItemView) v).getChecked()) {
                        Log.i("GridFragment/onItemClickListener","Add " + ((GridItemView) v).getGridItem().getName());
                        selectedItems.add(((GridItemView) v).getGridItem());
                    }
                    else {
                        Log.i("GridFragment/onItemClickListener","Removed " + ((GridItemView) v).getGridItem().getName());
                        selectedItems.remove(((GridItemView) v).getGridItem());
                    }
                }
            });
        Log.i("GridFragment/initGridSelectionListView","GridListView Initiated");
        return view;
    }

     /*
    * Adds Grid Item to GridSelectionListAdapter and to GridSelectionListView
    * */
    private GridSelectionListAdapter initiateAdapter(){
        adapter = new GridSelectionListAdapter(getActivity());

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(((BaseActivity)getActivity()).apiURL)
                .setLog(((BaseActivity)getActivity()).setRestAdapterLog())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        YammAPIService service = restAdapter.create(YammAPIService.class);

        service.getGridItems(new Callback<YammAPIService.Choices>() {
            @Override
            public void success(YammAPIService.Choices choices, Response response) {
                List<GridItem> gridItems = choices.getList();
                Log.i("GridFragment/initiateAdapter",gridItems.size() + " items loaded");
                Log.i("GridFragment/initiateAdapter",gridItems.toString());

                for (GridItem i : gridItems)
                    adapter.addItem(i);

                adapter.notifyDataSetChanged();
                listView.invalidateViews();
                progressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("GridFragment/initiateAdapter","Loading Adapter Failed");
                retrofitError.printStackTrace();

                if (retrofitError.isNetworkError())
                    Toast.makeText(getActivity(), getString(R.string.network_error_message), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getActivity(), getString(R.string.unidentified_error_message), Toast.LENGTH_LONG).show();
            }
        });

        return adapter;
    }

}