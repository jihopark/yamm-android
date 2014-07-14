package com.teamyamm.yamm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;


/**
 * Created by parkjiho on 5/26/14.
 */
public class FriendActivity extends BaseActivity {



    public final static String FRIEND_FRAGMENT = "ff";
    public final static String SELECTED_FRIEND_LIST = "fl";

    private boolean enableButtonFlag = true;
    private FriendsFragment friendsFragment;
    private List<Friend> friends;

    private Spinner datePickSpinner;
    public YammDatePickerFragment datePickerFragment;
    public ArrayAdapter<CharSequence> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        setActionBarBackButton(true);

        friends = loadFriends();
        Log.i("FriendActivity/onResume","Loaded Friends");

        //Set Fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        friendsFragment = new FriendsFragment();
        fragmentTransaction.add(R.id.friend_activity_container, friendsFragment, FRIEND_FRAGMENT);
        fragmentTransaction.commit();

        setDatePickSpinner();
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
            Log.e("FriendActivity/loadFriends","Failed to load contacts from shared pref");
            Toast.makeText(this, getString(R.string.friend_not_loaded_message), Toast.LENGTH_SHORT);
            finish();
        }
        else{
            Log.i("FriendActivity/loadFriends", "Successfully loaded friends");
            Log.i("FriendActivity/loadFriends",list.toString());

        }
        return list;
    }

    private void setDatePickSpinner(){
        datePickSpinner = (Spinner) findViewById(R.id.date_pick_spinner);
        spinnerAdapter = ArrayAdapter.createFromResource(FriendActivity.this, R.array.date_spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        datePickSpinner.setAdapter(spinnerAdapter);
        datePickSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                if (pos == getResources().getInteger(R.integer.spinner_datepick_pos) ){
                    datePickerFragment = new YammDatePickerFragment();
                    datePickerFragment.show(getSupportFragmentManager(), "timePicker");
                }
            }
            public void onNothingSelected(AdapterView<?> parent) { }

        });
    }

}
