package com.teamyamm.yamm.app;

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
import com.facebook.widget.LoginButton;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.network.YammAPIService;
import com.teamyamm.yamm.app.widget.IntroImageFragment;
import com.teamyamm.yamm.app.widget.YammCirclePageIndicator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 5/31/14.
 */
public class IntroActivity extends BaseActivity {

    private Button joinButton, loginButton;
    private final static int NUM_PAGES = 3;
    private ViewPager pager;

    @Override
    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        super.onSessionStateChange(session, state, exception);

        if (state.isOpened()) {
            //Logging in
            Log.d("IntroActivity/onSessionStateChange", session.getAccessToken());
            YammAPIAdapter.getFBLoginService().fbLogin(session.getAccessToken(), new Callback<YammAPIService.RawFBToken>() {
                @Override
                public void success(YammAPIService.RawFBToken rawFBToken, Response response) {
                   putInPref(prefs, getString(R.string.AUTH_TOKEN), rawFBToken.access_token);
                   YammAPIAdapter.setToken(rawFBToken.access_token);
                    Log.d("IntroActivity/fbLogin","FB Login Success." + rawFBToken);
                    if (rawFBToken.is_new)
                        fbToJoin(rawFBToken.email);
                    else
                        fbToLogin(rawFBToken.email);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    String msg = retrofitError.getCause().getMessage();

                    Log.e("IntroActivity/fbLogin","FB Login Error " + msg);
                    if (Session.getActiveSession()!=null) {
                        Session.getActiveSession().closeAndClearTokenInformation();
                    }

                    if (msg.equals(YammAPIService.YammRetrofitException.DUPLICATE_ACCOUNT))
                        makeYammToast(getString(R.string.fb_login_duplicate_error_message), Toast.LENGTH_LONG);
                    else if (msg.equals(YammAPIService.YammRetrofitException.NETWORK))
                        makeYammToast(getString(R.string.network_error_message), Toast.LENGTH_SHORT);
                    else if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION))
                        makeYammToast(getString(R.string.fb_invalid_token_message), Toast.LENGTH_SHORT);
                    else
                        makeYammToast(getString(R.string.unidentified_error_message), Toast.LENGTH_SHORT);
                }
            });
        }
    }

    private void fbToJoin(String email){
        MixpanelController.setMixpanelAlias(email);
        //Get Push Token
        registerGCM();
        //Move onto Next Activity
        goToActivity(GridActivity.class);
    }

    private void fbToLogin(String email){
        MixpanelController.setMixpanelIdentity(email);

        //For Push Token
        registerGCM();

        goToActivity(MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_intro);

        setFBAuth();
        setButtons();
        setViewPager();
    }

    @Override
    public void onResume(){
        super.onResume();
        BaseActivity.isLoggingOut = false;
    }

    @Override
    public void onBackPressed() {
        goBackHome();
    }

    private void setFBAuth(){
        LoginButton button = (LoginButton) findViewById(R.id.fb_auth_button);
        button.setReadPermissions("public_profile", "email");
        button.setBackgroundResource(R.drawable.fb_round_button);
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
