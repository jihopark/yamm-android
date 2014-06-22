package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 6/1/14.
 */
public class GridActivity extends BaseActivity {
    private GridFragment gridFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        gridFragment = (GridFragment) getSupportFragmentManager().findFragmentById(R.id.grid_fragment);
        setGridAllButton();
    }

    @Override
    public void onBackPressed() {
        goBackHome();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grid_activity_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.grid_confirm_button:
                Log.i("GridActivity/OnOptionsItemSelected","Confirm Grid Button Clicked");
                finishGridActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ////Private Methods
    private void setGridAllButton(){
        Button gridAllButton = (Button) findViewById(R.id.grid_all_button);
        gridAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("GridActivity/OnClickListener","All Button Clicked");
                if (gridFragment.getSelectedItems().size()!=0){
                    Toast.makeText(getApplicationContext(), getString(R.string.grid_all_button_message), Toast.LENGTH_LONG).show();
                }
                else {
                    finishGridActivity();
                }
            }
        });
    }

    /*
    * Finshes GridActivity and saves&sends gridresults and go to BattleActivity
    * */
    private void finishGridActivity(){
        boolean resultSent = true;
        Log.i("GridActivity/finishGridActivity", "FinishGridActivity Started");

        //Save to Shared Pref
        String result = saveGridResult(gridFragment);

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

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiURL)
                .setLog(setRestAdapterLog())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(setRequestInterceptorWithToken())
                .build();

        YammAPIService service = restAdapter.create(YammAPIService.class);

        final ProgressDialog progressDialog;
        // Show Progress Dialog
        progressDialog = createProgressDialog(this,
                R.string.battle_progress_dialog_title,
                R.string.battle_progress_dialog_message);
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
                Toast.makeText(getApplicationContext(), getString(R.string.unidentified_error_message), Toast.LENGTH_LONG).show();
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
    private String saveGridResult(GridFragment f){
        if (f.getSelectedItems().size() == 0) {
            Log.i("GridActivity/saveGridResult", "No item selected, returning blank string");
            return "";
        }

        String s = "";
        for (GridItem i : f.getSelectedItems())
            s = s +i.getId()+",";
        s = s.substring(0, s.length()-1);
        Log.i("GridActivity/saveGridResult","Grid Result Saved - "+ f.getSelectedItems());
        Log.i("GridActivity/saveGridResult", s);

        return s;
    }



}
