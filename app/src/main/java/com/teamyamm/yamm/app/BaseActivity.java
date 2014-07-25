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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.TransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;


/**
 * Created by parkjiho on 5/7/14.
 */
public class BaseActivity extends ActionBarActivity {
    protected static final String packageName = "com.teamyamm.yamm.app";
    protected AlertDialog.Builder builder;
    protected AlertDialog internetAlert;
    public static String apiURL = "http://api.yamm.me";
    public final static int ANIMATION_SPEED = 100;
    public final static int SUCCESS_RESULT_CODE = 200;
    public final static int FAILURE_RESULT_CODE = 400;
    protected SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);

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
    * Changes Action Bar Overlay
    * */
    protected void setActionBarOverlay(){
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
    }

    /*
    * Set ActionBar Back Button
    * */
    protected void setActionBarBackButton(boolean b){
        getSupportActionBar().setDisplayHomeAsUpEnabled(b);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.home_as_up_indicator);
    }


    /*
    * Saves PREVIOUS_ACTIVITY on Shared Pref and Moves to next Activity
    * */

    protected void goToActivity(Class<?> nextActivity){
        Log.v("BaseActivity/goToActivity", "Going to " + nextActivity.getSimpleName());

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
    * Converts Map into JsonString
    * */
    protected String fromHashMapToString(HashMap<String,String> map){
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    /*
    * Converts JsonString into Hashmap
    * */
    protected HashMap<String,String> fromStringToHashMap(String s){
        if (s=="none")
            return null;

        Gson gson = new Gson();
        Type typeOfDest = new TypeToken<HashMap<String,String>>() {
        }.getType();
        return gson.fromJson(s, typeOfDest);
    }

    /*
    * Converts Object into JsonString
    * */
    protected String fromFriendListToString(List<Friend> list){
        Gson gson = new Gson();
        return gson.toJson(list);
    }


    /*
    * Converts JsonString into Object
    * */
    protected List<Friend> fromStringToFriendList(String s){
        if (s=="none")
            return null;

        Gson gson = new Gson();
        Type typeOfDest = new TypeToken<List<Friend>>() {
        }.getType();

        return gson.fromJson(s, typeOfDest);
    }

    protected static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    protected String phoneNumberFormat(String phone){
        return phone.substring(0,3) + " - " + phone.substring(3,7) + " - " + phone.substring(7, phone.length());
    }

    protected void makeErrorToast(String message, int duration){
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.yamm_error_toast,
                (ViewGroup) findViewById(R.id.toast_layout));

        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        // Set the Text to show in TextView
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());

        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, getSupportActionBar().getHeight());
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    /*
    * Login Auth Token Issues
    * */

    protected String getAuthToken(){
        String value = prefs.getString(getString(R.string.AUTH_TOKEN),"none");

        if (value.equals("none"))
            return null;

        return value;
    }

    protected void removeAuthToken(){

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(getString(R.string.AUTH_TOKEN));
        editor.commit();
        Log.i("BaseActivity/removeAuthToken","Auth Token Removed");
    }

    protected void removeFriendList(){

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(getString(R.string.PHONE_NAME_MAP));
        editor.remove(getString(R.string.FRIEND_LIST));
        editor.commit();
        Log.i("BaseActivity/removeAuthToken","Phone/Friend List Removed");
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

    private void goToYammFacebook(){
        Intent intent;

        try {
            getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            Log.i("tried", "facebook");
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://page/251075981744124")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/yammapp")); //catches and opens a url to the desired page
        }
        startActivity(intent);
    }

}
