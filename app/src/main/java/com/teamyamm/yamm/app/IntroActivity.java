package com.teamyamm.yamm.app;

import android.app.Dialog;
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
    private final static boolean KAKAO = true;
    private final static boolean FB = false;

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
        Button start = (Button) findViewById(R.id.start_button);
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
        button.setBackgroundResource(R.drawable.fb_round_button);
    }

    @Override
    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        super.onSessionStateChange(session, state, exception);

        if (state.isOpened()) {
            //Logging in
            Log.d("IntroActivity/onSessionStateChange", session.getAccessToken());
            final Dialog dialog = createFullScreenDialog(IntroActivity.this, getString(R.string.progress_dialog_message));
            dialog.show();
            YammAPIAdapter.getOAuthLoginService().fbLogin(true, session.getAccessToken(), new Callback<YammAPIService.RawOAuthToken>() {
                @Override
                public void success(YammAPIService.RawOAuthToken rawOAuthToken, Response response) {
                    putInPref(prefs, getString(R.string.AUTH_TOKEN), rawOAuthToken.access_token);
                    YammAPIAdapter.setToken(rawOAuthToken.access_token);
                    Log.d("IntroActivity/fbLogin","FB Login Success." + rawOAuthToken);
                    dialog.dismiss();
                    if (rawOAuthToken.is_new)
                        toJoin(rawOAuthToken.email, FB);
                    else
                        toLogin(rawOAuthToken.email, FB, rawOAuthToken.has_phone);

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
                    dialog.dismiss();
                }
            });
        }
    }

    private void toJoin(String email, boolean type){
        MixpanelController.setMixpanelAlias(email);
        //Get Push Token
        registerGCM();

        if (type==FB) {
            makeYammToast(R.string.fb_join_success, Toast.LENGTH_SHORT);
            Session.getActiveSession().close();

        }
        else {
            makeYammToast(R.string.kakao_join_success, Toast.LENGTH_SHORT);
        }
        //Move onto Next Activity
        goToActivity(PhoneAuthActivity.class);
    }

    private void toLogin(String email, boolean type, boolean hasPhone){
        MixpanelController.setMixpanelIdentity(email);

        if (type==FB) {
            Session.getActiveSession().close();
        }

        makeYammToast(R.string.oauth_login_success, Toast.LENGTH_SHORT);

        //For Push Token
        registerGCM();

        if (hasPhone){
            Log.d("IntroActivity/toLogin","User has phone verified.");
            goToActivity(MainActivity.class);
        }
        else{
            Log.d("IntroActivity/toLogin","User has no phone verified.");
            goToActivity(PhoneAuthActivity.class);
        }
    }

    /*
    * Kakao Auth
    * */

    private void setKakaoAuth(){
        com.kakao.widget.LoginButton kakaoButton = joinFragment.getKakaoJoinButton();
        kakaoButton.setLoginSessionCallback(kakaoSessionCallback);
    }

    private class KakaoSessionStatusCallback implements SessionCallback {
        @Override
        public void onSessionOpened() {
            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈후 보일 페이지로 이동
            if (!isLoadingKakao)
                IntroActivity.this.onSessionOpened();
        }

        @Override
        public void onSessionClosed(final KakaoException exception) {
            Log.d("IntroActivity/kakao", "Session closed");
        }
    }

    protected void onSessionOpened(){
/*        final Intent intent = new Intent(IntroActivity.this, SampleSignupActivity.class);
        startActivity(intent);
        finish();*/
        Log.d("IntroActivity/kakao", "Session Opened");
        isLoadingKakao = true;
        final Dialog dialog = createFullScreenDialog(IntroActivity.this, getString(R.string.progress_dialog_message));
        dialog.show();
        YammAPIAdapter.getOAuthLoginService().kakaoLogin(true, com.kakao.Session.getCurrentSession().getAccessToken(), new Callback<YammAPIService.RawOAuthToken>() {
            @Override
            public void success(YammAPIService.RawOAuthToken rawOAuthToken, Response response) {
                Log.d("IntroActivity/kakaoLogin", "Successful " + rawOAuthToken.kakao_uid + " is_new " + rawOAuthToken.is_new);
                if (com.kakao.Session.getCurrentSession() != null) {
                    com.kakao.Session.getCurrentSession().close(kakaoSessionCallback);
                }

                putInPref(prefs, getString(R.string.AUTH_TOKEN), rawOAuthToken.access_token);
                YammAPIAdapter.setToken(rawOAuthToken.access_token);
                Log.d("IntroActivity/kakaoLogin","Kakao Login Success." + rawOAuthToken);

                dialog.dismiss();

                isLoadingKakao = false;
                if (rawOAuthToken.is_new)
                    toJoin(rawOAuthToken.kakao_uid + "@kakao.com", KAKAO);
                else
                    toLogin(rawOAuthToken.kakao_uid+"@kakao.com",KAKAO, rawOAuthToken.has_phone);


            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String msg = retrofitError.getCause().getMessage();

                Log.e("IntroActivity/kakaoLogin", "Kakao Login Error " + msg);

                if (msg.equals(YammAPIService.YammRetrofitException.NETWORK))
                    makeYammToast(getString(R.string.network_error_message), Toast.LENGTH_SHORT);
                else if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION))
                    makeYammToast(getString(R.string.fb_invalid_token_message), Toast.LENGTH_SHORT);
                else
                    makeYammToast(getString(R.string.unidentified_error_message), Toast.LENGTH_SHORT);
                if (com.kakao.Session.getCurrentSession() != null) {
                    com.kakao.Session.getCurrentSession().close(kakaoSessionCallback);
                }
                isLoadingKakao = false;
                dialog.dismiss();
            }
        });

        makeYammToast("KAKAO", Toast.LENGTH_SHORT);
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
                goToActivity(JoinActivity.class);
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
