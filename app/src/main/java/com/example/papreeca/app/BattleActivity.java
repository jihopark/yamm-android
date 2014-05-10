package com.example.papreeca.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class BattleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        hideActionBar();
        introButtonConfig();
    }

    /*
    * Go to Home Screen When Back Button of IntroActivity
    * */
    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Go to IntroActivity
                Intent introActivity = new Intent(getBaseContext(), IntroActivity.class);
                introActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(introActivity);
                goBackHome();
            }
        };

        createDialog(BattleActivity.this, R.string.battle_dialog_title, R.string.battle_dialog_message,
                R.string.dialog_positive, R.string.dialog_negative,positiveListener, null).show();
    }


    ////////////////////////////////Private Methods/////////////////////////////////////////////////

    /*
    * Setup OnclickListener for button that finishes battle
    * */
    private void introButtonConfig(){
        Button battleButton = (Button) findViewById(R.id.battle_button);
        battleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "배틀 끝", Toast.LENGTH_SHORT).show(); //To be deleted

                //Save Preference that finished first run
                SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);        //Save preference that INTRO was run once
                prefs.edit().putBoolean("firstrun", false).commit();

                // Send Battle Result to Server

                //Go to BattleResult Activity
                Intent battleResultActivity = new Intent(getBaseContext(), BattleResult.class);
                startActivity(battleResultActivity);
            }
        });
    }

}
