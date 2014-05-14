package com.teamyamm.yamm.app;

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
    private int currentFrame = JOIN_LAYOUT;

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
        if (currentFrame == JOIN_LAYOUT)
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
                Toast.makeText(getApplicationContext(),R.string.verification_sent,Toast.LENGTH_SHORT).show();

                if (!isVerificationLayoutInflated) {
                    inflateVerificationLayout();
                    isVerificationLayoutInflated = true;
                }
                changeFrame();
            }
        });
    }

    /*
    * Changes Frame
    * */
    private void changeFrame(){
        if (currentFrame==JOIN_LAYOUT){
            verificationLayout.setVisibility(View.VISIBLE);
            joinLayout.setVisibility(View.INVISIBLE);
            currentFrame = VERI_LAYOUT;
        }
        else{
            verificationLayout.setVisibility(View.INVISIBLE);
            joinLayout.setVisibility(View.VISIBLE);
            currentFrame = JOIN_LAYOUT;
        }
    }

    /*
    * Inflates next frame - showing verification code textfield and buttons (phone_verification.xml)
    * */

    private void inflateVerificationLayout(){
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mInflater.inflate(R.layout.phone_verification, verificationLayout,true);

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
                Toast.makeText(getApplicationContext(),"인증되었습니다",Toast.LENGTH_SHORT).show();

                goToActivity(MainActivity.class);
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
                changeFrame();
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

                DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), getString(R.string.verification_resend_message),Toast.LENGTH_SHORT).show();
                    }
                };

                createDialog(JoinActivity.this, R.string.verification_dialog_title, R.string.verification_dialog_message,
                        R.string.dialog_positive, R.string.dialog_negative,positiveListener, null).show();
            }
        });
    }

}
