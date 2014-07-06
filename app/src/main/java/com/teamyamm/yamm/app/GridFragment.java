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
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;

public class GridFragment extends Fragment {
    private GridSelectionListView listView;
    private GridSelectionListAdapter adapter;
    private LinearLayout mainLayout;

    private ScrollView sv;
    private CheckBox checkbox;
    private ArrayList<GridItem> selectedItems = new ArrayList<GridItem>();
    private GridItem vegi;
    private ProgressDialog progressDialog;
    private Button finishButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        // Inflate the layout for this fragment
        mainLayout = (LinearLayout) inflater.inflate(R.layout.grid_fragment, container, false);

        initButtons();

        listView = initGridSelectionListView();

        //In fragment, getActivity.findViewById won't work. Get layout directly from inflater instead

        //Set Checkbox
        checkbox = (CheckBox) mainLayout.findViewById(R.id.grid_checkbox);
        checkbox.setChecked(false);
        checkbox.setClickable(false);
             //  checkbox.setOnCheckedChangeListener(initCheckBoxChangeListener());



        return mainLayout;
    }


    public ArrayList<GridItem> getSelectedItems(){
        return selectedItems;
    }


    public int getNumOfSelectedItems(){
        return selectedItems.size();
    }

    ////////////////////////////////////Private Method

    private void initButtons() {
        Button vegiButton = (Button) mainLayout.findViewById(R.id.grid_checkbox_button);
        finishButton = (Button) mainLayout.findViewById(R.id.grid_all_button);

        vegi = new GridItem(getResources().getInteger(R.integer.grid_vegi_id), "채식");

        vegiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox.isChecked()) {
                    //To change Grid All Button Text
                    if (selectedItems.size() == 1) {
                        finishButton.setText(getResources().getString(R.string.grid_all_button));
                    }
                    selectedItems.remove(vegi);
                    checkbox.setChecked(false);
                } else {
                    //To change Grid All Button Text
                    if (selectedItems.size() == 0) {
                        finishButton.setText(getResources().getString(R.string.grid_all_button_finish));
                    }
                    selectedItems.add(vegi);
                    checkbox.setChecked(true);
                }
                Log.i("GridFragment/onClick", selectedItems.toString());
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
                        //To change Grid All Button Text
                        if (selectedItems.size() == 0){
                            finishButton.setText(getResources().getString(R.string.grid_all_button_finish));
                        }

                        selectedItems.add(((GridItemView) v).getGridItem());
                    }
                    else {
                        //To change Grid All Button Text
                        if (selectedItems.size() == 1){
                            finishButton.setText(getResources().getString(R.string.grid_all_button));
                        }
                        selectedItems.remove(((GridItemView) v).getGridItem());
                    }
                    Log.i("GridFragment/OnItemClick",selectedItems.toString());
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


        ///Deleted because no need to retrieve items from server
/*        RestAdapter restAdapter = new RestAdapter.Builder()
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

                for (GridItem i : gridItems) {
                    adapter.addItem(i);
                }

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
        */

        String[] itemNames = getResources().getStringArray(R.array.grid_item_names);
        int[] itemIds =  getResources().getIntArray(R.array.grid_item_ids);

        for (int i=0; i<itemIds.length; i++)
            adapter.addItem(new GridItem(itemIds[i], itemNames[i]));

        adapter.notifyDataSetChanged();
        progressDialog.dismiss();

        return adapter;
    }

}