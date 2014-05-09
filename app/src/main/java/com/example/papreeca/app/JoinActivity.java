package com.example.papreeca.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class JoinActivity extends BaseActivity {
    private LinearLayout verificationLayout;
    private LinearLayout joinLayout;
    private boolean isVerificationLayoutInflated = false;
    private final int JOIN_LAYOUT = 1;
    private final int VERI_LAYOUT = 2;
    private int currentScreen = JOIN_LAYOUT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        verificationLayout = (LinearLayout)findViewById(R.id.verification_layout);
        joinLayout = (LinearLayout)findViewById(R.id.join_layout);

        hideActionBar();
        configSendButton();

    }

    @Override
    public void onBackPressed() {
        if (currentScreen == JOIN_LAYOUT)
            super.onBackPressed();
        else
            goBackHome();
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

    /*
    * When SendVerificationCode Button is pressed, inflates next frame
    * */

    private void configSendButton(){
        Button sendV = (Button) findViewById(R.id.send_verification_code);

        sendV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),R.string.verification_sent,Toast.LENGTH_LONG).show();

                if (!isVerificationLayoutInflated) {
                    inflateVerificationLayout();
                    isVerificationLayoutInflated = true;
                }
            }
        });
    }

    /*
    * Inflates next frame - showing verification code textfield and buttons (phone_verification.xml)
    * */

    private void inflateVerificationLayout(){
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mInflater.inflate(R.layout.phone_verification, verificationLayout,true);
        verificationLayout.setVisibility(View.VISIBLE);
        joinLayout.setVisibility(View.INVISIBLE);
        currentScreen = VERI_LAYOUT;


        configVeriConfirmButton();
        configVeriAgainButton();
        configVeriResendButton();
    }

    /*
    * Config Verification Confirm Button that goes to next activity
    * */

    private void configVeriConfirmButton(){
        Button veriConfirmButton = (Button) findViewById(R.id.verification_confirm_button);

        veriConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"인증되었습니다",Toast.LENGTH_LONG).show();

            }
        });
    }

    /*
    * Config Verification Again Button that goes back to previous stage
    * */

    private void configVeriAgainButton(){
        TextView veriAgainButton = (TextView) findViewById(R.id.verification_again_button);

        veriAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationLayout.setVisibility(View.INVISIBLE);
                joinLayout.setVisibility(View.VISIBLE);
                currentScreen = JOIN_LAYOUT;
            }
        });
    }

    /*
    * Config Verification Resend Button that resends veri code sms
    * */
    private void configVeriResendButton(){
        TextView veriResendButton = (TextView) findViewById(R.id.verification_resend_button);
        veriResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);

                DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"문자를 재전송하였습니다",Toast.LENGTH_LONG).show();
                    }
                };

                final AlertDialog alert = builder.setPositiveButton(getString(R.string.dialog_positive),positiveListener)
                                                .setNegativeButton(getString(R.string.dialog_negative),null)
                                                .setTitle(getString(R.string.verification_dialog_title))
                                                .setMessage(getString(R.string.verification_dialog_message))
                                                .create();

                alert.show();

            }
        });
    }

}
