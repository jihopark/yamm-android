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
    }

    /*
    * Try Battle Again When Back Button of BattleResult
    * */
    @Override
    public void onBackPressed() {
        tryAgainDialog();
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

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
