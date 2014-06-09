package com.teamyamm.yamm.app;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
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

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


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
        setActionBarBackButton(true);

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
                boolean validInput = true;

                //If all input is valid and registration is complete

                if (validInput){
                    postRegistrationToServer();
                }

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
    private void postRegistrationToServer(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.yamm.me")
                .build();

        YammAPIService service = restAdapter.create(YammAPIService.class);

        String name = ((EditText) joinLayout.findViewById(R.id.name_field)).getText().toString();
        String email = ((EditText) joinLayout.findViewById(R.id.email_field)).getText().toString();
        String password = ((EditText) joinLayout.findViewById(R.id.pw_field)).getText().toString();
        String phone = ((EditText) joinLayout.findViewById(R.id.phone_field)).getText().toString();

        service.userRegistration(name, email, password, phone, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.i("JoinActivity/postRegistrationToServer","Registration " + s);
                goToActivity(GridActivity.class);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i("JoinActivity/postRegistrationToServer","Registration Failure ");
                retrofitError.printStackTrace();
            }
        });
    }

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

}
