package com.teamyamm.yamm.app;

import android.app.Dialog;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;
import com.kakao.SessionCallback;
import com.kakao.exception.KakaoException;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.network.YammAPIService;
import com.teamyamm.yamm.app.widget.IntroImageFragment;
import com.teamyamm.yamm.app.widget.JoinFragment;
import com.teamyamm.yamm.app.widget.YammCirclePageIndicator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 5/31/14.
 */
public class IntroActivity extends BaseActivity {
    public final static String AUTH_TYPE = "AUTH_TYPE";
    private final static int NUM_PAGES = 3;
    private ViewPager pager;
    private static boolean isLoadingKakao = false;

    private JoinFragment joinFragment;
    private static boolean isFragmentShown = false;

    private final SessionCallback kakaoSessionCallback = new KakaoSessionStatusCallback();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_intro);

        joinFragment = (JoinFragment) getSupportFragmentManager().findFragmentById(R.id.join_fragment);
        getSupportFragmentManager().beginTransaction()
                .hide(joinFragment).commit();

        setStartButton();
        setFBAuth();
        setKakaoAuth();
        setButtons();
        setViewPager();
    }

    @Override
    public void onResume(){
        super.onResume();
        BaseActivity.isLoggingOut = false;

        if(com.kakao.Session.initializeSession(this, kakaoSessionCallback)){
            Log.d("IntroActivity/kakao", "Initializing Session");
        }
        else if (com.kakao.Session.getCurrentSession().isOpened()){
            Log.d("IntroActivity/kakao", "Session Opened");
            onSessionOpened();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFragmentShown){
            getSupportFragmentManager().beginTransaction()
                    .hide(joinFragment).commit();
            isFragmentShown = false;
        }
        else
            goBackHome();
    }

    private void setStartButton(){
        ImageButton start = (ImageButton) findViewById(R.id.start_button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .show(joinFragment).commit();
                isFragmentShown = true;
            }
        });
    }

    /*
    * Facebook Auth
    * */
    private void setFBAuth(){
        LoginButton button = joinFragment.getFacebookJoinButton();
        button.setReadPermissions("public_profile", "email");
        button.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        button.setBackgroundResource(R.drawable.fb_round_button);
    }

    @Override
    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        super.onSessionStateChange(session, state, exception);

        if (state.isOpened()) {
            //Logging in
            Log.d("IntroActivity/onSessionStateChange", session.getAccessToken());
            final Dialog dialog = createFullScreenDialog(IntroActivity.this, getString(R.string.progress_dialog_message));
            YammAPIAdapter.getOAuthLoginService().fbLogin(session.getAccessToken(), new Callback<YammAPIService.RawOAuthToken>() {
                @Override
                public void success(YammAPIService.RawOAuthToken rawOAuthToken, Response response) {
                    putInPref(prefs, getString(R.string.AUTH_TOKEN), rawOAuthToken.access_token);
                    YammAPIAdapter.setToken(rawOAuthToken.access_token);
                    Log.d("IntroActivity/fbLogin", "FB Login Success. " + rawOAuthToken.uid);
                    toLogin(rawOAuthToken.uid, FB);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    String msg = retrofitError.getCause().getMessage();

                    if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION)){
                        Log.e("IntroActivity/fbLogin","FB New Entry");
                        toJoin(FB);
                    }

                   /* if (Session.getActiveSession()!=null) {
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
                    */
                }
            });
        }
    }

    private void toJoin(int type){
        //MixpanelController.setMixpanelAlias(email);
        //Get Push Token
        //registerGCM();

        /*if (type==FB) {
            makeYammToast(R.string.fb_join_success, Toast.LENGTH_SHORT);
        }
        else {
            makeYammToast(R.string.kakao_join_success, Toast.LENGTH_SHORT);
        }*/

        Bundle bundle = new Bundle();
        bundle.putInt(AUTH_TYPE, type);

        Intent intent = new Intent(IntroActivity.this, NewJoinActivity.class);
        intent.putExtras(bundle);

        putInPref(getSharedPreferences(packageName, MODE_PRIVATE)
                ,getString(R.string.PREVIOUS_ACTIVITY), NewJoinActivity.class.getSimpleName());

        //Move onto Next Activity
        startActivity(intent);
    }

    private void toLogin(String id, int type){
        MixpanelController.setMixpanelIdentity(id);

        makeYammToast(R.string.login_success, Toast.LENGTH_SHORT);
        //For Push Token
        registerGCM();

        goToActivity(MainActivity.class);
    }

    /*
    * Kakao Auth
    * */

    private void setKakaoAuth(){

    }

    private class KakaoSessionStatusCallback implements SessionCallback {
        @Override
        public void onSessionOpened() {
            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈후 보일 페이지로 이동
            Log.d("IntroActivity/kakao", "Session is open");
            if (!isLoadingKakao && isFragmentShown)
                IntroActivity.this.onSessionOpened();
        }

        @Override
        public void onSessionClosed(final KakaoException exception) {
            Log.d("IntroActivity/kakao", "Session closed");
        }
    }

    protected void onSessionOpened(){
        Log.d("IntroActivity/kakao", "Session Opened");
        isLoadingKakao = true;
        final Dialog dialog = createFullScreenDialog(IntroActivity.this, getString(R.string.progress_dialog_message));
        dialog.show();
        YammAPIAdapter.getOAuthLoginService().kakaoLogin(com.kakao.Session.getCurrentSession().getAccessToken(), new Callback<YammAPIService.RawOAuthToken>() {
            @Override
            public void success(YammAPIService.RawOAuthToken rawOAuthToken, Response response) {
                Log.d("IntroActivity/kakaoLogin", "Kakao Login Success" + rawOAuthToken.uid);

                putInPref(prefs, getString(R.string.AUTH_TOKEN), rawOAuthToken.access_token);
                YammAPIAdapter.setToken(rawOAuthToken.access_token);

                dialog.dismiss();

                isLoadingKakao = false;
                toLogin(rawOAuthToken.uid,KAKAO);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String msg = retrofitError.getCause().getMessage();

                if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION)){
                    Log.e("IntroActivity/kakaoLogin","Kakao New Entry");
                    toJoin(KAKAO);
                }
                isLoadingKakao = false;
                dialog.dismiss();
            }
        });
    }


    private void setViewPager(){
        pager = (ViewPager) findViewById(R.id.intro_view_pager);
        pager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        YammCirclePageIndicator indicator = (YammCirclePageIndicator)findViewById(R.id.intro_view_pager_indicator);

        indicator.setRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));

        indicator.setViewPager(pager);

    }

    private void setButtons(){
        Button joinButton = joinFragment.getEmailJoinButton();
        joinFragment.setLoginButton();

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toJoin(PW);
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
