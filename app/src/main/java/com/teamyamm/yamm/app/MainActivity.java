package com.teamyamm.yamm.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends BaseActivity {
    public final static int DRAWER_LOGOUT = 0;

    public static final String loggedFirstTime = "lft";
    private boolean neutral = false;


    private HashMap<String, String> phoneNameMap;
    private List<Friend> friendsList;
    private String[] navMenuTitles;
    private DrawerLayout drawerLayout;
    private ListView leftDrawer;
    private MainFragment mainFragment;
    private ReadContactAsyncTask readContactAsyncTask;
    private SharedPreferences prefs;

    private List<DishItem> dishItems;
    private ProgressDialog dialog, newDialog;

    private Button friendPickButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setLeftDrawer();
        setFriendPickButton();

        prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
    }

    @Override
    public void onStart(){
        super.onStart();
        showFBDialog();

        loadDishes();

        friendPickButton.setEnabled(true);
        Log.i("MainActivity","Execute Read Contact Async Task");

        readContactAsyncTask = new ReadContactAsyncTask();
        readContactAsyncTask.execute();
    }

    @Override
    public void onStop(){

        Log.i("MainActivity/onDestroy", "List saved in Pref");

        Type type = new TypeToken<List<DishItem>>(){}.getType();


        putInPref(prefs, "dishes", new Gson().toJson(dishItems, type));
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        goBackHome();
    }


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
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.friend_invite_button:
                Intent intent = new Intent(MainActivity.this, InviteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////


    private void showFBDialog(){
        boolean fb = prefs.getBoolean(loggedFirstTime, false);

        Log.i("MainActivity/showFBDialog","FB boolean " + fb);

        if (fb){

            Log.i("MainActivity/showFBDialog","Show FB Dialog");

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            DialogInterface.OnClickListener positiveListener =
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(MainActivity.loggedFirstTime, false);
                            editor.commit();

                            goToYammFacebook();
                            dialog.dismiss();
                        }
                    };
            DialogInterface.OnClickListener neutralListener =
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    };

            DialogInterface.OnClickListener negativeListener =
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(MainActivity.loggedFirstTime, false);
                            editor.commit();

                            dialog.dismiss();
                        }
                    };

            AlertDialog alert = builder.setPositiveButton(R.string.fb_dialog_positive,positiveListener)
                    .setNegativeButton(R.string.fb_dialog_negative, negativeListener)
                    .setNeutralButton(R.string.fb_dialog_neutral, neutralListener)
                    .setTitle(R.string.fb_dialog_title)
                    .setMessage(R.string.fb_dialog_message)
                    .create();

            alert.show();
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

    private void setMainFragment(){
        if (dishItems == null){
            Log.e("MainActivity/setMainFragment","Dishes haven't loaded yet");
            return ;
        }


        Type type = new TypeToken<List<DishItem>>(){}.getType();
        FragmentTransaction tact = getSupportFragmentManager().beginTransaction();

        if (mainFragment!= null){
            Log.i("MainActivity/setMainFragment","Remove previous fragment");
            tact.remove(mainFragment);
            tact.commitAllowingStateLoss();
            tact = getSupportFragmentManager().beginTransaction();
        }

        Bundle bundle = new Bundle();

        bundle.putString("dishes", new Gson().toJson(dishItems, type));
        bundle.putBoolean("isGroup", false);

        mainFragment = new MainFragment();
        mainFragment.setArguments(bundle);

        tact.add(R.id.main_layout, mainFragment, MainFragment.MAIN_FRAGMENT);
        tact.commitAllowingStateLoss();
    }

    /*
    * Loads dish IDs from Server
    * Returns true if there is a new recommendation
    * */
    private void loadDishes(){

        dialog = createProgressDialog(MainActivity.this,
                R.string.progress_dialog_title, R.string.progress_dialog_message);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiURL)
                .setLog(setRestAdapterLog())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(setRequestInterceptorWithToken())
                .build();

        YammAPIService service = restAdapter.create(YammAPIService.class);

        restoreSavedList();

        if (mainFragment==null) {
            //If no mainfragment, show progress dialog
            dialog.show();
            Log.i("MainActivity/loadDishes", "Set Main Fragment with previous dishes");
            setMainFragment();
            dialog.dismiss();
        }


        service.getPersonalDishes(new Callback<List<DishItem>>() {
            @Override
            public void success(List<DishItem> items, Response response) {
                Log.i("MainActivity/getPersonalDishes","Dishes Loaded");

                if (!isSameDishItems(items)){
                    //if there is new list, show newDialog
                    Log.i("MainActivity/getPersonalDishes","Different List. Init MainFragment");

                    dishItems = items;
                    setMainFragment();

                    Toast.makeText(MainActivity.this, getString(R.string.new_recommendation_message),Toast.LENGTH_LONG).show();

                    return ;
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("MainActivity/getPersonalDishes", "Server Error, setting saved list");
                retrofitError.printStackTrace();

                dialog.dismiss();
            }
        });
    }

    private void restoreSavedList(){
        String savedList = prefs.getString("dishes", "none");

        if (!savedList.equals("none")){
            Type type = new TypeToken<List<DishItem>>(){}.getType();

            dishItems = new Gson().fromJson(savedList, type);
            if (dishItems!=null)
                Log.i("MainActivity/restoreSavedList", "Restore saved list : " + dishItems.toString());
        }
        else
            Log.i("MainActivity/restoreSavedList", "saved null");

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
        friendPickButton = (Button) findViewById(R.id.friends_pick_button);

        friendPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFriendLoaded()){
                    Toast.makeText(MainActivity.this, R.string.friend_not_loaded_message, Toast.LENGTH_LONG).show();
                    return ;
                }
                Intent intent = new Intent(MainActivity.this, FriendActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                v.setEnabled(false); //To prevent double fire
                Log.i("MainActivity/onClick", "FriendActivity called");
                startActivity(intent);
            }
        });
    }

    private void setLeftDrawer(){
        navMenuTitles = getResources().getStringArray(R.array.nav_menu_titles);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        leftDrawer.setAdapter(new ArrayAdapter<String>(this,
                R.layout.left_drawer_item, navMenuTitles));

        //Set Item Click Listener

        leftDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MainActivity/onItemClick","Left Drawer Item Clicked at " + position);
                if (position == DRAWER_LOGOUT){
                    createDialog(MainActivity.this,
                            R.string.logout_dialog_title, R.string.logout_dialog_message,
                            R.string.dialog_positive, R.string.dialog_negative,
                            setPositiveListener(), null).show();
                }
            }

            private DialogInterface.OnClickListener setPositiveListener(){
                return new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAuthToken();
                        removeFriendList();
                        Intent intent = new Intent(getBaseContext(), IntroActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                };
            }
        });

        Log.i("MainActivity/onCreate","Drawer Initialized");


    }
    //For Contact Reading
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

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiURL)
                .setLog(setRestAdapterLog())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(setRequestInterceptorWithToken())
                .build();

        YammAPIService service = restAdapter.create(YammAPIService.class);

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
}

