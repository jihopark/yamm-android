package com.teamyamm.yamm.app;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class JoinActivity extends BaseActivity {
    private LinearLayout joinLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Log.i("JoinActivity/OnCreate", "JoinActivity onCreate");

        joinLayout = (LinearLayout)findViewById(R.id.join_layout);

        ((EditText) joinLayout.findViewById(R.id.pw_field)).setTransformationMethod(new HiddenPassTransformationMethod());
        configSendButton();

    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

    /*
    * When SendVerificationCode Button is pressed, inflates next frame
    * */

    private void configSendButton() {
        Button sendV = (Button) findViewById(R.id.send_verification_code);

        sendV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.verification_sent, Toast.LENGTH_SHORT).show();


            }
        });
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

    /*
    * To show the last character of password
    * */
    private class HiddenPassTransformationMethod implements TransformationMethod {

        private char DOT = '\u2022';

        @Override
        public CharSequence getTransformation(final CharSequence charSequence, final View view) {
            return new PassCharSequence(charSequence);
        }

        @Override
        public void onFocusChanged(final View view, final CharSequence charSequence, final boolean b, final int i,
                                   final Rect rect) {
            //nothing to do here
        }

        private class PassCharSequence implements CharSequence {

            private final CharSequence charSequence;

            public PassCharSequence(final CharSequence charSequence) {
                this.charSequence = charSequence;
            }

            @Override
            public char charAt(final int index) {
                if (index == length() - 1)
                    return charSequence.charAt(index);
                return DOT;
            }

            @Override
            public int length() {
                return charSequence.length();
            }

            @Override
            public CharSequence subSequence(final int start, final int end) {
                return new PassCharSequence(charSequence.subSequence(start, end));
            }
        }
    }

}
