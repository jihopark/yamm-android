package com.example.papreeca.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by parkjiho on 5/7/14.
 */
public class BaseActivity extends Activity {
    protected static final String packageName = "com.example.papreeca.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDefaultOrientation(); //Set Portrait Orientation for whole application

    }

    @Override
    protected void onResume() {
        super.onResume();

        showInternetConnectionAlert(); //Check if Internet is connected, else Show Alert

    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

    /*
    * Show Alert Box until Internet Connection is Available
    * */
    private void showInternetConnectionAlert(){
        if (!checkInternetConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("인터넷 연결?");
            builder.setMessage("인터넷 연결이 필요합니다");

            final AlertDialog alert = builder.setCancelable(false)
                    .setPositiveButton("재시도", null)
                    .create();

            alert.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            if (checkInternetConnection())
                                alert.dismiss();
                        }
                    });
                }
            });

            alert.show();
        }
    }

    /*
    * Returns TRUE if internet connection is available
    * */
    private boolean checkInternetConnection(){
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
