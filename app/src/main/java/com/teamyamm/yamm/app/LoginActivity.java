package com.teamyamm.yamm.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 5/31/14.
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((EditText) findViewById(R.id.pw_field)).setTransformationMethod(new HiddenPassTransformationMethod());
        setActionBarBackButton(true);
        setLoginButton();
    }

    private void setLoginButton(){
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postLoginToServer(((EditText) findViewById(R.id.email_field)).getText().toString(),
                        ((EditText) findViewById(R.id.pw_field)).getText().toString());
            }
        });
    }

    private void postLoginToServer(String email, String pw){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiURL)
                .setLog(setRestAdapterLog())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(setRequestInterceptorForLogin(email, pw))
                .build();


        YammAPIService service = restAdapter.create(YammAPIService.class);

        service.userLogin(new YammAPIService.GrantType(), new Callback<YammAPIService.YammToken>() {
            @Override
            public void success(YammAPIService.YammToken yammToken, Response response) {
                Log.i("LoginActivity/userLogin", "Logged in " + yammToken);

                //Save Token to Shared Pref
                SharedPreferences prefs = getSharedPreferences(packageName, MODE_PRIVATE);
                putInPref(prefs, getString(R.string.AUTH_TOKEN), yammToken.toString());

                Toast.makeText(getApplicationContext(), "로그인 되었습니다", Toast.LENGTH_SHORT).show();
                goToActivity(MainActivity.class);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(getApplicationContext(), "로그인이 실패하였습니다", Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity/userLogin", "Logged in Failed");
                Log.e("LoginActivity/userLogin", "Body " + retrofitError.getResponse().getBody().toString());
                retrofitError.printStackTrace();
            }
        });
    }

    private RequestInterceptor setRequestInterceptorForLogin(String email, String pw){
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
