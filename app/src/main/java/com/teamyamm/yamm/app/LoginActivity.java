package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;
import com.kakao.SessionCallback;
import com.kakao.exception.KakaoException;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.network.YammAPIService;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 5/31/14.
 */
public class LoginActivity extends BaseActivity {
    private EditText phoneField, pwdField;
    private final SessionCallback kakaoSessionCallback = new KakaoSessionStatusCallback();

    private boolean isLoadingKakao = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setEditTexts();
        setActionBarBackButton(true);
        setLoginButton();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(com.kakao.Session.initializeSession(this, kakaoSessionCallback)){
            Log.d("LoginActivity/kakao", "Initializing Session");
        }
    }

    private void setEditTexts(){
        phoneField = ((EditText) findViewById(R.id.phone_field));
        pwdField = ((EditText) findViewById(R.id.pw_field));
        pwdField.setTransformationMethod(new HiddenPassTransformationMethod());

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(phoneField.getText().toString().equals("") || pwdField.getText().toString().equals(""))){
                    changeLoginButtonState(true);
                }
                else{
                    changeLoginButtonState(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        phoneField.addTextChangedListener(textWatcher);
        pwdField.addTextChangedListener(textWatcher);

        TextView forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneField.getText().toString().equals("")){
                    requestPhone();
                }
                else{
                    final Dialog progress = createFullScreenDialog(LoginActivity.this, getString(R.string.progress_dialog_message));

                    View.OnClickListener positiveListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                progress.show();
                            YammAPIAdapter.getService().requestPasswordRecoveryFromPhone(phoneField.getText().toString(), new Callback<String>() {
                                @Override
                                public void success(String s, Response response) {
                                    progress.dismiss();
                                    makeYammToast(R.string.forgot_password_sent, Toast.LENGTH_SHORT);
                                }

                                @Override
                                public void failure(RetrofitError retrofitError) {
                                    progress.dismiss();
                                    makeYammToast(R.string.unidentified_error_message, Toast.LENGTH_SHORT);
                                }
                            });
                            dismissCurrentDialog();
                        }
                    };
                    View.OnClickListener negativeListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPhone();
                            dismissCurrentDialog();
                        }
                    };
                        createDialog(LoginActivity.this, R.string.forgot_password_dialog_title,
                                R.string.forgot_password_dialog_message, R.string.dialog_positive, R.string.dialog_negative,
                                positiveListener, negativeListener).show();


                }
            }

            private void requestPhone(){
                makeYammToast(R.string.forgot_password_no_phone, Toast.LENGTH_SHORT);
                showSoftKeyboard(phoneField, LoginActivity.this);
            }
        });
    }

    private void setLoginButton(){
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = phoneField.getText().toString();
                String pwd = pwdField.getText().toString();

                if (!(email.equals("") || pwd.equals(""))) {
                    hideSoftKeyboard(LoginActivity.this);
                    postLoginToServer(email, pwd);
                }
            }
        });

        com.kakao.widget.LoginButton kakaoButton = (com.kakao.widget.LoginButton) findViewById(R.id.kakao_login_button);
        kakaoButton.setLoginSessionCallback(kakaoSessionCallback);

        setFBAuth();
    }

    private void changeLoginButtonState(boolean b){
        Button loginButton = (Button) findViewById(R.id.login_button);

        if (b){
            loginButton.setTextColor(getResources().getColor(R.color.button_enabled_text));
            loginButton.setBackgroundResource(R.drawable.enabled_round_button);
        }
        else{
            loginButton.setTextColor(getResources().getColor(R.color.button_disabled_text));
            loginButton.setBackgroundResource(R.drawable.disabled_round_button);
        }
    }

    private void postLoginToServer(String email, String pw){
        final Dialog progressDialog;
        // Show Progress Dialog
        progressDialog = createFullScreenDialog(LoginActivity.this, getString(R.string.progress_dialog_message));
        progressDialog.show();

        YammAPIService service = YammAPIAdapter.getLoginService(email, pw);

        service.userLogin(new YammAPIService.GrantType(), new Callback<YammAPIService.YammToken>() {
            @Override
            public void success(YammAPIService.YammToken yammToken, Response response) {
                Log.i("LoginActivity/userLogin", "Logged in " + yammToken);

                progressDialog.dismiss();

                //Save Token to Shared Pref
                SharedPreferences prefs = getSharedPreferences(packageName, MODE_PRIVATE);
                putInPref(prefs, getString(R.string.AUTH_TOKEN), yammToken.toString());

                YammAPIAdapter.setToken(yammToken.toString());

                MixpanelController.setMixpanelIdentity(phoneField.getText().toString());

                //For Push Token
                registerGCM();

                goToActivity(MainActivity.class);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String msg = retrofitError.getCause().getMessage();
                Log.e("LoginActivity/userLogin", "ERROR CODE" + msg);

                progressDialog.dismiss();

                if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION))
                    makeYammToast(getString(R.string.login_authentication_error_message), Toast.LENGTH_SHORT);
                else if (msg.equals(YammAPIService.YammRetrofitException.NETWORK))
                    makeYammToast(getString(R.string.network_error_message), Toast.LENGTH_SHORT);
                else
                    makeYammToast(getString(R.string.unidentified_error_message), Toast.LENGTH_SHORT);


            }
        });
    }

    private void setFBAuth(){
        LoginButton button = (LoginButton) findViewById(R.id.fb_auth_button);
        button.setReadPermissions("public_profile", "email");
        button.setBackgroundResource(R.drawable.fb_round_button);
    }

    @Override
    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        super.onSessionStateChange(session, state, exception);

        if (state.isOpened()) {
            //Logging in
            Log.d("LoginActivity/onSessionStateChange", session.getAccessToken());
            final Dialog dialog = createFullScreenDialog(LoginActivity.this, getString(R.string.progress_dialog_message));
            dialog.show();
            YammAPIAdapter.getOAuthLoginService().fbLogin(session.getAccessToken(), new Callback<YammAPIService.RawOAuthToken>() {
                @Override
                public void success(YammAPIService.RawOAuthToken rawOAuthToken, Response response) {
                    putInPref(prefs, getString(R.string.AUTH_TOKEN), rawOAuthToken.access_token);
                    YammAPIAdapter.setToken(rawOAuthToken.access_token);
                    Log.d("IntroActivity/fbLogin", "FB Login Success. " + rawOAuthToken.uid);
                    toLogin(rawOAuthToken.uid+"@facebook", FB);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    dialog.dismiss();

                    String msg = retrofitError.getCause().getMessage();
                    if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION)){
                        Log.e("IntroActivity/fbLogin", "FB New Entry");
                        makeYammToast(R.string.oauth_login_failure, Toast.LENGTH_SHORT);
                    }
                    else if (msg.equals(YammAPIService.YammRetrofitException.NETWORK)){
                        makeYammToast(R.string.network_error_message, Toast.LENGTH_SHORT);
                    }
                    else
                        makeYammToast(R.string.unidentified_error_message, Toast.LENGTH_SHORT);
                    Session.getActiveSession().closeAndClearTokenInformation();
                }
            });
        }
    }


    private class KakaoSessionStatusCallback implements SessionCallback {
        @Override
        public void onSessionOpened() {
            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈후 보일 페이지로 이동
            Log.d("LoginActivity/kakao", "Session is open");
            if (!isLoadingKakao)
                LoginActivity.this.onSessionOpened();
        }

        @Override
        public void onSessionClosed(final KakaoException exception) {
            Log.d("LoginActivity/kakao", "Session closed");
        }
    }

    protected void onSessionOpened(){
        Log.d("LoginActivity/kakao", "Session Opened");
        isLoadingKakao = true;
        final Dialog dialog = createFullScreenDialog(LoginActivity.this, getString(R.string.progress_dialog_message));
        dialog.show();
        YammAPIAdapter.getOAuthLoginService().kakaoLogin(com.kakao.Session.getCurrentSession().getAccessToken(), new Callback<YammAPIService.RawOAuthToken>() {
            @Override
            public void success(YammAPIService.RawOAuthToken rawOAuthToken, Response response) {
                Log.d("LoginActivity/kakaoLogin", "Kakao Login Success" + rawOAuthToken.uid);

                putInPref(prefs, getString(R.string.AUTH_TOKEN), rawOAuthToken.access_token);
                YammAPIAdapter.setToken(rawOAuthToken.access_token);

                dialog.dismiss();

                isLoadingKakao = false;
                toLogin(rawOAuthToken.uid+"@kakao", KAKAO);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String msg = retrofitError.getCause().getMessage();
                dialog.dismiss();

                if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION)){
                    Log.e("LoginActivity/kakaoLogin", "Kakao New Entry");
                    makeYammToast(R.string.oauth_login_failure, Toast.LENGTH_SHORT);
                }
                else if (msg.equals(YammAPIService.YammRetrofitException.NETWORK)){
                    makeYammToast(R.string.network_error_message, Toast.LENGTH_SHORT);
                }
                else
                    makeYammToast(R.string.unidentified_error_message, Toast.LENGTH_SHORT);

                isLoadingKakao = false;
                com.kakao.Session.getCurrentSession().close(new SessionCallback() {
                    @Override
                    public void onSessionOpened() {

                    }

                    @Override
                    public void onSessionClosed(KakaoException e) {
                        Log.e("LoginActivity/kakaoLogin", "Kakao Session closed");
                    }
                });
            }
        });
    }

    private void toLogin(String id, int type){
        MixpanelController.setMixpanelIdentity(id);

        if (type == KAKAO)
            makeYammToast(R.string.kakao_join_success, Toast.LENGTH_SHORT);
        else if (type == FB)
            makeYammToast(R.string.fb_join_success, Toast.LENGTH_SHORT);
        else
            makeYammToast(R.string.login_success, Toast.LENGTH_SHORT);
        //For Push Token
        registerGCM();

        goToActivity(MainActivity.class);
    }


    public static RequestInterceptor setRequestInterceptorForLogin(String email, String pw){
        final String username = email;
        final String pwd = pw;
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                Log.i("RequestInterceptor","username " + username + " pwd " + pwd);
                String cred = username + ":" + pwd;
                Log.i("RequestInterceptor","Basic " + Base64.encodeToString(cred.getBytes(), Base64.NO_WRAP));
                request.addHeader("Authorization", "Basic " + Base64.encodeToString(cred.getBytes(), Base64.NO_WRAP));
            }
        };
    }
}
