package com.example.papreeca.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class BattleResult extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_result);

        configButtons();
    }

    /*
    * Try Battle Again When Back Button of BattleResult
    * */
    @Override
    public void onBackPressed() {
        tryAgainDialog();
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

    private void configButtons() {
        Button joinUsButton = (Button) findViewById(R.id.battle_result_joinus);
        Button facebookButton = (Button) findViewById(R.id.battle_result_facebook);
        Button tryAgainButton = (Button) findViewById(R.id.battle_result_tryagain);

        //Join Us

        joinUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to JoinActivity
                Intent joinActivity = new Intent(getBaseContext(), JoinActivity.class);
                startActivity(joinActivity);
            }
        });

        //Facebook

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        //Try Again
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryAgainDialog();
            }
        });
    }

    private void tryAgainDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("한번 더?");
        builder.setMessage("다시 한번 해보시겠습니까?");
        final AlertDialog alert = builder.setPositiveButton("한번 더", null)
                .setNegativeButton("이제 그만", null)
                .create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Go back to BattleActivity
                        Intent battleActivity = new Intent(getBaseContext(), BattleActivity.class);
                        startActivity(battleActivity);
                    }
                });
            }
        });

        alert.show();
    }


}
