package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by parkjiho on 5/31/14.
 */
public class NewIntroActivity extends BaseActivity {

    private Button joinButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_intro);

        setButtons();
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
}
