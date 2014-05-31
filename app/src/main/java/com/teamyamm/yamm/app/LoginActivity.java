package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                Toast.makeText(getApplicationContext(),"로그인 되었습니다",Toast.LENGTH_SHORT).show();
                goToActivity(MainActivity.class);
            }
        });
    }

}
