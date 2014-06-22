package com.teamyamm.yamm.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.TransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;

import java.util.HashMap;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;


/**
 * Created by parkjiho on 5/7/14.
 */
public class BaseActivity extends ActionBarActivity {
    protected static final String packageName = "com.teamyamm.yamm.app";
    protected AlertDialog.Builder builder;
    protected AlertDialog internetAlert;
    public static String apiURL = "https://api.yamm.me";
    public final static int ANIMATION_SPEED = 100;
    public final static int SUCCESS_RESULT_CODE = 200;
    public final static int FAILURE_RESULT_CODE = 400;

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

    /*
    * returns screen width
    * */
    public int getScreenWidth(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////
    /*
    * Changes Action Bar Transparent
    * */
    protected void setActionBarTransparent(){
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0x64000000));
    }

    /*
    * Set ActionBar Back Button
    * */
    protected void setActionBarBackButton(boolean b){
        getSupportActionBar().setDisplayHomeAsUpEnabled(b);

    }


    /*
    * Saves PREVIOUS_ACTIVITY on Shared Pref and Moves to next Activity
    * */

    protected void goToActivity(Class<?> nextActivity){
        Log.v("BaseActivity/goToActivity","Going to "+nextActivity.getSimpleName());

        //Save Previous Activity
        putInPref(getSharedPreferences(packageName, MODE_PRIVATE)
                ,getString(R.string.PREVIOUS_ACTIVITY), nextActivity.getSimpleName());

        Intent intent = new Intent(getBaseContext(), nextActivity);
        startActivity(intent);
    }

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
    * Builds Progress Dialog with title & message
    * */
    protected ProgressDialog createProgressDialog(Context context, int title, int message){
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(getString(title));
        dialog.setMessage(getString(message));
        return dialog;
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

    /*
    * Converts JsonString into Hashmap
    * */
    protected HashMap<String,String> fromStringToHashMap(String s){
        if (s=="none")
            return null;

        Gson gson = new Gson();
        HashMap<String,String> map = new HashMap<String, String>();
        map = gson.fromJson(s, map.getClass());
        return map;
    }

    /*
    * Converts Object into JsonString
    * */
    protected String fromObjectToString(Object obj){
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /*
    * Converts JsonString into Object
    * */
    protected Object fromStringToObject(String s){
        if (s=="none")
            return null;

        Gson gson = new Gson();
        Object obj = new Object();
        obj = gson.fromJson(s, obj.getClass());
        return obj;
    }

    protected static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    protected String phoneNumberFormat(String phone){
        return phone.substring(0,3) + " - " + phone.substring(3,7) + " - " + phone.substring(7, phone.length());
    }

    /*
    * Login Auth Token Issues
    * */

    protected String getAuthToken(){
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
        String value = prefs.getString(getString(R.string.AUTH_TOKEN),"none");

        if (value.equals("none"))
            return null;

        return value;
    }

    protected void removeAuthToken(){
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(getString(R.string.AUTH_TOKEN));
        editor.commit();
        Log.i("BaseActivity/removeAuthToken","Auth Token Removed");
    }

    /*
    * NETWORKING ISSUE METHODS
    *
    * */

    protected RestAdapter.Log setRestAdapterLog(){
        return new RestAdapter.Log() {
            @Override
            public void log(String s) {
                Log.i("YammAPIServiceLog", s);
            }
        };
    }

    /*
    * Sets Request Header with Auth token
    * */
    public RequestInterceptor setRequestInterceptorWithToken(){
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
                String token = prefs.getString(getString(R.string.AUTH_TOKEN),"none");

                if (token == "none"){
                    Log.e("BaseActivity/setRequestInterceptor","Token does not exist");
                }

                token = "Bearer " + token;
                Log.i("BaseActivity/setRequestInterceptor", token);
                request.addHeader("Authorization", token);
            }
        };
    }



    /*
    * To show the last character of password
    * */
    protected class HiddenPassTransformationMethod implements TransformationMethod {

        private char DOT = '\u2022';

        @Override
        public CharSequence getTransformation(final CharSequence charSequence, final View view) {
            return new PassCharSequence(charSequence);
        }

        @Override
        public void onFocusChanged(final View view, final CharSequence charSequence, final boolean b, final int i,
                                   final Rect rect) {
            //nothing to do here
        }

        private class PassCharSequence implements CharSequence {

            private final CharSequence charSequence;

            public PassCharSequence(final CharSequence charSequence) {
                this.charSequence = charSequence;
            }

            @Override
            public char charAt(final int index) {
                if (index == length() - 1)
                    return charSequence.charAt(index);
                return DOT;
            }

            @Override
            public int length() {
                return charSequence.length();
            }

            @Override
            public CharSequence subSequence(final int start, final int end) {
                return new PassCharSequence(charSequence.subSequence(start, end));
            }
        }
    }

}
