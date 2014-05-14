package com.teamyamm.yamm.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by parkjiho on 5/7/14.
 */
public class BaseActivity extends ActionBarActivity {
    protected static final String packageName = "com.teamyamm.yamm.app";
    protected AlertDialog.Builder builder;
    protected AlertDialog internetAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setDefaultOrientation(); //Set Portrait Orientation for whole application

        //Set Dialog for Internet Connection
        setInternetConnectionAlert();
    }



    @Override
    protected void onResume() {
        super.onResume();

        showInternetConnectionAlert(null); //Check if Internet is connected, else Show Alert

    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////
    /*
    * Builds Alert Dialog with positive and negative buttons
    * */
    protected AlertDialog createDialog(Context context, int title, int message, int positive, int negative,
                                       DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        AlertDialog alert = builder.setPositiveButton(getString(positive),positiveListener)
                .setNegativeButton(getString(negative),negativeListener)
                .setTitle(getString(title))
                .setMessage(getString(message))
                .create();

        return alert;
    }
    /*
    * Puts String in SharedPreference
    * */
    public static void putInPref(SharedPreferences prf, String a, String b){
        SharedPreferences.Editor editor = prf.edit();
        editor.putString(a,b);
        editor.commit();
        Log.v("BaseActivity/putInPref", "key:" + a + " value:" + b + " saved");
    }
     /*
    * Hides Action Bar
    * */
    protected void hideActionBar() {
        getSupportActionBar().hide();
    }

    protected void goBackHome(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void setInternetConnectionAlert(){
        builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setTitle(R.string.internet_alert_title);
        builder.setMessage(R.string.internet_alert_message);
        internetAlert = builder.setCancelable(false)
                .setPositiveButton(R.string.internet_alert_button, null).create();
    }
    /*
    * Show Alert Box until Internet Connection is Available
    * */

     protected void showInternetConnectionAlert(View.OnClickListener listener){
        if (!checkInternetConnection()) {
            if (listener == null)
                listener = new CustomListener(internetAlert);
            internetAlert.show();
            internetAlert.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(listener);
        }
    }

    private class CustomListener implements View.OnClickListener {
        private final Dialog dialog;
        public CustomListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            Log.v("BaseActivity/CustomListener", "Listener activated");
            if (checkInternetConnection()) {
                Log.v("BaseActivity/CustomListener","Internet came back");
                dialog.dismiss();
            }
        }
    }


    /*
    * Returns TRUE if internet connection is available
    * */
    protected boolean checkInternetConnection(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /*
    * Set Portrait Orientation for whole application
    * */
    private void setDefaultOrientation(){
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
