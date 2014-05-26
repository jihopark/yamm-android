package com.teamyamm.yamm.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.RelativeLayout;


/**
 * Created by parkjiho on 5/26/14.
 */
public class FriendActivity extends BaseActivity {
    FriendsFragment friendsFragment;
    RelativeLayout friendFragmentContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        friendsFragment = new FriendsFragment();

        FragmentTransaction tact = getSupportFragmentManager().beginTransaction();
        tact.add(R.id.friends_fragment_container, friendsFragment);
        tact.commit();
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    private void finishActivity(){
        Intent resultIntent = new Intent();
        setResult(BaseActivity.SUCCESS_RESULT_CODE,resultIntent);
        finish();
    }

}
