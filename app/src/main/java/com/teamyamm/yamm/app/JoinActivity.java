package com.teamyamm.yamm.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class JoinActivity extends BaseActivity {
    private LinearLayout joinLayout;
    private boolean enableButtonFlag = false;
    private boolean flag_name = false, flag_email= false, flag_phone= false, flag_pwd= false, flag_veri = false, flag_checkbox = false;
    private CheckBox agreementCheckBox;
    private SmsListener smsListener;
    private Button joinConfirmButton;
    private EditText emailText, nameText, pwText, phoneText, veriText;
    private Dialog progressDialog;
    private int loginAttempt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Log.i("JoinActivity/OnCreate", "JoinActivity onCreate");

        joinLayout = (LinearLayout) findViewById(R.id.join_layout);
        ((EditText) joinLayout.findViewById(R.id.pw_field)).setTransformationMethod(new HiddenPassTransformationMethod());
        setActionBarBackButton(true);
        configSendButton();
        configAgreementCheckBox();
        configEditTexts();
        configJoinConfirmButton();
        getPhoneNumber();
        trackJoiningMixpanel();

    }

    private void getPhoneNumber(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phone = manager.getLine1Number();

        if (phone==null)
            return ;
        phone = MainActivity.parsePhoneNumber(phone);

        Log.i("JoinActivity/getPhoneNumber","Read Phone Number : " + phone);

        if (phone.length() > 9){
            //Set it on Phone Text
            phoneText.setText(phone);
        }
    }


    private void configJoinConfirmButton(){
        joinConfirmButton = (Button) findViewById(R.id.join_confirm_button);
        joinConfirmButton.setEnabled(false);

        joinConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("JoinActivity/configJoinConfirmButton","Confirm Join Button Clicked");
                if (agreementCheckBox.isChecked()){
                    postRegistrationToServer();
                }
                else{
                    makeYammToast(R.string.join_checkbox_message, Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        configSmsListener();
        //To Show Keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(smsListener);
        Log.i("JoinActivity/configSmsListener", "SMS Listener Unregistered");
    }

    public void setConfirmButtonEnabled(){
        boolean tmp = enableButtonFlag;
        enableButtonFlag = calculateFlag();
        if (enableButtonFlag != tmp) {
            joinConfirmButton.setEnabled(enableButtonFlag);
            if (enableButtonFlag){
                joinConfirmButton.setBackgroundResource(R.drawable.enabled_round_button);
                joinConfirmButton.setTextColor(getResources().getColor(R.color.button_enabled_text));
            }
            else{
                joinConfirmButton.setBackgroundResource(R.drawable.disabled_round_button);
                joinConfirmButton.setTextColor(getResources().getColor(R.color.button_disabled_text));
            }

        }
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////
    private void configSmsListener(){
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        // filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.setPriority(10000);
        smsListener = new SmsListener(((EditText) joinLayout.findViewById(R.id.verification_field)),
                ((ImageView) joinLayout.findViewById(R.id.veri_success_image)));
        registerReceiver(smsListener, filter);
        Log.i("JoinActivity/configSmsListener","SMS Listener Registered");
    }


    private boolean calculateFlag(){
        return flag_email && flag_name && flag_pwd && flag_phone && flag_veri && flag_checkbox;
    }

    private void configEditTexts(){

        nameText = (EditText) joinLayout.findViewById(R.id.name_field);
        nameText.addTextChangedListener(new TextWatcher() {
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

        emailText = (EditText) joinLayout.findViewById(R.id.email_field);
        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flag_email = !(s.length() == 0);
                setConfirmButtonEnabled();

                ((ImageView)findViewById(R.id.email_field_error)).setVisibility(View.GONE);
                emailText.setTextColor(getResources().getColor(R.color.join_text_color));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pwText = (EditText) joinLayout.findViewById(R.id.pw_field);
        pwText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flag_pwd = !(s.length() == 0);
                setConfirmButtonEnabled();

                ((ImageView)findViewById(R.id.pw_field_error)).setVisibility(View.GONE);
                pwText.setTextColor(getResources().getColor(R.color.join_text_color));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phoneText = (EditText) joinLayout.findViewById(R.id.phone_field);
        phoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flag_phone = !(s.length() == 0);
                setConfirmButtonEnabled();

                Button b = (Button) findViewById(R.id.verification_button);
                if (s.toString().length() < 10){
                    b.setBackgroundResource(R.drawable.veri_disabled_rounded_button);
                    b.setTextColor(getResources().getColor(R.color.button_disabled_text));

                }
                else{
                    b.setBackgroundResource(R.drawable.veri_enabled_rounded_button);
                    b.setTextColor(getResources().getColor(R.color.button_enabled_text));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        veriText = (EditText) joinLayout.findViewById(R.id.verification_field);
        veriText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flag_veri = !( s.length() == 0 );
                setConfirmButtonEnabled();

                ((ImageView)findViewById(R.id.veri_success_image)).setVisibility(View.GONE);
                ((ImageView)findViewById(R.id.veri_field_error)).setVisibility(View.GONE);
                veriText.setTextColor(getResources().getColor(R.color.join_text_color));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void postRegistrationToServer(){
        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = pwText.getText().toString();
        String phone = phoneText.getText().toString();
        String veri = veriText.getText().toString();
        final ImageView emailError = (ImageView) findViewById(R.id.email_field_error);
        final ImageView pwError = (ImageView) findViewById(R.id.pw_field_error);
        final ImageView veriError = (ImageView) findViewById(R.id.veri_field_error);
        final ImageView veriSuccess = (ImageView)findViewById(R.id.veri_success_image);

        Log.i("JoinActivity/postRegistrationToServer", name + "/" + email + "/" + password + "/" + phone);

        YammAPIService service = YammAPIAdapter.getJoinService();

        //To Prevent Double Fire
        joinConfirmButton.setEnabled(false);
        progressDialog = createFullScreenDialog(JoinActivity.this, getString(R.string.join_progress_dialog_title));
        if (checkIfAppIsRunning())
            progressDialog.show();

        service.userRegistration(name.toString(), email.toString(), password.toString(), phone.toString(), veri.toString(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.i("JoinActivity/postRegistrationToServer", "Registration " + s);
                setMixpanelAlias();
                logInAfterJoin();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String msg = retrofitError.getCause().getMessage();
                Log.e("JoinActivity/userRegistration", "ERROR CODE " + msg);

                //To Prevent Double Fire
                joinConfirmButton.setEnabled(true);
                progressDialog.dismiss();


                if (msg.equals(YammAPIService.YammRetrofitException.NETWORK))
                    makeYammToast(getString(R.string.network_error_message), Toast.LENGTH_SHORT);
                else if (msg.equals(YammAPIService.YammRetrofitException.DUPLICATE_ACCOUNT)) {
                    makeYammToast(getString(R.string.duplicate_account_error_message), Toast.LENGTH_SHORT);
                    phoneText.setTextColor(getResources().getColor(R.color.error_color));
                    emailText.setTextColor(getResources().getColor(R.color.error_color));
                    emailError.setVisibility(View.VISIBLE);
                }
                else if (msg.equals(YammAPIService.YammRetrofitException.INCORRECT_AUTHCODE)) {
                    makeYammToast(getString(R.string.incorrect_authcode_error_message), Toast.LENGTH_SHORT);
                    veriText.setTextColor(getResources().getColor(R.color.error_color));
                    veriSuccess.setVisibility(View.GONE);
                    veriError.setVisibility(View.VISIBLE);
                }
                else if (msg.equals(YammAPIService.YammRetrofitException.EMAIL_FORMAT)) {
                    makeYammToast(getString(R.string.email_format_error_message), Toast.LENGTH_SHORT);
                    emailText.setTextColor(getResources().getColor(R.color.error_color));
                    emailError.setVisibility(View.VISIBLE);
                }
                else if (msg.equals(YammAPIService.YammRetrofitException.PASSWORD_FORMAT)) {
                    makeYammToast(getString(R.string.password_format_error_message), Toast.LENGTH_SHORT);
                    pwText.setTextColor(getResources().getColor(R.color.error_color));
                    pwError.setVisibility(View.VISIBLE);
                }
                else if (msg.equals(YammAPIService.YammRetrofitException.PASSWORD_MIN)) {
                    makeYammToast(getString(R.string.password_min_error_message), Toast.LENGTH_SHORT);
                    pwText.setTextColor(getResources().getColor(R.color.error_color));
                    pwError.setVisibility(View.VISIBLE);
                }
                else if (msg.equals(YammAPIService.YammRetrofitException.PHONE_FORMAT)) {
                    makeYammToast(getString(R.string.phone_number_error_message), Toast.LENGTH_SHORT);
                    phoneText.setTextColor(getResources().getColor(R.color.error_color));
                }
                else
                    makeYammToast(getString(R.string.unidentified_error_message), Toast.LENGTH_SHORT);
            }
        });
    }

    private void logInAfterJoin(){
        String email = ((EditText) joinLayout.findViewById(R.id.email_field)).getText().toString();
        String password = ((EditText) joinLayout.findViewById(R.id.pw_field)).getText().toString();

        YammAPIService service = YammAPIAdapter.getLoginService(email, password);

        service.userLogin(new YammAPIService.GrantType(), new Callback<YammAPIService.YammToken>() {
            @Override
            public void success(YammAPIService.YammToken yammToken, Response response) {
                Log.i("LoginActivity/userLogin", "Logged in " + yammToken);
                progressDialog.hide();

                //Save Token to Shared Pref
                SharedPreferences prefs = getSharedPreferences(packageName, MODE_PRIVATE);
                putInPref(prefs, getString(R.string.AUTH_TOKEN), yammToken.toString());
                YammAPIAdapter.setToken(yammToken.toString());

                //Get Push Token
                registerGCM();

                //Move onto Next Activity
                goToActivity(GridActivity.class);

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("JoinActivity/userLogin", "ERROR " + retrofitError.getCause().toString());

                if(loginAttempt < 6){
                    loginAttempt++;

                    Log.e("JoinActivity/userLogin", "Handling Error Retrying for " +loginAttempt + " times");

                    String msg = "";
                    if (retrofitError.getMessage()!=null){
                        msg = retrofitError.getMessage();
                    }

                    trackLoginErrorMixpanel(msg, loginAttempt);

                    logInAfterJoin();
                    return ;
                }

                progressDialog.dismiss();


                if (retrofitError.isNetworkError())
                    makeYammToast(getString(R.string.network_error_message), Toast.LENGTH_SHORT);
                else
                    makeYammToast(getString(R.string.unidentified_error_message), Toast.LENGTH_SHORT);


            }
        });
    }

    private void configAgreementCheckBox(){
        agreementCheckBox = (CheckBox) joinLayout.findViewById(R.id.agreement_checkbox);

        agreementCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    hideSoftKeyboard(JoinActivity.this);
                    flag_checkbox = true;
                }
                else
                    flag_checkbox = false;
                setConfirmButtonEnabled();
            }
        });

        TextView agreementTextView = (TextView) joinLayout.findViewById(R.id.agreement_textview);

        //Set span for blue color
        Spannable span = new SpannableString(getString(R.string.agreement_textview));
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.agreement_link_color)), 0, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        agreementTextView.setText(span);

        agreementTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeYammToast(R.string.left_drawer_not_ready ,Toast.LENGTH_SHORT);
            }
        });

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
                    makeYammToast(R.string.phone_number_error_message, Toast.LENGTH_SHORT);
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

                View.OnClickListener positiveListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideSoftKeyboard(JoinActivity.this);
                        String phone = ((EditText) joinLayout.findViewById(R.id.phone_field)).getText().toString();
                        sendVeriMessage(phone);
                        dismissCurrentDialog();
                    }
                };
                createDialog(JoinActivity.this, R.string.verification_resend_dialog_title, R.string.verification_resend_dialog_message,
                            R.string.dialog_positive, R.string.dialog_negative, positiveListener, null).show();
                }

        });
    }

    private void sendVeriMessage(String phone){
        YammAPIService service = YammAPIAdapter.getService();

        service.phoneVerification(phone, new Callback<YammAPIService.VeriExp>() {
            @Override
            public void success(YammAPIService.VeriExp s, Response response) {
                Log.i("JoinActivity/getVeriDialogPositiveListener", "VeriExpires at " + s);
                makeYammToast(R.string.verification_sent, Toast.LENGTH_SHORT);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("JoinActivity/getVeriDialogPositiveListener", "Veri Failed ");
                retrofitError.printStackTrace();
                makeYammToast(R.string.verification_error_message, Toast.LENGTH_SHORT);
            }
        });
    }

    private void setMixpanelAlias(){
        mixpanel.alias(emailText.getText().toString(), null);
        Log.i("JoinActivity/setMixpanelAlias","Mixpanel - Setting Unique ID with email "+ emailText.getText().toString());

        mixpanel.getPeople().identify(emailText.getText().toString());
        mixpanel.getPeople().set("$email",emailText.getText().toString());
        Log.i("JoinActivity/setMixpanelAlias","Mixpanel - Setting Name for Account"+ emailText.getText().toString());
    }

    private void trackLoginErrorMixpanel(String errorMessage, int count){
        JSONObject props = new JSONObject();
        try {
            props.put("count", count);
            props.put("Message", errorMessage);
        }catch(JSONException e){
            Log.e("JoinActivity/trackLoginErrorMixpanel","JSON Error");
        }
        mixpanel.track("Login Error", props);
        Log.i("JoinActivity/trackJoiningMixpanel","Login Error Tracked");
    }

    private void trackJoiningMixpanel(){
        JSONObject props = new JSONObject();
        mixpanel.track("Joining", props);
        Log.i("JoinActivity/trackJoiningMixpanel","Joining Tracked");
    }
}
