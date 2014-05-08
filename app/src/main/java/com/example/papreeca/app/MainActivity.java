package com.example.papreeca.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;


public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkFirstExecution(); // If first execution of the app, go to IntroActivity
    }


    ////////////////////////////////Private Methods/////////////////////////////////////////////////


    private void checkFirstExecution(){
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);

        // Remove TRUE later

        if (true || prefs.getBoolean("firstrun",true)){
            Toast.makeText(getApplicationContext(),"첫 실행입니다",Toast.LENGTH_LONG).show(); //To be deleted
            Intent introActivity = new Intent(getBaseContext(), IntroActivity.class);
            startActivity(introActivity);
        }


    }
}
