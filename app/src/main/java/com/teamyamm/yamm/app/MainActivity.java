package com.teamyamm.yamm.app;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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

    private HashMap<String, String> phoneNameMap;
    private List<Friend> friendsList;
    private String[] navMenuTitles;
    private DrawerLayout drawerLayout;
    private ListView leftDrawer;
    private MainFragment mainFragment;
    private ReadContactAsyncTask readContactAsyncTask;
    private SharedPreferences prefs;

    private List<DishItem> dishItems;
    private ProgressDialog dialog;

    private Button friendPickButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("MainActivity/onCreate", "onCreate started");

        setLeftDrawer();
        setFriendPickButton();

        prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
    }

    @Override
    public void onStart(){
        super.onStart();
        loadDishes();

        friendPickButton.setEnabled(true);
        Log.i("MainActivity","Execute Read Contact Async Task");

        readContactAsyncTask = new ReadContactAsyncTask();
        readContactAsyncTask.execute();
    }

    @Override
    public void onBackPressed() {
        goBackHome();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isFriendLoaded(){
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);

        String value = prefs.getString(getString(R.string.FRIEND_LIST),"none");

        return value != "none";
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

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
        }


        Bundle bundle = new Bundle();

        bundle.putString("dishes",new Gson().toJson(dishItems, type) );
        bundle.putBoolean("isGroup", false);

        mainFragment = new MainFragment();
        mainFragment.setArguments(bundle);


        tact.add(R.id.main_layout, mainFragment, MainFragment.MAIN_FRAGMENT);
        tact.commitAllowingStateLoss();


        dialog.dismiss();
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

        if (mainFragment == null){
            dialog.show();
        }

        service.getPersonalDishes(new Callback<List<DishItem>>() {
            @Override
            public void success(List<DishItem> items, Response response) {
                Log.i("MainActivity/getPersonalDishes","Dishes Loaded");
                if (!isSameDishItems(items)){
                    Log.i("MainActivity/getPersonalDishes","Different List. Init MainFragment");

                    //Should have Immense Loading here

                    dishItems = items;
                    setMainFragment();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("MainActivity/getPersonalDishes","Something went wrong");
                dialog.dismiss();
            }
        });

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
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FRIEND_ACTIVITY_REQUEST_CODE){
            Log.i("MainFragment/onActivityResult","Got back from FriendActivity; resultcode: " + resultCode);

            friendPickButton.setEnabled(true);

            if (resultCode == BaseActivity.SUCCESS_RESULT_CODE) {
                //Get Friend List
                selectedFriendList = data.getStringArrayListExtra(FriendActivity.SELECTED_FRIEND_LIST);

                Toast.makeText(getActivity(), "Got Back from Friend" + selectedFriendList, Toast.LENGTH_LONG).show();
            }
        }
    }*/


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
        Log.i("MainActivity/readContacts","Saved " + phoneNameMap.size() + " numbers");
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
                        Log.e("MainActivity/sendContactsToServer", "Sending Failed");
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

