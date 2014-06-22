package com.teamyamm.yamm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;


/**
 * Created by parkjiho on 5/26/14.
 */
public class FriendActivity extends BaseActivity {



    public final static String FRIEND_FRAGMENT = "ff";
    public final static String SELECTED_FRIEND_LIST = "fl";

    private HashMap<String, String> friendNameMap;
    private boolean enableButtonFlag = true;
    private FriendsFragment friendsFragment;
    private List<Friend> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        friends = loadFriends();
        Log.i("FriendActivity/onResume","Loaded Friends");

        friendsFragment = new FriendsFragment();

        FragmentTransaction tact = getSupportFragmentManager().beginTransaction();
        tact.add(R.id.friends_fragment_container, friendsFragment, FRIEND_FRAGMENT);
        tact.commit();
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(BaseActivity.FAILURE_RESULT_CODE, resultIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.friend_activity_actions, menu);

        if (enableButtonFlag) {
            menu.findItem(R.id.friend_pick_confirm_button).setEnabled(true);
        } else {
            menu.findItem(R.id.friend_pick_confirm_button).setEnabled(false);
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.friend_pick_confirm_button:
                finishActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setConfirmButtonEnabled(boolean b){
        if (enableButtonFlag != b) {
            enableButtonFlag = b;
            supportInvalidateOptionsMenu();
        }
    }

    public List<Friend> getFriends(){
        return friends;
    }



    private void finishActivity(){
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra(FriendActivity.SELECTED_FRIEND_LIST, friendsFragment.selectedItemsID);
        setResult(BaseActivity.SUCCESS_RESULT_CODE, resultIntent);
        finish();
    }

    private List<Friend> loadFriends(){
        //Load Contacts From SharedPrefs
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);

        List<Friend> list = fromStringToFriendList(prefs.getString(getString(R.string.FRIEND_LIST),"none"));

        if (list == null){
            Log.e("FriendActivity/loadContacts","Failed to load contacts from shared pref");
            Toast.makeText(this, getString(R.string.friend_not_loaded_message), Toast.LENGTH_SHORT);
            finish();
        }
        else{
            Log.i("FriendActivity/loadContacts", "Successfully loaded friends");
            Log.i("FriendActivity/loadContacts",list.toString());

        }
        return list;
    }


}
