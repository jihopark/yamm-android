package com.teamyamm.yamm.app;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 5/31/14.
 */
public class LoginActivity extends BaseActivity {
    private EditText emailField, pwdField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setEditTexts();
        setActionBarBackButton(true);
        setLoginButton();
    }

    private void setEditTexts(){
        emailField = ((EditText) findViewById(R.id.email_field));
        pwdField = ((EditText) findViewById(R.id.pw_field));
        pwdField.setTransformationMethod(new HiddenPassTransformationMethod());

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(emailField.getText().toString().equals("") || pwdField.getText().toString().equals(""))){
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

        emailField.addTextChangedListener(textWatcher);
        pwdField.addTextChangedListener(textWatcher);
    }

    private void setLoginButton(){
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String pwd = pwdField.getText().toString();

                if (!(email.equals("") || pwd.equals(""))) {
                    hideSoftKeyboard(LoginActivity.this);
                    postLoginToServer(email, pwd);
                }
            }
        });
    }

    private void changeLoginButtonState(boolean b){
        Button loginButton = (Button) findViewById(R.id.login_button);

        if (b){
            loginButton.setTextColor(getResources().getColor(R.color.button_enabled_text));
            loginButton.setBackgroundColor(getResources().getColor(R.color.button_enabled_background));
        }
        else{
            loginButton.setTextColor(getResources().getColor(R.color.button_disabled_text));
            loginButton.setBackgroundColor(getResources().getColor(R.color.button_disabled_background));
        }
    }

    private void postLoginToServer(String email, String pw){
        final ProgressDialog progressDialog;
        // Show Progress Dialog
        progressDialog = createProgressDialog(LoginActivity.this,
                R.string.progress_dialog_title,
                R.string.progress_dialog_message);
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

                setMixpanelIdentity();

                Toast.makeText(getApplicationContext(), "로그인 되었습니다", Toast.LENGTH_SHORT).show();
                goToActivity(MainActivity.class);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String msg = retrofitError.getCause().getMessage();
                Log.e("LoginActivity/userLogin", "ERROR CODE" + msg);

                progressDialog.dismiss();

                if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION))
                    makeErrorToast(getString(R.string.login_authentication_error_message), Toast.LENGTH_SHORT);
                else if (msg.equals(YammAPIService.YammRetrofitException.NETWORK))
                    makeErrorToast(getString(R.string.network_error_message), Toast.LENGTH_SHORT);
                else
                    makeErrorToast(getString(R.string.unidentified_error_message), Toast.LENGTH_SHORT);


            }
        });
    }

    private void setMixpanelIdentity(){
        mixpanel.identify(emailField.getText().toString());
        Log.i("LoginActivity/setMixpanelIdentity","Setting Unique ID with email "+ emailField.getText().toString());
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
