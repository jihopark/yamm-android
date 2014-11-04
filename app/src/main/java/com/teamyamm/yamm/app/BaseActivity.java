package com.teamyamm.yamm.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.method.TransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kakao.SessionCallback;
import com.kakao.exception.KakaoException;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.teamyamm.yamm.app.interfaces.FriendListInterface;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.network.YammAPIService;
import com.teamyamm.yamm.app.pojos.DishItem;
import com.teamyamm.yamm.app.pojos.Friend;
import com.teamyamm.yamm.app.pojos.YammItem;
import com.teamyamm.yamm.app.util.ImageCacheManager;
import com.teamyamm.yamm.app.util.WTFExceptionHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by parkjiho on 5/7/14.
 */
public class BaseActivity extends ActionBarActivity {

    public final static Type DISH_ITEM_LIST_TYPE = new TypeToken<List<DishItem>>(){}.getType();

    public final static int KAKAO = 1;
    public final static int FB = 2;
    public final static int PW = 3;

    public static final String PRODUCTION = "production";
    public static final String TESTING = "test";
    public static final String STAGING = "staging";

    public static final String CURRENT_APPLICATION_STATUS = TESTING;

    public static final String USER_EMAIL = "USEREMAIL";

    public static final String appURL = "http://goo.gl/nJEFEq";
    public static final String packageName = "com.teamyamm.yamm.app";

    private static boolean isAppRunning;

    protected AlertDialog.Builder builder;
    protected AlertDialog internetAlert;
    public SharedPreferences prefs;

    public static boolean isLoggingOut = false;

    protected Dialog currentDialog = null;

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isClosed()) {
            Log.i("BaseActivity/onSessionStateChange", "Logged out...");
            session.closeAndClearTokenInformation();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        isAppRunning = true;

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);



        //Check If WTFException was Handled
        if (getIntent().getExtras()!=null) {
            if (getIntent().getExtras().get("error")!=null) {
                WTFExceptionHandler.sendLogToServer(BaseActivity.this, getIntent().getExtras().get("error").toString());
                makeYammToast(R.string.wtf_error_handle_message, Toast.LENGTH_LONG);
                getIntent().getExtras().clear();
            }
        }

        checkAppVersion();

        prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
        Thread.setDefaultUncaughtExceptionHandler(new WTFExceptionHandler(this, prefs));

        setDefaultOrientation(); //Set Portrait Orientation for whole application
        //Set Dialog for Internet Connection
        setInternetConnectionAlert();

        YammAPIAdapter.setToken(getAuthToken());
        YammAPIAdapter.setContext(getApplicationContext());

        if (CURRENT_APPLICATION_STATUS.equals(TESTING)) {
            MixpanelController.setMixpanel(MixpanelAPI.getInstance(BaseActivity.this, MixpanelController.MIXPANEL_TOKEN_DEVELOPMENT));
        }
        else {
            MixpanelController.setMixpanel(MixpanelAPI.getInstance(BaseActivity.this, MixpanelController.MIXPANEL_TOKEN_PRODUCTION));
        }
        MixpanelController.setMixpanelRecommendation(MixpanelAPI.getInstance(BaseActivity.this, MixpanelController.MIXPANEL_RECOMMENDATIONS_TOKEN));
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();

        showInternetConnectionAlert(null); //Check if Internet is connected, else Show Alert

        if (CURRENT_APPLICATION_STATUS.equals(PRODUCTION))
            checkPlayServices();


        isAppRunning = true;
        Log.d("BaseActivity/onResume","App is Running " + isAppRunning);
    }

    @Override
    protected void onPause(){
        super.onPause();
        isAppRunning = false;
        uiHelper.onPause();
        Log.d("BaseActivity/onPause", "App is Running " + isAppRunning);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        MixpanelController.flushAll();
        uiHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            // do nothing
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void setPaddingOnHomeAsUpIndicator(){
        if (findViewById(android.R.id.home)!=null) {
            findViewById(android.R.id.home).setPadding((int) getResources().getDimension(R.dimen.home_padding), 0,(int) getResources().getDimension(R.dimen.home_padding), 0);
            Log.i("BaseActivity/setPaddingONHomeAsUpIndicator","Setting Padding " + getResources().getDimension(R.dimen.home_padding));
        }
    }

    public static boolean checkIfAppIsRunning(){
        return isAppRunning;
    }


    private void checkAppVersion(){
        YammAPIAdapter.getService().getClientInfo(new Callback<YammAPIService.RawClientInfo>() {
            View.OnClickListener positiveListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    try {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName));
                    }
                    startActivity(intent);
                    dismissCurrentDialog();
                }
            };

            @Override
            public void success(YammAPIService.RawClientInfo rawClientInfo, Response response) {
                Log.d("BaseActivity/checkAppVersion", "Checking App Version... " + rawClientInfo.android_version);
                if (!rawClientInfo.android_version.equals(getString(R.string.app_version_name))) {
                    createDialog(BaseActivity.this, 0, R.string.check_app_version_message,
                            R.string.dialog_positive, R.string.dialog_negative, positiveListener, null).show();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
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
    protected String getPhoneNumber(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phone = manager.getLine1Number();

        if (phone==null)
            return "";
        phone = MainActivity.parsePhoneNumber(phone);

        Log.i("BaseActivity/getPhoneNumber","Read Phone Number : " + phone);

        if (phone.length() > 9){
            //Set it on Phone Text
            return phone;
        }
        return "";
    }
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
        setPaddingOnHomeAsUpIndicator();
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.home_as_up_indicator);
    }


    /*
    * Saves PREVIOUS_ACTIVITY on Shared Pref and Moves to next Activity
    * */

    public void goToActivity(Class<?> nextActivity){
        Log.d("BaseActivity/goToActivity", "Going to " + nextActivity.getSimpleName());

        //Save Previous Activity
        putInPref(getSharedPreferences(packageName, MODE_PRIVATE)
                ,getString(R.string.PREVIOUS_ACTIVITY), nextActivity.getSimpleName());

        Intent intent = new Intent(getBaseContext(), nextActivity);

        startActivity(intent);
    }


    /*
   * Builds Alert Dialog with positive and negative buttons
   * */
    protected Dialog createDialog(Context context, String title, String message, String positive, String negative,
                                  View.OnClickListener positiveListener, View.OnClickListener negativeListener){
        /*AlertDialog.Builder builder = new AlertDialog.Builder(context);

        AlertDialog alert = builder.setPositiveButton(getString(positive),positiveListener)
                .setNegativeButton(getString(negative),negativeListener)
                .setTitle(getString(title))
                .setMessage(getString(message))
                .create();*/

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_default);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView titleText = (TextView) dialog.findViewById(R.id.dialog_title);
        TextView messageText = (TextView) dialog.findViewById(R.id.dialog_message);
        Button positiveButton = (Button) dialog.findViewById(R.id.dialog_positive_button);
        Button negativeButton = (Button) dialog.findViewById(R.id.dialog_negative_button);
        ImageButton closeButton = (ImageButton) dialog.findViewById(R.id.dialog_close_button);

        if (title.equals("")){
            titleText.setBackgroundColor(getResources().getColor(R.color.dialog_content_background));
            titleText.setText("");
            messageText.setPadding(0, 0, 0, (int) (getResources().getDimension(R.dimen.custom_dialog_title_height) / 2));
        }
        else
            titleText.setText(title);

        messageText.setText(message);
        positiveButton.setText(positive);
        negativeButton.setText(negative);

        View.OnClickListener dismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissCurrentDialog();
            }
        };

        closeButton.setOnClickListener(dismissListener);

        if (positiveListener==null)
            positiveListener = dismissListener;
        if (negativeListener==null)
            negativeListener = dismissListener;

        positiveButton.setOnClickListener(positiveListener);
        negativeButton.setOnClickListener(negativeListener);

        currentDialog = dialog;

        return dialog;
    }

    protected Dialog createDialog(Context context, int title, int message, int positive, int negative,
                                  View.OnClickListener positiveListener, View.OnClickListener negativeListener) {
        if (title==0)
            return createDialog(context,"",getString(message), getString(positive),getString(negative),positiveListener,negativeListener);
        return createDialog(context,getString(title),getString(message), getString(positive),getString(negative),positiveListener,negativeListener);
    }

    protected Dialog createDialog(Context context, int title, int message, int positive,
                                  View.OnClickListener positiveListener){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_default_one_button);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView titleText = (TextView) dialog.findViewById(R.id.dialog_title);
        TextView messageText = (TextView) dialog.findViewById(R.id.dialog_message);
        Button positiveButton = (Button) dialog.findViewById(R.id.dialog_positive_button);
        ImageButton closeButton = (ImageButton) dialog.findViewById(R.id.dialog_close_button);

        if (title==0){
            titleText.setBackgroundColor(getResources().getColor(R.color.dialog_content_background));
            titleText.setText("");
            messageText.setPadding(0, 0, 0, (int) (getResources().getDimension(R.dimen.custom_dialog_title_height) / 2));
        }
        else
            titleText.setText(getString(title));

        messageText.setText(getString(message));
        positiveButton.setText(getString(positive));

        View.OnClickListener dismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissCurrentDialog();
            }
        };

        closeButton.setOnClickListener(dismissListener);

        if (positiveListener==null)
            positiveListener = dismissListener;

        positiveButton.setOnClickListener(positiveListener);

        currentDialog = dialog;

        return dialog;
    }


    protected void dismissCurrentDialog(){
        if (currentDialog==null){
            Log.e("BaseActivity/onClick", "Current Dialog null");
            return ;
        }
        currentDialog.dismiss();
        currentDialog = null;
    }

    /*
    * Builds Progress Dialog with title & message
    * */

    public Dialog createFullScreenDialog(Context context, String message){
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

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }catch(Exception e){
            Log.i("BaseActivity/hideSoftKeyboard","Exception in softkeyboard manipulation");
            e.printStackTrace();
        }
    }
    public static void showSoftKeyboard(View view, Activity activity){
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }catch(Exception e){
            Log.i("BaseActivity/showSoftKeyboard","Exception in softkeyboard manipulation");
            e.printStackTrace();
        }
    }

    public String phoneNumberFormat(String phone){
        return phone.substring(0,3) + " - " + phone.substring(3,7) + " - " + phone.substring(7, phone.length());
    }

    public void makeYammToast(int rId, int duration){
        makeYammToast(getString(rId), duration);
    }

    public void makeYammToast(String message, int duration){
        try {
            if (BaseActivity.checkIfAppIsRunning()) {
                LayoutInflater inflater = getLayoutInflater();

                View layout = inflater.inflate(R.layout.yamm_toast,
                        (ViewGroup) findViewById(R.id.toast_layout));

                TextView text = (TextView) layout.findViewById(R.id.toast_text);
                // Set the Text to show in TextView
                text.setText(message);
                Toast toast = new Toast(getApplicationContext());

                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(duration);
                toast.setView(layout);
                toast.show();
            }
        }catch(RuntimeException e){
            Log.e("BaseActivity/makeYammToast","Caught RuntimeException");
            e.printStackTrace();
        }
    }

    protected void startInviteActivity(Context context){
        Intent intent = new Intent(context, InviteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_alpha_out);
    }

    protected void recycleImageView(ImageView v){
        if (v.getDrawable() instanceof BitmapDrawable){
            ((BitmapDrawable) v.getDrawable()).getBitmap().recycle();
            Log.d("BaseActivity/recycleImageView","Recycled ImageView");
        }
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

        boolean isKakaoClosing = false;

        if (ImageCacheManager.getInstance()!=null){
            if (ImageCacheManager.getInstance().getImageCache()!=null
                    && ImageCacheManager.getInstance().getImageCache() instanceof ImageCacheManager.BitmapLruImageCache){
                ((ImageCacheManager.BitmapLruImageCache) ImageCacheManager.getInstance().getImageCache()).evictAll();
                Log.d("BaseActivity/logOut","Clear Image Cache");
            }
        }

        if (this instanceof MainActivity)
            ((MainActivity)this).isLeftMenuLoaded = false;
        try {
            if (Session.getActiveSession() != null) {
                Session.getActiveSession().closeAndClearTokenInformation();
                Session.setActiveSession(null);
                Log.d("BaseActivity/logOut", "Clear FB Session");
            }
        }catch(IllegalStateException e){
            Log.e("BaseActivity/logOut", "FB Session is invalid. Just Log out.");
        }
        try {
            if (com.kakao.Session.getCurrentSession().isOpened()) {
                isKakaoClosing = true;
                com.kakao.Session.getCurrentSession().close(new SessionCallback() {
                    @Override
                    public void onSessionOpened() {

                    }

                    @Override
                    public void onSessionClosed(KakaoException e) {
                        Log.d("BaseActivity/onSessionClosed", "Kakao Session Closed for Logout");
                        Intent intent = new Intent(getBaseContext(), IntroActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        }catch(IllegalStateException e){
            Log.e("BaseActivity/logOut", "Kakao Session is invalid. Just Log out.");
        }

        isLoggingOut = true;
        removeAuthToken();
        removePersonalData();
        if (!isKakaoClosing) {
            Intent intent = new Intent(getBaseContext(), IntroActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    protected void removeAuthToken(){

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(getString(R.string.AUTH_TOKEN));
        editor.remove(PROPERTY_REG_ID);
        editor.remove(PROPERTY_APP_VERSION);
        regid = null;
        editor.commit();

        //GCM push
        MixpanelAPI.People people = MixpanelController.mixpanel.getPeople();
        people.clearPushRegistrationId();

        String deviceId = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);

        YammAPIService service = YammAPIAdapter.getTokenService();

        if (service!=null) {
            service.unregisterPushToken(deviceId, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.i("BaseActivity/removeAuthToken", "Successfully unregistered Push Token");
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.e("BaseActivity/removeAuthToken", "Error in unregistering Push Token");
                }
            });
        }

        YammAPIAdapter.setToken(null);
    }

    protected void removePersonalData(){

        MixpanelController.logOut();

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(getString(R.string.PHONE_NAME_MAP));
        editor.remove(getString(R.string.FRIEND_LIST));
        editor.remove(getString(R.string.PREV_DISHES));

        for (String s : YammActivity.suggestionType)
            editor.remove(s);

        editor.remove(MainActivity.TUTORIAL);
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

    public void invalidToken(){
        makeYammToast(R.string.invalid_token_error, Toast.LENGTH_LONG);
        logOut();
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

    protected void startSMSIntent(String msg, List<YammItem> items){
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.putExtra("sms_body", msg);

        //Get Senders

        String separator = "; ";
        if(android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")){
            separator = ", ";
        }
        try {
            String s = "";
            //List<YammItem> items = contactFriendsFragment.getSelectedItems();
            if (items.size() == 1){
                s = ((Friend)items.get(0)).getPhone();
            }
            else {
                for (YammItem i : items){
                    s += ((Friend)i).getPhone();
                    s += separator;
                }
                s = s.substring(0, s.length() - 1);
            }
            sendIntent.setData(Uri.parse("smsto:" + s));

        } catch (Exception e) {
            makeYammToast(getString(R.string.invite_sms_error_message), Toast.LENGTH_SHORT);
            Log.e("BaseActivity/startSMSIntent","SMS Error");
            e.printStackTrace();
        }
        startActivity(sendIntent);
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

    protected void checkIfPushTokenIsIssued(){
        if (regid == null || regid.isEmpty())
            regid = getRegistrationId(getApplicationContext());
        else {
            Log.i("BaseActivity/checkIfPushTokenIsIssued", "Regid is Issued");
            return;
        }
        Log.i("BaseActivity/checkIfPushTokenIsIssued", "Regid " + regid);

        if (regid.isEmpty()) {
            Log.i("BaseActivity/checkIfPushTokenIsIssued", "Regid is empty. Receiving from GCM");
            registerGCM();
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

    protected void registerInBackground() {
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(BaseActivity.this);
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
                } catch(NullPointerException e){
                    Log.e("BaseActivity/registerInBackground","Nullpointer spotted in AsyncTask");
                    e.printStackTrace();
                    this.cancel(true);
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
        if (service==null) {
            invalidToken();
            WTFExceptionHandler.sendLogToServer(BaseActivity.this, "WTF Invalid Token Error @SendRegistrationToBackend");
            return ;
        }

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


        MixpanelAPI.People people = MixpanelController.mixpanel.getPeople();
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

    protected String friendKoreanPlural(int n){
        if (n > 1)
            return getString(R.string.poke_friend_plural);
        return getString(R.string.poke_friend_singular);
    }

    protected void setDefaultValueForSpinner(Spinner s){
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        if (hour < 14 && hour > 2){
            Log.d("BaseActivity/setDefaultValueForSpinner","Yay Lunch!");
            s.setSelection(0);
        }
        else{
            Log.d("BaseActivity/setDefaultValueForSpinner","Yay Dinner!");
            s.setSelection(1);
        }
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
