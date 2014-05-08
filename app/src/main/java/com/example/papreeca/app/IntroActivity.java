package com.example.papreeca.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class IntroActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        configButtons();
    }

    /*
    * Go to Home Screen When Back Button of IntroActivity
    * */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

    /*
    * Setup OnclickListener for button that finishes intro
    * */
    private void configButtons(){
        Button introButton = (Button) findViewById(R.id.intro_button);
        introButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
                Toast.makeText(getApplicationContext(), "인트로 끝", Toast.LENGTH_LONG).show(); //To be deleted
                prefs.edit().putBoolean("firstrun", false).commit();

                //Go to Battle Activity
                Intent battleActivity = new Intent(getBaseContext(), BattleActivity.class);
                startActivity(battleActivity);
            }
        });
    }

}
