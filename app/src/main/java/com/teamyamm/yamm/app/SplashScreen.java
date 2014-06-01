package com.teamyamm.yamm.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


/**
 * Created by parkjiho on 5/7/14.
 */
public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 500;

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

                if (!checkPreviousActivity()) { // If first execution of the app, go to IntroActivity
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                }
                // close this activity
                finish();

            }
        }, SPLASH_TIME_OUT);
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

    private boolean checkPreviousActivity(){
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
        Intent activity = null;

        // To Remove later
        BaseActivity.putInPref(prefs, getString(R.string.PREVIOUS_ACTIVITY),getString(R.string.PREVIOUS_ACTIVITY_INTRO));
        prefs.edit().remove(getString(R.string.BATTLE_RESULTS)).commit();
    //    Log.w("SplashScreen","Delete SharedPreference manipulation in production");

        String value = prefs.getString(getString(R.string.PREVIOUS_ACTIVITY),"none");
        Log.v("SplashScreen","Activity Pref value:"+value);

        if (value == "none" || value.equals(getString(R.string.PREVIOUS_ACTIVITY_INTRO))){
            //Save Previous activity to shared preference
            BaseActivity.putInPref(prefs, getString(R.string.PREVIOUS_ACTIVITY),getString(R.string.PREVIOUS_ACTIVITY_INTRO));
            activity = new Intent(getBaseContext(), IntroActivity.class);
        }
        else if (value.equals(getString(R.string.PREVIOUS_ACTIVITY_BATTLE)))
            activity = new Intent(getBaseContext(), BattleActivity.class);
        else if (value.equals(getString(R.string.PREVIOUS_ACTIVITY_BATTLERESULT)))
            activity = new Intent(getBaseContext(), BattleResultActivity.class);
        else if (value.equals(getString(R.string.PREVIOUS_ACTIVITY_JOIN)))
            activity = new Intent(getBaseContext(), JoinActivity.class);

        if (activity!=null){
            Log.v("SplashScreen","Activity Start");
            startActivity(activity);
            return true;
        }
        return false;


    }



}