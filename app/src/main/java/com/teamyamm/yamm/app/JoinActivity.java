package com.teamyamm.yamm.app;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.TransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class JoinActivity extends BaseActivity {
    private LinearLayout joinLayout;
    private boolean enableButtonFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Log.i("JoinActivity/OnCreate", "JoinActivity onCreate");

        joinLayout = (LinearLayout)findViewById(R.id.join_layout);
        ((EditText) joinLayout.findViewById(R.id.pw_field)).setTransformationMethod(new HiddenPassTransformationMethod());


        configSendButton();
        configAgreementCheckBox();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.join_activity_actions, menu);

        if (enableButtonFlag) {
            menu.findItem(R.id.join_confirm_button).setEnabled(true);
        } else {
            menu.findItem(R.id.join_confirm_button).setEnabled(false);
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.join_confirm_button:
                Log.i("JoinActivity/OnOptionsItemSelected","Confirm Join Button Clicked");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setConfirmButtonEnabled(boolean b){
        if (enableButtonFlag != b) {
            enableButtonFlag = b;
            supportInvalidateOptionsMenu();
        }
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

    private void configAgreementCheckBox(){
        CheckBox agreementCheckBox = (CheckBox) joinLayout.findViewById(R.id.agreement_checkbox);
        TextView agreementTextView = (TextView) joinLayout.findViewById(R.id.agreement_textview);

        //Set span for blue color
        Spannable span = new SpannableString(getString(R.string.agreement_textview));
        span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        agreementTextView.setText(span);

    }

    private void configSendButton() {
        final Button sendVButton = (Button) findViewById(R.id.verification_button);
        final Button resendVButton = (Button) findViewById(R.id.verification_resend_button);

        sendVButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.verification_sent, Toast.LENGTH_SHORT).show();
                resendVButton.setVisibility(View.VISIBLE);
                sendVButton.setVisibility(View.GONE);
            }
        });

        resendVButton.setOnClickListener(new View.OnClickListener() {
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
