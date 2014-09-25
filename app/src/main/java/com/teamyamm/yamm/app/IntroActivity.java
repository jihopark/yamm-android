package com.teamyamm.yamm.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.teamyamm.yamm.app.widget.IntroImageFragment;
import com.teamyamm.yamm.app.widget.YammCirclePageIndicator;

/**
 * Created by parkjiho on 5/31/14.
 */
public class IntroActivity extends BaseActivity {

    private Button joinButton, loginButton;
    private final static int NUM_PAGES = 3;
    private ViewPager pager;

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.d("IntroActivity/onSessionStateChange", "Logged in...");
            makeYammToast("페북 로그인!", Toast.LENGTH_SHORT);
            Log.d("IntroActivity/onSessionStateChange",session.getAccessToken());
        } else if (state.isClosed()) {
            Log.i("IntroActivity/onSessionStateChange", "Logged out...");
        }
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_intro);

        setFBAuth();
        setButtons();
        setViewPager();
    }

    @Override
    public void onResume(){
        super.onResume();
        uiHelper.onResume();
        BaseActivity.isLoggingOut = false;
    }


    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        goBackHome();
    }

    private void setFBAuth(){
        LoginButton button = (LoginButton) findViewById(R.id.fb_auth_button);
        button.setReadPermissions("public_profile", "email");
    }

    private void setViewPager(){
        pager = (ViewPager) findViewById(R.id.intro_view_pager);
        pager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        YammCirclePageIndicator indicator = (YammCirclePageIndicator)findViewById(R.id.intro_view_pager_indicator);

        indicator.setRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));

        indicator.setViewPager(pager);

    }

    private void setButtons(){
        joinButton = (Button) findViewById(R.id.join_button);
        loginButton = (Button) findViewById(R.id.login_button);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(JoinActivity.class);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(LoginActivity.class);
            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = new IntroImageFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position",position);
            f.setArguments(bundle);
            return f;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
