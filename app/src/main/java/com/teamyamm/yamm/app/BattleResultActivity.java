package com.teamyamm.yamm.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class BattleResultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_result);

        hideActionBar();
        configButtons();
        loadBattleResultImage();
    }

    /*
    * Try Battle Again When Back Button of BattleResult
    * */
    @Override
    public void onBackPressed() {
        tryAgainDialog();
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

    private void loadBattleResultImage(){
        ImageView imageView = (ImageView) findViewById(R.id.battle_result);
        String imageURL = "http://d2ivf9obbiisa4.cloudfront.net/dish/1";

        Picasso.with(getApplicationContext()).load(imageURL).into(imageView);

    }


    private void configButtons() {
        Button joinUsButton = (Button) findViewById(R.id.battle_result_joinus);
        Button facebookButton = (Button) findViewById(R.id.battle_result_facebook);
        Button tryAgainButton = (Button) findViewById(R.id.battle_result_tryagain);

        //Join Us

        joinUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(JoinActivity.class);
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
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToActivity(BattleActivity.class);
            }
        };

        createDialog(BattleResultActivity.this, R.string.battleagain_dialog_title, R.string.battleagain_dialog_message,
                R.string.battleagain_dialog_positive, R.string.battleagain_dialog_negative,positiveListener, null).show();

    }


}
