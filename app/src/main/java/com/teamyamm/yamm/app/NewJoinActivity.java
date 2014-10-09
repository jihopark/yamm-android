package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.kakao.APIErrorResult;
import com.kakao.MeResponseCallback;
import com.kakao.SessionCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.exception.KakaoException;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.network.YammAPIService;
import com.teamyamm.yamm.app.util.SmsListener;
import com.teamyamm.yamm.app.widget.PhoneAuthFragment;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 9/29/14.
 */
public class NewJoinActivity extends BaseActivity {
    private final static String PHONE = "PH";
    private final static String NAME = "NA";
    private final static String PW = "PW";

    private EditText nameField, phoneField, pwField;
    private Button phoneAuthRequest;

    private SmsListener smsListener;
    private int authType;

    private boolean isNameDone = false, isPhoneDone = false, isPWDone = false;

    private boolean enablePhoneAuthRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivity();
        setContentView(R.layout.activity_new_join);
        setActionBarBackButton(true);

        nameField = (EditText) findViewById(R.id.name_field);
        phoneField = (EditText) findViewById(R.id.phone_field);
        pwField = (EditText) findViewById(R.id.password_field);
        phoneAuthRequest = (Button) findViewById(R.id.phone_auth_request);

        phoneField.setText(getPhoneNumber());

        configNameEditText();
        configPhoneEditText();
        configPWField();
        configPhoneAuthRequest();
   //     configSendButton();

        MixpanelController.trackEnteredPhoneAuthMixpanel();
    }

    private void initActivity(){
        if (getIntent().getExtras()!=null){
            authType = getIntent().getExtras().getInt(IntroActivity.AUTH_TYPE);
            Log.i("NewJoinActivity/initActivity", "Auth Type is " + authType);
        }
    }

    @Override
    public void onBackPressed() {
        showBackWarningDialog();
    }

    @Override
    public void onDestroy(){
        removeOAuthSessions();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                showBackWarningDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showBackWarningDialog(){
        createDialog(NewJoinActivity.this, 0,
                R.string.phone_auth_back_dialog_message, R.string.dialog_positive, R.string.dialog_negative,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeOAuthSessions();
                    }
                },null).show();
    }

    private void removeOAuthSessions(){
        if (authType==IntroActivity.FB){
            if (Session.getActiveSession()!=null)
                Session.getActiveSession().closeAndClearTokenInformation();
        }
        else if (authType==IntroActivity.KAKAO){
            if (com.kakao.Session.getCurrentSession() != null) {
                com.kakao.Session.getCurrentSession().close(new SessionCallback() {
                    @Override
                    public void onSessionOpened() {

                    }

                    @Override
                    public void onSessionClosed(KakaoException e) {
                        Log.d("NewJoinActivity/onSessionClosed","Kakao Session Closed");
                    }
                });
            }
        }
        goToActivity(IntroActivity.class);
    }

    @Override
    public void onResume(){
        super.onResume();
        configSmsListener();
    }

    @Override
    public void onPause(){
        super.onPause();
       // unregisterReceiver(smsListener);

    }

    private void configPWField(){
        if (authType!=IntroActivity.PW) {
            pwField.setVisibility(View.GONE);
            isPWDone = true;
        }
        else {
            pwField.setTransformationMethod(new HiddenPassTransformationMethod());
            pwField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().length() >= 8)
                        isPWDone = true;
                    else
                        isPWDone = false;
                    Log.d("NewJoinActivity/onTextChanged","PW Done " + isPWDone);
                    setPhoneAuthButtonEnable();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    private void configPhoneAuthRequest(){
        phoneAuthRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = phoneNumberFormat(phoneField.getText().toString()) + "\n" + getString(R.string.verification_dialog_message);
                createDialog(NewJoinActivity.this, "", message,
                        getString(R.string.dialog_positive), getString(R.string.dialog_negative), getVeriDialogPositiveListener(), null ).show();
            }

            private View.OnClickListener getVeriDialogPositiveListener(){
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = phoneField.getText().toString();
                        Log.i("NewJoinActivity/getVeriDialogPositiveListener", "Verification API Called for " + phone);
                        sendVeriMessage(phone);
                        dismissCurrentDialog();
                        createPhoneAuthFragment();
                    }
                };
            }
        });
    }

    private void configNameEditText(){
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 1)
                    isNameDone = true;
                else
                    isNameDone = false;
                Log.d("NewJoinActivity/onTextChanged","Name Done " + isNameDone);
                setPhoneAuthButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (authType == IntroActivity.FB){
            if (Session.getActiveSession()!=null) {
                Log.d("NewJoinActivity/configNameEditText", "Fetching Name From FB");
                Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, com.facebook.Response response) {
                        if (response != null) {
                            try {
                                String name = user.getName();
                                // If you asked for email permission
                                Log.d("NewJoinActivity/configNameEditText", "Name Fetched from FB: " + name);
                                if (nameField!=null)
                                    nameField.setText(name);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("NewJoinActivity/configNameEditText", "Exception in loading FB Name");
                            }

                        }
                    }
                }).executeAsync();
            }
        }
        else if (authType == IntroActivity.KAKAO){
            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                protected void onSuccess(final UserProfile userProfile) {
                    String name = userProfile.getNickname();
                    Log.d("NewJoinActivity/configNameEditField", "Name Fetched from Kakao: " + name);
                    if (nameField!=null)
                        nameField.setText(name);

                }

                @Override
                protected void onNotSignedUp() {
                    // 가입 페이지로 이동
                }

                @Override
                protected void onSessionClosedFailure(final APIErrorResult errorResult) {
                    // 다시 로그인 시도
                }

                @Override
                protected void onFailure(final APIErrorResult errorResult) {
                    // 실패
                    Log.e("NewJoinActivity/configNameEditField","Failed to Retrieve Kakao Name");
                }
            });
        }

    }

    private void createPhoneAuthFragment(){
        PhoneAuthFragment fragment = new PhoneAuthFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PHONE,phoneField.getText().toString());
        bundle.putString(NAME,nameField.getText().toString());
        if (authType == IntroActivity.PW)
            bundle.putString(PW, pwField.getText().toString());

    }

    private void setPhoneAuthButtonEnable(){
        enablePhoneAuthRequest = isNameDone && isPhoneDone && isPWDone;
        Log.d("NewJoinActivity/setPhoneAuthButtonEnable","Enable Button " + enablePhoneAuthRequest);
        phoneAuthRequest.setEnabled(enablePhoneAuthRequest);
    }

    private void configPhoneEditText(){
        phoneField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 10)
                    isPhoneDone = false;
                else
                    isPhoneDone = true;
                Log.d("NewJoinActivity/onTextChanged","Phone Done " + isPhoneDone);
                setPhoneAuthButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phoneField.setText(getPhoneNumber());
        Button question = (Button) findViewById(R.id.phone_question);
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(NewJoinActivity.this, 0, R.string.phone_question_message, R.string.dialog_positive_formal, null).show();
            }
        });
    }

    private void configSmsListener(){
    /*    IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(10000);
        smsListener = new SmsListener(veriField, successImage);
        registerReceiver(smsListener, filter);
        Log.i("NewJoinActivity/configSmsListener", "SMS Listener Registered");*/
    }

  /*  private void configSendButton() {
        final Button sendVButton = (Button) findViewById(R.id.verification_button);
        final Button resendVButton = (Button) findViewById(R.id.verification_resend_button);
        Log.d("NewJoinActivity/configSendButton","Config");
        sendVButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("NewJoinActivity/onClick","Clicked");
                hideSoftKeyboard(NewJoinActivity.this);

                String phone = phoneField.getText().toString();
                    if (phone!=null && (phone.length() == 10 || phone.length() == 11)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(NewJoinActivity.this);

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
                        String phone = phoneField.getText().toString();
                        Log.i("NewJoinActivity/getVeriDialogPositiveListener", "Verification API Called for " + phone);
                        resendVButton.setVisibility(View.VISIBLE);
                        sendVButton.setVisibility(View.INVISIBLE);

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
                        hideSoftKeyboard(NewJoinActivity.this);
                        String phone = phoneField.getText().toString();
                        sendVeriMessage(phone);
                        dismissCurrentDialog();
                    }
                };
                createDialog(NewJoinActivity.this, R.string.verification_resend_dialog_title, R.string.verification_resend_dialog_message,
                        R.string.dialog_positive, R.string.dialog_negative, positiveListener, null).show();
            }

        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = createFullScreenDialog(NewJoinActivity.this, getString(R.string.progress_dialog_message));
                dialog.show();
                YammAPIAdapter.getJoinService().facebookRegistration(nameField.getText().toString(), Session.getActiveSession().getAccessToken(), veriField.getText().toString(), new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        //MixpanelController.setMixpanelAlias(email);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {

                    }
                });
            }
        });
    }*/

    private void sendVeriMessage(String phone){
        YammAPIService service = YammAPIAdapter.getService();

        service.phoneVerification(phone, new Callback<YammAPIService.VeriExp>() {
            @Override
            public void success(YammAPIService.VeriExp s, Response response) {
                Log.i("NewJoinActivity/getVeriDialogPositiveListener", "VeriExpires at " + s);
                makeYammToast(R.string.verification_sent, Toast.LENGTH_SHORT);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("NewJoinActivity/getVeriDialogPositiveListener", "Veri Failed ");
                retrofitError.printStackTrace();
                makeYammToast(R.string.verification_error_message, Toast.LENGTH_SHORT);
            }
        });
    }
}
