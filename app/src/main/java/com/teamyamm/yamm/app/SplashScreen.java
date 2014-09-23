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
    private static int SPLASH_TIME_OUT = 1000;
    private Bundle bundle = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if (getIntent().getExtras()!=null && getIntent().getExtras().getString("error")!=null)
            bundle = getIntent().getExtras();

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
                    Intent i = new Intent(SplashScreen.this, IntroActivity.class);
                    if (bundle!=null)
                        i.putExtras(bundle);

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



       /*
        *
        * REMOVES TOKEN TO NULL - NEED TO BE DELETED FOR PRODUCTION
        */

        /*SharedPreferences.Editor editor = prefs.edit();
        editor.remove(getString(R.string.AUTH_TOKEN));
        editor.commit();

        Log.i("SplashScreen","Auth Token Reset For Development ");*/

        //BaseActivity.putInPref(prefs, getString(R.string.PREVIOUS_ACTIVITY),getString(R.string.PREVIOUS_ACTIVITY_INTRO));

        /*
        *
        *
        *
        * */
        String value = prefs.getString(getString(R.string.PREVIOUS_ACTIVITY),"none");
        String token = prefs.getString(getString(R.string.AUTH_TOKEN),"none");

        Log.i("SplashScreen","Activity Pref value:"+value);
        Log.i("SplashScreen","Token Pref value:"+token);



        if (token == "none" || value == "none" || value.equals(getString(R.string.PREVIOUS_ACTIVITY_INTRO))) {
            if (token == "none")
                Log.i("SplashScreen/checkPreviousActivity", "Access Token is null. Proceed to Intro");

            //Save Previous activity to shared preference
            BaseActivity.putInPref(prefs, getString(R.string.PREVIOUS_ACTIVITY), getString(R.string.PREVIOUS_ACTIVITY_INTRO));
            activity = new Intent(getBaseContext(), IntroActivity.class);
        } else if (value.equals(getString(R.string.PREVIOUS_ACTIVITY_BATTLE)))
            activity = new Intent(getBaseContext(), BattleActivity.class);
        else if (value.equals(getString(R.string.PREVIOUS_ACTIVITY_MAIN)))
            activity = new Intent(getBaseContext(), MainActivity.class);
        else if (value.equals(getString(R.string.PREVIOUS_ACTIVITY_GRID)))
            activity = new Intent(getBaseContext(), GridActivity.class);
        else if (value.equals(getString(R.string.PREVIOUS_ACTIVITY_FRIEND)))
            activity = new Intent(getBaseContext(), MainActivity.class);
        else if (value.equals(getString(R.string.PREVIOUS_ACTIVITY_INVITE)))
            activity = new Intent(getBaseContext(), MainActivity.class);
        //To be deleted !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! For Testing Friend
        //activity = new Intent(getBaseContext(), BattleActivity.class);

        if (activity!=null){
            Log.v("SplashScreen","Activity Start");

            if (bundle!=null)
                activity.putExtras(bundle);

            startActivity(activity);
            return true;
        }
        return false;


    }



}