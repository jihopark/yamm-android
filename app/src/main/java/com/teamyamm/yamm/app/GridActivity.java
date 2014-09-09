package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 6/1/14.
 */
public class GridActivity extends BaseActivity {
    private GridSelectionListView listView;
    private GridSelectionListAdapter adapter;

    private CheckBox checkbox;
    private ArrayList<GridItem> selectedItems = new ArrayList<GridItem>();
    private GridItem vegi;
    private Dialog progressDialog;
    private Button finishButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        initButtons();

        listView = initGridSelectionListView();

        //Set Checkbox
        checkbox = (CheckBox) findViewById(R.id.grid_checkbox);
        checkbox.setChecked(false);
        checkbox.setClickable(false);
        trackSelectingDislkeFoodMixpanel();
    }


    @Override
    public void onBackPressed() {
        View.OnClickListener positiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackHome();
                dismissCurrentDialog();
            }
        };
        createDialog(GridActivity.this, R.string.grid_dialog_title, R.string.grid_dialog_message,
                    R.string.dialog_positive, R.string.dialog_negative, positiveListener, null).show();

    }

    /*
    * Finshes GridActivity and saves&sends gridresults and go to BattleActivity
    * */
    private void finishGridActivity(){
        boolean resultSent = true;
        Log.i("GridActivity/finishGridActivity", "FinishGridActivity Started");

        //Save to Shared Pref
        String result = saveGridResult();

        //Send to Server

        if (!sendGridResult(result)){
            Log.e("Server Communication Error", "Sending Battle Results Failed");
            showInternetConnectionAlert(new CustomInternetListener(internetAlert));
            resultSent=false;
        }
    }

    /*
    * Send Grid Selected Result to server
    * Only executed right before stating Battle Activity
    * */
    private boolean sendGridResult(String s){
        //Check internet connection
        if (!checkInternetConnection()){
            return false;
        }

        YammAPIService service = YammAPIAdapter.getTokenService();

        final Dialog progressDialog;
        // Show Progress Dialog
        progressDialog = createFullScreenDialog(GridActivity.this, getString(R.string.progress_dialog_message));
        progressDialog.show();

        service.postGridItems(s, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                progressDialog.dismiss();
                Log.i("GridActivity/sendGridResults", "Sending " + s);
                goToActivity(BattleActivity.class);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                progressDialog.dismiss();
                Log.e("GridActivity/sendGridResults","Sending Error");
                retrofitError.printStackTrace();
                makeYammToast( getString(R.string.unidentified_error_message), Toast.LENGTH_LONG);
            }
        });

        return true;
    }

    /*
   * Custom Listener for Intro Activity InternetDialog
   * */
    private class CustomInternetListener implements View.OnClickListener {
        private final Dialog dialog;
        public CustomInternetListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            Log.i("GridActivity/CustomInternetListener", "Listener activated");
            if (checkInternetConnection()) {
                Log.i("GridActivity/CustomInternetListener","Internet came back");
                dialog.dismiss();
                finishGridActivity();
            }
        }
    }

    /*
   * Save Grid Selected Result to shared preferences
   * Only executed right before stating Battle Activity
   * */
    private String saveGridResult(){
        if (selectedItems.size() == 0) {
            Log.i("GridActivity/saveGridResult", "No item selected, returning blank string");
            return "";
        }

        String s = "";
        for (GridItem i : selectedItems)
            s = s +i.getId()+",";
        s = s.substring(0, s.length()-1);
        Log.i("GridActivity/saveGridResult", "Items: "+ selectedItems);
        Log.i("GridActivity/saveGridResult", "Result String: " + s);

        return s;
    }

    ////////////////////////////////////Private Method

    private void initButtons() {
        RelativeLayout vegiButton = (RelativeLayout) findViewById(R.id.grid_checkbox_container);
        finishButton = (Button) findViewById(R.id.grid_all_button);

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

                    setVegiItemsChecked();
                }
                Log.i("GridActivity/onClick", selectedItems.toString());
            }

            private void setVegiItemsChecked(){
                int[] vegiList= {0, 2, 7};  //put 돼지고기, 내장, 계란 as selected

                for (int i : vegiList) {
                    GridItemView v = adapter.getItemView(i, adapter.getItem(i).getId());
                    if (v != null && !v.getChecked()) {
                        v.toggle();
                        selectedItems.add(adapter.getItem(i));
                    }

                }

            }

        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("GridActivity/onClick", "Finishing Activity");
                finishGridActivity();
            }
        });
    }

    /*
    * Initiate GridSelectionListView
    * */
    private GridSelectionListView initGridSelectionListView(){
        GridSelectionListView view = (GridSelectionListView) findViewById(R.id.grid_selection_list_view);

        progressDialog = createFullScreenDialog(GridActivity.this, getString(R.string.progress_dialog_message));
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
                Log.i("GridActivity/OnItemClick",selectedItems.toString());
            }
        });
        Log.i("GridActivity/initGridSelectionListView","GridListView Initiated");
        return view;
    }

    /*
   * Adds Grid Item to GridSelectionListAdapter and to GridSelectionListView
   * */
    private GridSelectionListAdapter initiateAdapter(){
        adapter = new GridSelectionListAdapter(this);

        String[] itemNames = getResources().getStringArray(R.array.grid_item_names);
        int[] itemIds =  getResources().getIntArray(R.array.grid_item_ids);

        for (int i=0; i<itemIds.length; i++)
            adapter.addItem(new GridItem(itemIds[i], itemNames[i]));

        adapter.notifyDataSetChanged();
        progressDialog.dismiss();

        return adapter;
    }

    private void trackSelectingDislkeFoodMixpanel(){
        JSONObject props = new JSONObject();
        mixpanel.track("Selecting Dislike Food", props);
        Log.i("GridActivity/trackSelectingDislkeFoodMixpanell","Selecting Dislike Food Tracked ");
    }
}
