package com.teamyamm.yamm.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


/**
 * Created by parkjiho on 5/26/14.
 */
public class FriendActivity extends BaseActivity {
    public final static String FRIEND_FRAGMENT = "ff";
    public final static String FRIEND_LIST = "fl";

    private FriendsFragment friendsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

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



    private void finishActivity(){
        Intent resultIntent = new Intent();
        resultIntent.putIntegerArrayListExtra(FriendActivity.FRIEND_LIST, friendsFragment.selectedItemsInteger);
        setResult(BaseActivity.SUCCESS_RESULT_CODE, resultIntent);
        finish();
    }

}
