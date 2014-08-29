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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by parkjiho on 5/7/14.
 */
public class BaseActivity extends ActionBarActivity {
    protected static final String packageName = "com.teamyamm.yamm.app";

    private static final String MIXPANEL_TOKEN = "5bebb04a41c88c1fad928b5526990d03";
    protected MixpanelAPI mixpanel;

    protected AlertDialog.Builder builder;
    protected AlertDialog internetAlert;
    protected SharedPreferences prefs;

    protected boolean isLoggingOut = false;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);

        setDefaultOrientation(); //Set Portrait Orientation for whole application
        //Set Dialog for Internet Connection
        setInternetConnectionAlert();

        YammAPIAdapter.setToken(getAuthToken());

        mixpanel =
                MixpanelAPI.getInstance(BaseActivity.this, MIXPANEL_TOKEN);

        checkPlayServices();
    }



    @Override
    protected void onResume() {
        super.onResume();
        showInternetConnectionAlert(null); //Check if Internet is connected, else Show Alert
        checkPlayServices();

    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }

    public MixpanelAPI getMixpanelAPI(){ return mixpanel; }

    protected void trackCaughtExceptionMixpanel(String where, String message){
        JSONObject props = new JSONObject();
        try {
            props.put("Where", where);
            props.put("Message", message);
        }catch(JSONException e){
            Log.e("BaseActivity/trackCaughtExceptionMixpanel","JSON Error");
        }
        mixpanel.track("Caught Exception", props);
        Log.i("BaseActivity/trackCaughtExceptionMixpanel","Caught Exception Tracked");
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
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.home_as_up_indicator);
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

    protected Dialog createFullScreenDialog(Context context, String message){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_full_screen);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_overlay)));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView tv = (TextView) dialog.findViewById(R.id.dialog_message);
        tv.setText(message);

        //ProgressBar spinner = (ProgressBar) dialog.findViewById(R.id.dialog_progress_bar);

        //spinner.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.default_color),
         //       android.graphics.PorterDuff.Mode.MULTIPLY);

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
    protected static void showSoftKeyboard(View view, Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
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

    protected void startInviteActivity(Context context){
        Intent intent = new Intent(context, InviteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_alpha_out);
    }

    /*
    * Login Auth Token Issues
    * */

    public String getAuthToken(){
        String value = prefs.getString(getString(R.string.AUTH_TOKEN),"none");

        if (value.equals("none"))
            return null;

        return value;
    }

    protected void logOut(){
        isLoggingOut = true;
        removeAuthToken();
        removePersonalData();
        Intent intent = new Intent(getBaseContext(), IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void removeAuthToken(){

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(getString(R.string.AUTH_TOKEN));
        editor.remove(PROPERTY_REG_ID);
        editor.remove(PROPERTY_APP_VERSION);
        editor.commit();

        //GCM push
        MixpanelAPI.People people = mixpanel.getPeople();
        people.clearPushRegistrationId();

        String deviceId = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);

        YammAPIAdapter.getTokenService().unregisterPushToken(deviceId, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.i("BaseActivity/removeAuthToken","Successfully unregistered Push Token");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("BaseActivity/removeAuthToken","Error in unregistering Push Token");
            }
        });


        YammAPIAdapter.setToken(null);

        Log.i("BaseActivity/removeAuthToken","Auth Token Removed");
    }

    protected void removePersonalData(){

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(getString(R.string.PHONE_NAME_MAP));
        editor.remove(getString(R.string.FRIEND_LIST));
        editor.remove(getString(R.string.PREV_DISHES));
        editor.commit();
        Log.i("BaseActivity/removeAuthToken","Phone/Friend List Removed " + prefs.getString(getString(R.string.PREV_DISHES), "none"));

    }

    protected void confirmButtonAnimation(FriendListInterface activity,
                                          Button confirm, boolean enableButtonFlag, int type){
        final FriendListInterface fActivity = activity;
        final Button fConfirm = confirm;
        final int fType = type;
        final boolean fEnableButtonFlag = enableButtonFlag;

        if (!enableButtonFlag && confirm.getVisibility() == View.VISIBLE){
            Animation slideOut = new TranslateAnimation(0, 0, 0,
                    getResources().getDimension(R.dimen.friends_list_confirm_button_height));
            slideOut.setDuration(getResources().getInteger(R.integer.confirm_button_slide_duration));
            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fConfirm.setVisibility(View.GONE);
                    fActivity.setConfirmButtonEnabled(fEnableButtonFlag, fType);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            confirm.startAnimation(slideOut);
        }
        else if (enableButtonFlag && confirm.getVisibility() == View.GONE) {
            Animation slideIn = new TranslateAnimation(0, 0,
                    getResources().getDimension(R.dimen.friends_list_confirm_button_height), 0);
            slideIn.setDuration(getResources().getInteger(R.integer.confirm_button_slide_duration));
            slideIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    fActivity.setConfirmButtonEnabled(fEnableButtonFlag, fType);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            confirm.setVisibility(View.VISIBLE);
            confirm.startAnimation(slideIn);
        }
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

    /*
    * Related to Push Services
    * */


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("BaseActivity/checkPlayServices", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    protected GoogleCloudMessaging gcm;
    protected String regid;
    protected String SENDER_ID;

    protected void registerGCM(){
        SENDER_ID = getResources().getString(R.string.gcm_project_number);

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(getApplicationContext());

            Log.i("BaseActivity/registerGCM", "Regid " + regid);

            if (regid.isEmpty()) {
                Log.i("BaseActivity/registerGCM", "Regid is empty. Receiving from GCM");

                registerInBackground();
            }
        } else {
            Log.e("BaseActivity/registerGCM", "No valid Google Play Services APK found.");
        }
    }

    protected String getRegistrationId(Context context) {
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("BaseActiviy/getRegistrationId", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("BaseActivity/getRegistrationId", "App version changed.");
            return "";
        }
        Log.i("BaseActiviy/getRegistrationId", "RegId found " + registrationId);
        return registrationId;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(getApplicationContext(), regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i("BaseActivity/registerInBackground","GCM Registeration " + msg);
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend() {

        Log.i("BaseActivity/sendRegistrationIdToBackend","Send Registration Id to Backend");

        YammAPIService service = YammAPIAdapter.getTokenService();

        String deviceId = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);

        service.registerPushToken(regid, deviceId, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.i("BaseActivity/sendRegistrationIdToBackend", "Push Token successfully sent to server");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("BaseActivity/sendRegistrationIdToBackend", "Push Token not sent to server");
            }
        });

        MixpanelAPI.People people = mixpanel.getPeople();
        people.setPushRegistrationId(regid);
        Log.i("BaseActivity/sendRegistrationIdToBackend", "Push Token successfully sent to Mixpanel");
    }

    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.i("BaseActivity/storeRegistrationId", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

}
