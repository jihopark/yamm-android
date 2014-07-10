package com.teamyamm.yamm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
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
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        friends = loadFriends();
        Log.i("FriendActivity/onResume","Loaded Friends");

        setTabViewPager();



    }

    private void setTabViewPager(){

        //Set Fragment View Pager
        FriendActivityPagerAdapter adapter =new FriendActivityPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.friend_fragment_pager);
        viewPager.setAdapter(adapter);
    }

    private class FriendActivityPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<String> tabNameList = new ArrayList<String>();


        public FriendActivityPagerAdapter(FragmentManager fm){
            super(fm);
            tabNameList.add(getResources().getString(R.string.friend_activity_tab1));
            tabNameList.add(getResources().getString(R.string.friend_activity_tab2));
        }

        @Override
        public Fragment getItem(int i) {
           if (i==0){
               return new FriendsFragment();
           }
           else{
               return new InviteFragment();
           }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNameList.get(position);
        }

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
