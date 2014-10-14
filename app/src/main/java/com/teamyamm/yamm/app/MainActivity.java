package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kakao.SessionCallback;
import com.kakao.exception.KakaoException;
import com.teamyamm.yamm.app.interfaces.MainFragmentInterface;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.network.YammAPIService;
import com.teamyamm.yamm.app.pojos.DishItem;
import com.teamyamm.yamm.app.pojos.Friend;
import com.teamyamm.yamm.app.pojos.LeftDrawerItem;
import com.teamyamm.yamm.app.pojos.PushContent;
import com.teamyamm.yamm.app.util.WTFExceptionHandler;
import com.teamyamm.yamm.app.util.YammLeftDrawerAdapter;
import com.teamyamm.yamm.app.widget.TutorialFragment;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends BaseActivity implements MainFragmentInterface {
    public final static int DRAWER_LOGOUT = 0;
    public final static String TUTORIAL = "tutorial";
    private boolean neutral = false;


    private HashMap<String, String> phoneNameMap;
    private List<Friend> friendsList;
    private String[] navMenuTitles;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView leftDrawer;
    private MainFragment mainFragment;
    private ReadContactAsyncTask readContactAsyncTask;

    private List<DishItem> dishItems;
    private Dialog fullScreenDialog;
    private boolean isDialogOpen = false;
    private boolean isLoading = false;
    protected boolean isLeftMenuLoaded = false;
    private ImageButton friendPickButton;
    private PushContent pushContent = null;
    private TutorialFragment tutorial;

    private static boolean isLoadingFB = false;

    private YammLeftDrawerAdapter leftDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (findViewById(android.R.id.home)!=null) {
            findViewById(android.R.id.home).setPadding((int) getResources().getDimension(R.dimen.logo_padding), 0,(int) getResources().getDimension(R.dimen.logo_padding), 0);
            Log.i("MainAcitivty/Padding","Setting Padding " + getResources().getDimension(R.dimen.logo_padding));
        }



        setLeftDrawer();
        setFriendPickButton();
    }

    @Override
    public void onStart(){
        super.onStart();

        if (!isLeftMenuLoaded){
            loadLeftMenu();
        }
        drawerLayout.closeDrawers();


        friendPickButton.setEnabled(true);
        Log.i("MainActivity/onStart","Execute Read Contact Async Task");

        readContactAsyncTask = new ReadContactAsyncTask();
        readContactAsyncTask.execute();
    }

    @Override
    public void onResume(){
        super.onResume();
        //To disable swipe open
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        checkIfPushTokenIsIssued();
    }

    @Override
    public void onPostResume(){
        super.onPostResume();
        loadDishes();

    }

    @Override
    public void onStop(){
        Log.i("MainActivity/onStop", "isLoggingOut " + isLoggingOut);
        if (!BaseActivity.isLoggingOut) {
            saveDishItemsInPref();
        }
        closeFullScreenDialog();
        drawerLayout.closeDrawers();
        if (tutorial!=null)
            tutorial.dismissAllowingStateLoss();

        readContactAsyncTask.cancel(true);

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.closeDrawers();
            return ;
        }
        goBackHome();
    }

    public Dialog getFullScreenDialog(){ return fullScreenDialog; }

    public boolean isFullScreenDialogOpen(){ return isDialogOpen; }

    public void closeFullScreenDialog(){
        if (fullScreenDialog!= null) {
            fullScreenDialog.dismiss();
            isDialogOpen = false;
        }
    }

    public void setFullScreenDialogOpen(boolean b){ isDialogOpen = b; }

    public boolean isLoading(){return isLoading;}

    public boolean shouldTutorialOpen(){
        return prefs.getBoolean(TUTORIAL, true);
    }

    public boolean isFriendLoaded(){
        String value = prefs.getString(getString(R.string.FRIEND_LIST),"none");

        return value != "none";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.invite_button_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            if (isLeftMenuLoaded)
                return true;
            makeYammToast(R.string.friend_not_loaded_message, Toast.LENGTH_SHORT);
            return false;
        }
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.friend_invite_button:
                startInviteActivity(MainActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public YammLeftDrawerAdapter getLeftDrawerAdapter(){return leftDrawerAdapter;}

    ////////////////////////////////Private Methods/////////////////////////////////////////////////
    private void saveDishItemsInPref(){
        Log.i("MainActivity/savedishItemsInPref", "List saved in Pref");

        Type type = new TypeToken<List<DishItem>>(){}.getType();


        putInPref(prefs, getString(R.string.PREV_DISHES), new Gson().toJson(dishItems, type));
    }





    private void setMainFragment(){
        if (dishItems == null){
            Log.e("MainActivity/setMainFragment","Dishes haven't loaded yet");
            return ;
        }


        Type type = new TypeToken<List<DishItem>>(){}.getType();
        FragmentTransaction tact = getSupportFragmentManager().beginTransaction();
        try {
            if (mainFragment != null) {
                tact.remove(mainFragment);
                tact.commitAllowingStateLoss();
                tact = getSupportFragmentManager().beginTransaction();
            }


            Bundle bundle = new Bundle();

            bundle.putString("dishes", new Gson().toJson(dishItems, type));
            bundle.putBoolean("isGroup", false);

            MainFragment newMainFragment = new MainFragment();
            newMainFragment.setArguments(bundle);

            if (mainFragment == null) {
                Log.i("MainActivity/setMainFragment", "Added new fragment");

                tact.add(R.id.main_layout, newMainFragment, MainFragment.MAIN_FRAGMENT);
                tact.commitAllowingStateLoss();
            } else {
                Log.i("MainActivity/setMainFragment", "Replacing previous fragment");
                tact.replace(R.id.main_layout, newMainFragment);
                tact.commitAllowingStateLoss();

            }
            mainFragment = newMainFragment;
        }catch (IllegalStateException e){
            Log.e("MainActivity/setMainFragment","Activity Destroyed");
        }
    }

    /*
    * Loads dish IDs from Server
    * Returns true if there is a new recommendation
    * */
    private void loadDishes(){

        if (fullScreenDialog==null) {
            fullScreenDialog = createFullScreenDialog(MainActivity.this, getString(R.string.dialog_main));
        }

     /*   RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiURL)
                .setLog(setRestAdapterLog())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(setRequestInterceptorWithToken())
                .build();*/

        YammAPIService service = YammAPIAdapter.getTokenService();

        restoreSavedList();

        if (mainFragment==null) {
            //If no mainfragment, show progress dialog
            fullScreenDialog.show();

            isDialogOpen = true;
            Log.d("MainActivity/loadDishes", "Dialog Opened here - 1");

            Log.i("MainActivity/loadDishes", "Set Main Fragment with previous dishes");
            setMainFragment();
        }

        isLoading = true;

        if (service==null) {
            invalidToken();
            WTFExceptionHandler.sendLogToServer(MainActivity.this, "WTF Invalid Token Error @MainActivity/loadDishes");
            return ;
        }

        service.getPersonalDishes(new Callback<List<DishItem>>() {
            @Override
            public void success(List<DishItem> items, Response response) {
                Log.i("MainActivity/getPersonalDishes", "Dishes Loaded");

                if (!isSameDishItems(items)) {
                    //if there is new list, renew hasReachedEnd
                    String key = getString(R.string.PREV_END_OF_RECOMMENDATION_PERSONAL);
                    prefs.edit().putBoolean(key, false).commit();
                    Log.i("MainActivity/loadDishes", key + " removed " + prefs.getBoolean(key, false));

                    //if there is new list, show newDialog
                    if (isDialogOpen == false) {
                        fullScreenDialog.show();
                        isDialogOpen = true;
                        Log.d("MainActivity/getPersonalDishes", "Dialog Opened here - 2");
                    }
                    isLoading = false;
                    Log.i("MainActivity/getPersonalDishes", "Different List. Init MainFragment");

                    dishItems = items;

                    MixpanelController.trackRecommendationsMixpanel(dishItems, MixpanelController.PERSONAL);

                    setMainFragment();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            makeYammToast(getString(R.string.new_recommendation_message), Toast.LENGTH_SHORT);
                        }
                    }, getResources().getInteger(R.integer.dialog_delay_duration));

                    MixpanelController.trackNewRecommendationMixpanel();
                    return;
                }
                if (isDialogOpen) {
                    // To delay Toast
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                     //       makeYammToast(R.string.no_new_recommendation_message, Toast.LENGTH_LONG);

                            closeFullScreenDialog();
                            Log.d("MainActivity/getPersonalDishes", "Dialog Dismissed here - 3");
                        }
                    }, getResources().getInteger(R.integer.dialog_delay_duration) - 2000);
                    // fullScreenDialog.dismiss();
                    // isDialogOpen = false;
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                isLoading = false;
                String msg = retrofitError.getCause().getMessage();
                if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION)) {
                    Log.e("MainActivity/getPersonalDishes", "Invalid Token, Logging out");
                    invalidToken();
                }

                Log.e("MainActivity/getPersonalDishes", "Server Error, setting saved list");
                retrofitError.printStackTrace();
                closeFullScreenDialog();
                Log.i("MainActivity/getPersonalDishes", "Dialog Dismissed here - 4");
            }
        });
    }

    private void restoreSavedList(){
        String savedList = prefs.getString(getString(R.string.PREV_DISHES), "none");

        if (!savedList.equals("none")){
            Type type = new TypeToken<List<DishItem>>(){}.getType();

            dishItems = new Gson().fromJson(savedList, type);
            if (dishItems!=null)
                Log.i("MainActivity/restoreSavedList", "Restore saved list : " + dishItems.toString());
        }
        else
            Log.i("MainActivity/restoreSavedList", "saved null");

    }

    public void changeInDishItem(List<DishItem> list){
        dishItems = list;
        Log.i("MainActivity/changeInDishItem","Dish Item changed to " + dishItems);
        saveDishItemsInPref();
    }

    private boolean isSameDishItems(List<DishItem> items){
        if (dishItems == null)
            return false;

        boolean present;

        for (DishItem i : items){
            present = false;
            for (DishItem j : dishItems){
                if (i.equals(j)){
                    present = true;
                    break;
                }
            }

            if (!present)
                return false;
        }

        return true;
    }

    private void setFriendPickButton(){
        friendPickButton = (ImageButton) findViewById(R.id.friends_pick_button);

        friendPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFriendLoaded()) {
                    makeYammToast(R.string.friend_not_loaded_message, Toast.LENGTH_LONG);
                    return;
                }
                Intent intent = new Intent(MainActivity.this, FriendActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                v.setEnabled(false); //To prevent double fire
                Log.i("MainActivity/onClick", "FriendActivity called");

                MixpanelController.trackEnteredGroupRecommendationMixpanel();

                startActivity(intent);
            }
        });
    }

    /*
    * LeftDrawer Methods
    * */

    private void setLeftDrawer(){
        navMenuTitles = getResources().getStringArray(R.array.nav_menu_titles);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (ListView) findViewById(R.id.left_drawer_menu_list);


        drawerToggle = new ActionBarDrawerToggle(
                MainActivity.this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.menu,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);


        //To enable back prssed
        drawerLayout.setFocusableInTouchMode(false);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Log.i("MainActivity/onCreate", "Drawer Initialized");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void loadLeftMenu(){
        YammAPIService service = YammAPIAdapter.getTokenService();

        if (service==null) {
            invalidToken();
            WTFExceptionHandler.sendLogToServer(MainActivity.this, "WTF Invalid Token Error @MainActivity/setLeftMenu");
            return ;
        }

        service.getUserInfo(new Callback<YammAPIService.RawInfo>() {
            @Override
            public void success(YammAPIService.RawInfo info, Response response) {
                Log.i("MainActivity/loadLeftMenu","Personal Info loaded from Server");
                setMenuList(info.name, info.phone, info.facebook_uid, info.kakao_uid);
                putInPref(prefs, USER_EMAIL, info.email);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("MainActivity/loadLeftMenu", "Fail to load personal info from Server");

            }
        });
    }

    private void setMenuList(String name, String phone, String fbUid, String kakaoUid){
        leftDrawerAdapter = new YammLeftDrawerAdapter(MainActivity.this);

        View.OnClickListener notReady = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeYammToast(R.string.left_drawer_not_ready, Toast.LENGTH_SHORT);
            }
        };

        leftDrawerAdapter.addMenuItems(new LeftDrawerItem(name, getString(R.string.left_drawer_logout), 0, null,
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        createDialog(MainActivity.this,
                                0, R.string.logout_dialog_message,
                                R.string.dialog_positive, R.string.dialog_negative,
                                setPositiveListener(), null).show();

                    }
                    private View.OnClickListener setPositiveListener(){
                        return new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                logOut();
                                dismissCurrentDialog();
                            }
                        };
                    }
                }));

        //leftDrawerAdapter.addMenuItems(new LeftDrawerItem(email, getString(R.string.left_drawer_change_pw), 1, null, notReady));
        leftDrawerAdapter.addMenuItems(new LeftDrawerItem(phone,getString(R.string.left_drawer_change_phone), 1, null, notReady));
        leftDrawerAdapter.addMenuItems(new LeftDrawerItem(getString(R.string.left_drawer_pw), getString(R.string.left_drawer_change_pw), 2, null, notReady));

        if (getRegistrationId(MainActivity.this).isEmpty())
            leftDrawerAdapter.setPushUsageMenu(false);
        else
            leftDrawerAdapter.setPushUsageMenu(true);

        if (fbUid.isEmpty())
            leftDrawerAdapter.setFBUsageMenu(false, getFBConnectHandler());
        else
            leftDrawerAdapter.setFBUsageMenu(true, getFBDisconnectHandler());

        if (kakaoUid.isEmpty())
            leftDrawerAdapter.setKakaoUsageMenu(false, notReady);
        else
            leftDrawerAdapter.setKakaoUsageMenu(true, getKakaoDisconnectHandler());


        leftDrawerAdapter.addMenuItems(new LeftDrawerItem(getString(R.string.left_drawer_help),"",6, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTutorial();
            }
        }));

        if (!CURRENT_APPLICATION_STATUS.equals(PRODUCTION)) {
      /*      leftDrawerAdapter.addMenuItems(new LeftDrawerItem("못먹는음식 다시하기", "", 5, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToActivity(GridActivity.class);
                }
            }));*/
            String status = "";
            if (CURRENT_APPLICATION_STATUS.equals(STAGING))
                status = "STAGING";
            else if (CURRENT_APPLICATION_STATUS.equals(TESTING))
                status = "TESTING";

            if (!status.isEmpty())
                leftDrawerAdapter.addMenuItems(new LeftDrawerItem(status,"",7,null));
        }


        leftDrawer.setAdapter(leftDrawerAdapter);
        leftDrawer.setSelector(new ColorDrawable(Color.TRANSPARENT));

        isLeftMenuLoaded = true;
    }

    private Session.StatusCallback statusCallback =
            new SessionStatusCallback();

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    }

    @Override
    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        super.onSessionStateChange(session, state, exception);
        if (state.isOpened() && !isLoadingFB) {
            isLoadingFB = true;
            Log.d("MainActivity/onSessionStateChange", session.getAccessToken());
            YammAPIAdapter.getFBConnectService().connectFacebook(session.getAccessToken(), new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.d("MainActivity/connectFacebook/Success", "FB Connect Successful");
                    leftDrawerAdapter.setFBUsageMenu(true, getFBDisconnectHandler());
                    makeYammToast(R.string.fb_connect_success, Toast.LENGTH_SHORT);
                    isLoadingFB = false;
                    if (Session.getActiveSession()!=null) {
                        Session.getActiveSession().close();
                    }
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.e("MainActivity/connectFacebook/Failure", "FB Connect Failure");
                    makeYammToast(R.string.fb_connect_failure, Toast.LENGTH_LONG);
                    isLoadingFB = false;
                    if (Session.getActiveSession()!=null) {
                        Session.getActiveSession().closeAndClearTokenInformation();
                    }
                }
            });
        }
    }

    private View.OnClickListener getFBConnectHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoadingFB)
                    return ;

                Session session = Session.getActiveSession();
                if (!session.isOpened() && !session.isClosed()) {
                    session.openForRead(new Session.OpenRequest(MainActivity.this)
                            .setPermissions(Arrays.asList("public_profile", "email"))
                            .setCallback(statusCallback));
                } else {
                    Session.openActiveSession(MainActivity.this, true, statusCallback);
                }
            }
        };
    }

    private View.OnClickListener getFBDisconnectHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoadingFB)
                    return ;

                isLoadingFB = true;
                YammAPIAdapter.getFBConnectService().disconnectFacebook(new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Log.d("MainActivity/disconnectFacebook/Success", "FB Disconnect Successful");
                        leftDrawerAdapter.setFBUsageMenu(false, getFBConnectHandler());
                        makeYammToast(R.string.fb_disconnect_success, Toast.LENGTH_SHORT);
                        isLoadingFB = false;
                        if (Session.getActiveSession()!=null) {
                            Session.getActiveSession().closeAndClearTokenInformation();
                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.e("MainActivity/disconnectFacebook/Failure", "FB Disconnect Failure");
                        isLoadingFB = false;
                        String msg = retrofitError.getCause().getMessage();
                        if (msg.equals(YammAPIService.YammRetrofitException.NO_OTHER_AUTHENTICATION))
                            makeYammToast(getString(R.string.no_other_authentication_error), Toast.LENGTH_LONG);
                        else
                            makeYammToast(R.string.fb_disconnect_failure, Toast.LENGTH_LONG);
                    }
                });
            }
        };
    }

  /*  private View.OnClickListener getKakaoConnectHandler() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.kakao.widget.LoginButton button = new LoginButton(MainActivity.this);
                button.setLoginSessionCallback(new SessionCallback() {
                    @Override
                    public void onSessionOpened() {
                        Log.d("MainActivity/getKakaoConnectHandler", "Kakao Session Opened : " + com.kakao.Session.getCurrentSession().getAccessToken());
                        YammAPIAdapter.getFBConnectService().connectKakao(com.kakao.Session.getCurrentSession().getAccessToken(), new Callback<String>() {
                            @Override
                            public void success(String s, Response response) {
                                Log.d("MainActivity/connecKakao/Success", "Kakao Connect Successful");
                                leftDrawerAdapter.setKakaoUsageMenu(true, getKakaoDisconnectHandler());
                                makeYammToast(R.string.kakao_connect_success, Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                                Log.e("MainActivity/connectKakao/Failure", "Kakao Connect Failure");
                                makeYammToast(R.string.kakao_connect_failure, Toast.LENGTH_SHORT);
                            }
                        });

                    }

                    @Override
                    public void onSessionClosed(KakaoException e) {
                        Log.d("MainActivity/getKakaoConnectHandler","Kakao Session Closed");
                    }
                });
                Log.d("MainActivity/getKakaoConnectHandler", button.touch + "");
            }
        };
    }*/

    private View.OnClickListener getKakaoDisconnectHandler() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YammAPIAdapter.getFBConnectService().disconnectKakao(new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Log.d("MainActivity/disconnectKakao/Success", "Kakao Disconnect Successful");
                        leftDrawerAdapter.setKakaoUsageMenu(false, null);
                        makeYammToast(R.string.kakao_disconnect_success, Toast.LENGTH_SHORT);
                        com.kakao.Session.getCurrentSession().close(new SessionCallback() {
                            @Override
                            public void onSessionOpened() {

                            }

                            @Override
                            public void onSessionClosed(KakaoException e) {
                                Log.d("MainActivity/onSessionClosed","Kakao Closed");
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.e("MainActivity/disconnectKakao/Failure", "Kakao Disconnect Failure");
                        String msg = retrofitError.getCause().getMessage();
                        if (msg.equals(YammAPIService.YammRetrofitException.NO_OTHER_AUTHENTICATION))
                            makeYammToast(getString(R.string.no_other_authentication_error), Toast.LENGTH_LONG);
                        else
                            makeYammToast(R.string.kakao_disconnect_failure, Toast.LENGTH_LONG);
                    }
                });
            }
        };
    }

        public void showTutorial(){
        drawerLayout.closeDrawers();
        tutorial = new TutorialFragment();

        FragmentManager fm = getSupportFragmentManager();
        tutorial.show(fm,"TUTORIAL");

        prefs.edit().putBoolean(TUTORIAL, false).commit();

        Log.i("MainFragmentInterface/showTutorial","Set TUTORIAL prefs to false");
    }

    /*
    * Contact Reading Methods
    * */
    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.DATA
    };

    public void readContacts(){
        phoneNameMap = new HashMap<String, String>();

        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor != null) {
            try {
                final int contactIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                long contactId;
                String displayName, phone;
                while (cursor.moveToNext()) {
                    displayName = cursor.getString(displayNameIndex);
                    phone = parsePhoneNumber(cursor.getString(phoneIndex)); //parse phone number

                    //Put into ArrayList
                    if (phone.startsWith("01")) { //figure out mobile phone numbers
                        phoneNameMap.put(phone, displayName);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        //Save HashMap
        BaseActivity.putInPref(prefs, getString(R.string.PHONE_NAME_MAP), fromHashMapToString(phoneNameMap));
        Log.i("MainActivity/readContacts", "Saved " + phoneNameMap.size() + " numbers");
    }

    private void sendContactsToServer(){
        Log.i("MainActivity/sendContactsToServer","Phone List " + phoneNameMap.keySet());

       /* RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiURL)
                .setLog(setRestAdapterLog())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(setRequestInterceptorWithToken())
                .build();

        YammAPIService service = restAdapter.create(YammAPIService.class);*/

        YammAPIService service = YammAPIAdapter.getTokenService();

        if (service==null) {
            invalidToken();
            WTFExceptionHandler.sendLogToServer(MainActivity.this, "WTF Invalid Token Error @MainActivity/sendContactsToServer");
            return ;
        }

        service.findFriendsFromPhone(new YammAPIService.RawPhones(phoneNameMap.keySet()),
                new Callback<YammAPIService.RawFriends>() {
                    @Override
                    public void success(YammAPIService.RawFriends rawFriends, Response response) {
                        friendsList = rawFriends.getFriendsList();
                        Log.i("MainActivity/sendContactsToServer","Friend List Loaded "  + friendsList);
                        setContactNames();
                        BaseActivity.putInPref(prefs, getString(R.string.FRIEND_LIST), fromFriendListToString(friendsList));
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        String msg = retrofitError.getCause().getMessage();
                        if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION)) {
                            Log.e("MainActivity/getPersonalDishes", "Invalid Token, Logging out");
                            invalidToken();
                        }
                        Log.e("MainActivity/sendContactsToServer", "Phone Sending Failed");
                    }
                });
    }

    private void setContactNames(){
        for (Friend i : friendsList){
            if (phoneNameMap.containsKey(i.getPhone())){
                i.setContactName(phoneNameMap.get(i.getPhone()));
            }
        }
    }

    public static String parsePhoneNumber(String s){
        //Remove Korean National Code
        if (s.isEmpty())
            return "";

        s = s.replace("+82","0");
        //Remove non digits
        s = s.replaceAll("\\D+","");

        return s;
    }

    public class ReadContactAsyncTask extends AsyncTask<Integer, Integer, Integer>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            friendsList = null;
            readContacts();
            sendContactsToServer();
            return 1;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }
    }
}

