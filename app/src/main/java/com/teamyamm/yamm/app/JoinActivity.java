package com.teamyamm.yamm.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class JoinActivity extends BaseActivity {
    private LinearLayout joinLayout;
    private boolean enableButtonFlag = false;
    private boolean flag_name = false, flag_email= false, flag_phone= false, flag_pwd= false, flag_veri = false;
    private CheckBox agreementCheckBox;
    private SmsListener smsListener;

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
        configEditTexts();
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


                if (agreementCheckBox.isChecked()){
                    hideSoftKeyboard(this);
                    postRegistrationToServer();
                    //goToActivity(GridActivity.class);
                }
                else{
                    hideSoftKeyboard(this);
                    Toast.makeText(this, R.string.join_checkbox_message, Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        configSmsListener();
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(smsListener);
        Log.i("JoinActivity/configSmsListener","SMS Listener Unregistered");
    }

    public void setConfirmButtonEnabled(){
        boolean tmp = enableButtonFlag;
        enableButtonFlag = calculateFlag();
        if (enableButtonFlag != tmp) {
            supportInvalidateOptionsMenu();
        }
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////
    private void configSmsListener(){
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
       // filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.setPriority(10000);
        smsListener = new SmsListener(((EditText) joinLayout.findViewById(R.id.verification_field)));
        registerReceiver(smsListener, filter);
        Log.i("JoinActivity/configSmsListener","SMS Listener Registered");
    }


    private boolean calculateFlag(){
        return flag_email && flag_name && flag_pwd && flag_phone && flag_veri;
    }

    private void configEditTexts(){
        ((EditText) joinLayout.findViewById(R.id.name_field)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flag_name = !( s.length() == 0 );
                setConfirmButtonEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ((EditText) joinLayout.findViewById(R.id.email_field)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flag_email = !(s.length() == 0);
                setConfirmButtonEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ((EditText) joinLayout.findViewById(R.id.pw_field)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flag_pwd = !(s.length() == 0);
                setConfirmButtonEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ((EditText) joinLayout.findViewById(R.id.phone_field)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flag_phone = !(s.length() == 0);
                setConfirmButtonEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ((EditText) joinLayout.findViewById(R.id.verification_field)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flag_veri = !( s.length() == 0 );
                setConfirmButtonEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void postRegistrationToServer(){
        String name = ((EditText) joinLayout.findViewById(R.id.name_field)).getText().toString();
        String email = ((EditText) joinLayout.findViewById(R.id.email_field)).getText().toString();
        String password = ((EditText) joinLayout.findViewById(R.id.pw_field)).getText().toString();
        String phone = ((EditText) joinLayout.findViewById(R.id.phone_field)).getText().toString();
        String veri = ((EditText) joinLayout.findViewById(R.id.verification_field)).getText().toString();

        Log.i("JoinActivity/postRegistrationToServer", name + "/" + email + "/" + password + "/" + phone);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiURL)
                .setErrorHandler(new JoinErrorHandler())
                .setLog(setRestAdapterLog())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        YammAPIService service = restAdapter.create(YammAPIService.class);

        service.userRegistration(name.toString(), email.toString(), password.toString(), phone.toString(), veri.toString(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.i("JoinActivity/postRegistrationToServer", "Registration " + s);
                logInAfterJoin();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String msg = retrofitError.getCause().getMessage();
                Log.e("JoinActivity/userRegistration", "ERROR CODE " + msg);

                if (msg.equals(YammAPIService.YammRetrofitException.NETWORK))
                    Toast.makeText(getApplicationContext(), getString(R.string.network_error_message), Toast.LENGTH_LONG).show();
                else if (msg.equals(YammAPIService.YammRetrofitException.DUPLICATE_ACCOUNT))
                    Toast.makeText(getApplicationContext(), getString(R.string.duplicate_account_error_message), Toast.LENGTH_LONG).show();
                else if (msg.equals(YammAPIService.YammRetrofitException.INCORRECT_AUTHCODE))
                    Toast.makeText(getApplicationContext(), getString(R.string.incorrect_authcode_error_message), Toast.LENGTH_LONG).show();
                else if (msg.equals(YammAPIService.YammRetrofitException.EMAIL_FORMAT))
                    Toast.makeText(getApplicationContext(), getString(R.string.email_format_error_message), Toast.LENGTH_LONG).show();
                else if (msg.equals(YammAPIService.YammRetrofitException.PASSWORD_FORMAT))
                    Toast.makeText(getApplicationContext(), getString(R.string.password_format_error_message), Toast.LENGTH_LONG).show();
                else if (msg.equals(YammAPIService.YammRetrofitException.PASSWORD_MIN))
                    Toast.makeText(getApplicationContext(), getString(R.string.password_min_error_message), Toast.LENGTH_LONG).show();
                else if (msg.equals(YammAPIService.YammRetrofitException.PHONE_FORMAT))
                    Toast.makeText(getApplicationContext(), getString(R.string.phone_number_error_message), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.unidentified_error_message), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logInAfterJoin(){
        String email = ((EditText) joinLayout.findViewById(R.id.email_field)).getText().toString();
        String password = ((EditText) joinLayout.findViewById(R.id.pw_field)).getText().toString();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiURL)
                .setLog(setRestAdapterLog())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(LoginActivity.setRequestInterceptorForLogin(email, password))
                .build();

        YammAPIService service = restAdapter.create(YammAPIService.class);

        service.userLogin(new YammAPIService.GrantType(), new Callback<YammAPIService.YammToken>() {
            @Override
            public void success(YammAPIService.YammToken yammToken, Response response) {
                Log.i("LoginActivity/userLogin", "Logged in " + yammToken);

                //Save Token to Shared Pref
                SharedPreferences prefs = getSharedPreferences(packageName, MODE_PRIVATE);
                putInPref(prefs, getString(R.string.AUTH_TOKEN), yammToken.toString());

                //Move onto Next Activity
                goToActivity(GridActivity.class);

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("JoinActivity/userLogin", "ERROR");

                if (retrofitError.isNetworkError())
                    Toast.makeText(getApplicationContext(), getString(R.string.network_error_message), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.unidentified_error_message), Toast.LENGTH_LONG).show();


            }
        });
    }

    private void configAgreementCheckBox(){
        agreementCheckBox = (CheckBox) joinLayout.findViewById(R.id.agreement_checkbox);
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
                hideSoftKeyboard(JoinActivity.this);

                String phone = ((EditText) joinLayout.findViewById(R.id.phone_field)).getText().toString();

                if (phone!=null && (phone.length() == 10 || phone.length() == 11)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);

                    AlertDialog alert = builder.setPositiveButton(getString(R.string.dialog_positive), getVeriDialogPositiveListener())
                            .setNegativeButton(getString(R.string.dialog_negative), null)
                            .setTitle(getString(R.string.verification_dialog_title))
                            .setMessage(getVeriDialogMessage(phone))
                            .create();
                    alert.show();

                    //Center Text
                    TextView messageView = (TextView) alert.findViewById(android.R.id.message);
                    messageView.setGravity(Gravity.CENTER);
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.phone_number_error_message, Toast.LENGTH_SHORT).show();
                }
            }

            // Set Message
            private String getVeriDialogMessage(String phone){
                return phoneNumberFormat(phone) + "\n\n" + getString(R.string.verification_dialog_message);
            }

            // Positive OnClick Listener
            private DialogInterface.OnClickListener getVeriDialogPositiveListener(){
                return new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phone = ((EditText) joinLayout.findViewById(R.id.phone_field)).getText().toString();
                        Log.i("JoinActivity/getVeriDialogPositiveListener", "Verification API Called for " + phone);
                        resendVButton.setVisibility(View.VISIBLE);
                        sendVButton.setVisibility(View.GONE);

                        sendVeriMessage(phone);
                    }
                };
            }
        });

        resendVButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideSoftKeyboard(JoinActivity.this);
                        String phone = ((EditText) joinLayout.findViewById(R.id.phone_field)).getText().toString();
                        sendVeriMessage(phone);
                    }
                };

                createDialog(JoinActivity.this, R.string.verification_resend_dialog_title, R.string.verification_resend_dialog_message,
                        R.string.dialog_positive, R.string.dialog_negative,positiveListener, null).show();
            }
        });
    }

    private void sendVeriMessage(String phone){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiURL)
                .build();
        YammAPIService service = restAdapter.create(YammAPIService.class);

        service.phoneVerification(phone, new Callback<YammAPIService.VeriExp>() {
            @Override
            public void success(YammAPIService.VeriExp s, Response response) {
                Log.i("JoinActivity/getVeriDialogPositiveListener", "VeriExpires at " + s);
                Toast.makeText(getApplicationContext(), R.string.verification_sent, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("JoinActivity/getVeriDialogPositiveListener", "Veri Failed " + retrofitError.getBody());
                retrofitError.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.verification_error_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Error Handler
    public class JoinErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError cause) {
            Response r = cause.getResponse();

            if (cause.isNetworkError()){
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.NETWORK);
            }
            YammAPIService.YammRetrofitError error = new YammAPIService.YammRetrofitError();
            Gson gson = new Gson();

            error = gson.fromJson(responseToString(r), error.getClass());
            Log.e("JoinErrorHandler/handleError",error.getMessage());


            if (error.getCode().equals("DuplicateAccount")) {
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.DUPLICATE_ACCOUNT);
            }
            else if (error.getCode().equals("IncorrectAuthCode"))
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.INCORRECT_AUTHCODE);
            else if (error.getCode().equals("InvalidParam:password:numchars"))
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.PASSWORD_MIN);
            else if (error.getCode().equals("InvalidParam:password:specialchars"))
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.PASSWORD_FORMAT);
            else if (error.getCode().equals("InvalidParam:email"))
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.EMAIL_FORMAT);
            else if (error.getCode().equals("InvalidParam:phone"))
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.PHONE_FORMAT);

            Log.e("JoinErrorHandler/handleError", "Handling Unidentified Error");
            return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
        }

        private String responseToString(Response result){
            //Try to get response body
            BufferedReader reader = null;
            StringBuilder sb = new StringBuilder();
            try {

                reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                String line;

                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return sb.toString();
        }
    }


}
