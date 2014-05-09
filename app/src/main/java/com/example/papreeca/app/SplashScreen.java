package com.example.papreeca.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;


/**
 * Created by parkjiho on 5/7/14.
 */
public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                if (!checkFirstExecution()) { // If first execution of the app, go to IntroActivity
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                }
                // close this activity
                finish();

            }
        }, SPLASH_TIME_OUT);
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

    private boolean checkFirstExecution(){
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);

        // Remove TRUE later

        if (true || prefs.getBoolean("firstrun",true)){
            Toast.makeText(getApplicationContext(), "첫 실행입니다", Toast.LENGTH_LONG).show(); //To be deleted
            Intent introActivity = new Intent(getBaseContext(), IntroActivity.class);
            startActivity(introActivity);
            return true;
        }
        return false;


    }

}