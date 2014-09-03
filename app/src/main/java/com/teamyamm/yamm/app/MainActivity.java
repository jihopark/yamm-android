package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActionBarDrawerToggle;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends BaseActivity implements MainFragmentInterface {
    public final static int DRAWER_LOGOUT = 0;

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
    private boolean isLeftMenuLoaded = false;
    private ImageButton friendPickButton;
    private PushContent pushContent = null;

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
    public void onPostResume(){
        super.onPostResume();
        drawerLayout.closeDrawers();
        loadDishes();
    }

    @Override
    public void onStop(){
        Log.i("MainActivity/onStop", "isLoggingOut " + isLoggingOut);
        if (!BaseActivity.isLoggingOut) {
            saveDishItemsInPref();
        }
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

    public boolean isFriendLoaded(){
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);

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

        if (mainFragment!= null){
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
            Log.i("MainActivity/setMainFragment","Added new fragment");

            tact.add(R.id.main_layout, newMainFragment, MainFragment.MAIN_FRAGMENT);
            tact.commitAllowingStateLoss();
        }
        else{
            Log.i("MainActivity/setMainFragment","Replacing previous fragment");
            tact.replace(R.id.main_layout, newMainFragment);
            tact.commitAllowingStateLoss();

        }
        mainFragment = newMainFragment;
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
        service.getPersonalDishes(new Callback<List<DishItem>>() {
            @Override
            public void success(List<DishItem> items, Response response) {
                Log.i("MainActivity/getPersonalDishes", "Dishes Loaded");

                if (!isSameDishItems(items)) {
                    //if there is new list, show newDialog
                    if (isDialogOpen == false) {
                        fullScreenDialog.show();
                        isDialogOpen = true;
                        Log.d("MainActivity/getPersonalDishes", "Dialog Opened here - 2");
                    }
                    isLoading = false;
                    Log.i("MainActivity/getPersonalDishes", "Different List. Init MainFragment");

                    dishItems = items;
                    setMainFragment();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            makeYammToast(getString(R.string.new_recommendation_message), Toast.LENGTH_SHORT);
                        }
                    }, getResources().getInteger(R.integer.dialog_delay_duration));

                    trackNewRecommendationMixpanel();
                    return;
                }
                if (isDialogOpen) {
                    // To delay Toast
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            makeYammToast(R.string.no_new_recommendation_message, Toast.LENGTH_LONG);

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

                trackEnteredGroupRecommendationMixpanel();

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

        //To disable swipe open
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //To enable back prssed
        drawerLayout.setFocusableInTouchMode(false);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Log.i("MainActivity/onCreate","Drawer Initialized");
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
        YammAPIAdapter.getTokenService().getUserInfo(new Callback<YammAPIService.RawInfo>() {
            @Override
            public void success(YammAPIService.RawInfo info, Response response) {
                Log.i("MainActivity/loadLeftMenu","Personal Info loaded from Server");
                setMenuList(info.name, info.email, info.phone);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("MainActivity/loadLeftMenu", "Fail to load personal info from Server");

            }
        });
    }

    private void setMenuList(String name, String email, String phone){

        Log.i("MainActivity/setMenuList", "Name " + name + " Email " +email);
        YammLeftDrawerAdapter adapter = new YammLeftDrawerAdapter(MainActivity.this);

        View.OnClickListener notReady = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeYammToast(R.string.left_drawer_not_ready, Toast.LENGTH_SHORT);
            }
        };

        adapter.addMenuItems(new LeftDrawerItem(name, getString(R.string.left_drawer_logout), 0, null,
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        createDialog(MainActivity.this,
                                R.string.logout_dialog_title, R.string.logout_dialog_message,
                                R.string.dialog_positive, R.string.dialog_negative,
                                setPositiveListener(), null).show();
                    }
                    private DialogInterface.OnClickListener setPositiveListener(){
                        return new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logOut();
                            }
                        };
                    }
                }));

        adapter.addMenuItems(new LeftDrawerItem(email, getString(R.string.left_drawer_change_pw), 1, null, notReady));
        adapter.addMenuItems(new LeftDrawerItem(phone,getString(R.string.left_drawer_change_phone), 2, null, notReady));

        if (getRegistrationId(MainActivity.this).isEmpty())
            adapter.addMenuItems(new LeftDrawerItem(getString(R.string.left_drawer_alarm_title),
                    getString(R.string.left_drawer_alarm_status_negative),3));
        else
            adapter.addMenuItems(new LeftDrawerItem(getString(R.string.left_drawer_alarm_title),
                    getString(R.string.left_drawer_alarm_status_positive),3));

        adapter.addMenuItems(new LeftDrawerItem(getString(R.string.left_drawer_help),"",4, notReady));

        // Should be deleted for production
        adapter.addMenuItems(new LeftDrawerItem("배틀 다시하기","", 5, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(BattleActivity.class);
            }
        }));
        adapter.addMenuItems(new LeftDrawerItem("못먹는음식 다시하기","", 6, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(GridActivity.class);
            }
        }));
        adapter.addMenuItems(new LeftDrawerItem("API Protocol 바꾸기","", 7, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = YammAPIAdapter.toggleProtocol();
                if (result == YammAPIAdapter.HTTP)
                    Toast.makeText(MainActivity.this, "H!T!T!P!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "H!T!T!P!S!", Toast.LENGTH_SHORT).show();
            }
        }));


        leftDrawer.setAdapter(adapter);
        leftDrawer.setSelector(new ColorDrawable(Color.TRANSPARENT));

        isLeftMenuLoaded = true;
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

    private void trackNewRecommendationMixpanel(){
        JSONObject props = new JSONObject();
        mixpanel.track("New Recommendation", props);
        Log.i("MainActivity/trackNewRecommendationMixpanel","New Recommendation Tracked ");

    }

    private void trackEnteredGroupRecommendationMixpanel(){
        JSONObject props = new JSONObject();
        mixpanel.track("Entered Group Recommendation", props);
        Log.i("MainActivity/trackEnteredGroupRecommendationMixpanel","Entered Group Recommendation Tracked ");
    }
}

