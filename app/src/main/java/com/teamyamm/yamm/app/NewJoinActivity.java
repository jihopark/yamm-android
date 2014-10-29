package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
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
    public final static String PHONE = "PH";
    public final static String NAME = "NA";
    public final static String PW = "PW";

    private final static String termsURL = "http://terms.yamm.me/";

    private EditText nameField, phoneField, pwField;
    private Button phoneAuthRequest;
    private PhoneAuthFragment fragment;

    private SmsListener smsListener;
    private int authType;
    private boolean isFragmentShown = false;

    private boolean isNameDone = false, isPhoneDone = false, isPWDone = false, isCheckDone = false;

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
        configAgreementCheckBox();

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
        if (isFragmentShown){
            createDialog(NewJoinActivity.this, 0,
                    R.string.phone_auth_fragment_back_dialog_message, R.string.dialog_positive, R.string.dialog_negative,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setTitle(R.string.title_activity_new_join);
                            FragmentTransaction tact = getSupportFragmentManager().beginTransaction();
                            if (fragment!=null)
                                tact.remove(fragment).commit();
                            enableEditTextsFocus(true);
                            fragment = null;
                            dismissCurrentDialog();
                            isFragmentShown = false;
                            unregisterSMSListener();
                        }
                    },null).show();
            return ;
        }

        createDialog(NewJoinActivity.this, 0,
                R.string.phone_auth_back_dialog_message, R.string.dialog_positive, R.string.dialog_negative,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissCurrentDialog();
                        removeOAuthSessions();
                        goToActivity(IntroActivity.class);
                    }
                },null).show();
    }

    private void enableEditTextsFocus(boolean b){
        Log.d("NewJoinActivity/enableEditTextsFocus","Text Focus " + b);
        nameField.setEnabled(b);
        pwField.setEnabled(b);
        phoneField.setEnabled(b);
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
    }

    public String getPassword(){
        if (authType == IntroActivity.PW){
            if (pwField!=null)
                return pwField.getText().toString();
        }
        return "";
    }

    @Override
    public void onResume(){
        super.onResume();
        if (isFragmentShown)
            configSmsListener(fragment.getEditText());
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterSMSListener();
    }

    private void unregisterSMSListener(){
        if (smsListener!=null) {
            Log.i("NewJoinActivity/onPause","SMS Listener Unregistered");
            try {
                unregisterReceiver(smsListener);
            }catch(IllegalArgumentException e){
                Log.e("NewJoinActivity/onPause","Already Registered");
            }catch(RuntimeException e){
                Log.e("NewJoinActivity/onPause","Already Registered");
            }
        }
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

    private boolean validateInputField(){
        if (authType!=IntroActivity.PW)
            return true;

        if (pwField.getText().toString().length() < 8)
            return false;

        return pwField.getText().toString().matches("^.*[0-9~!@#\\$%\\^&\\*\\(\\)_\\+\\-=` \\{}\\|\\[\\]\\\\:\";'<>\\?,\\.\\/].*$");
    }

    private void configPhoneAuthRequest(){
        phoneAuthRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputField()) {
                    String message = phoneNumberFormat(phoneField.getText().toString()) + "\n" + getString(R.string.verification_dialog_message);
                    createDialog(NewJoinActivity.this, "", message,
                            getString(R.string.dialog_positive), getString(R.string.dialog_negative), getVeriDialogPositiveListener(), null).show();
                }
                else{
                    makeYammToast(R.string.password_format_error_message, Toast.LENGTH_SHORT);
                }

            }

            private View.OnClickListener getVeriDialogPositiveListener(){
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = phoneField.getText().toString();
                        Log.i("NewJoinActivity/getVeriDialogPositiveListener", "Verification API Called for " + phone);
                        sendVeriMessage(phone);
                        dismissCurrentDialog();
                    }
                };
            }
        });
    }

    private void configAgreementCheckBox(){
        CheckBox agreementCheckBox = (CheckBox) findViewById(R.id.agreement_checkbox);

        agreementCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isCheckDone = true;
                }
                else
                    isCheckDone = false;
                setPhoneAuthButtonEnable();
            }
        });

        TextView agreementTextView = (TextView) findViewById(R.id.agreement_textview);

        //Set span for blue color
        Spannable span = new SpannableString(getString(R.string.agreement_textview));
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.agreement_link_color)), 0, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        agreementTextView.setText(span);

        agreementTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYammTermsWebPage();
            }
        });

    }

    private void showYammTermsWebPage(){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(termsURL));
        startActivity(i);
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

    public String getOAuthToken(){
        if (authType == IntroActivity.KAKAO){
            return com.kakao.Session.getCurrentSession().getAccessToken();
        }
        else if (authType==IntroActivity.FB){
            return Session.getActiveSession().getAccessToken();
        }
        return "";
    }

    private void createPhoneAuthFragment(){
        fragment = new PhoneAuthFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IntroActivity.AUTH_TYPE, authType);
        bundle.putString(PHONE,phoneField.getText().toString());
        bundle.putString(NAME,nameField.getText().toString());
        if (authType == IntroActivity.PW)
            bundle.putString(PW, pwField.getText().toString());
        fragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction tact = getSupportFragmentManager().beginTransaction();
        tact.add(R.id.phone_auth_fragment_container, fragment).commit();
        enableEditTextsFocus(false);
        isFragmentShown = true;
    }

    private void setPhoneAuthButtonEnable(){
        enablePhoneAuthRequest = isNameDone && isPhoneDone && isPWDone && isCheckDone;
        Log.d("NewJoinActivity/setPhoneAuthButtonEnable","Enable Button " + enablePhoneAuthRequest);
        phoneAuthRequest.setEnabled(enablePhoneAuthRequest);
        if (enablePhoneAuthRequest){
            phoneAuthRequest.setBackgroundResource(R.drawable.enabled_round_button);
            phoneAuthRequest.setTextColor(getResources().getColor(R.color.button_enabled_text));
        }
        else{
            phoneAuthRequest.setBackgroundResource(R.drawable.disabled_round_button);
            phoneAuthRequest.setTextColor(getResources().getColor(R.color.button_disabled_text));
        }
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

    public void configSmsListener(EditText text){
        if (text ==null)
            return ;
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(10000);
        smsListener = new SmsListener(text);
        registerReceiver(smsListener, filter);
        Log.i("NewJoinActivity/configSmsListener", "SMS Listener Registered");
    }

    private void sendVeriMessage(String phone){
        YammAPIService service = YammAPIAdapter.getService();
        final Dialog dialog = createFullScreenDialog(NewJoinActivity.this, getString(R.string.progress_dialog_message));
        dialog.show();
        service.phoneVerification(phone, new Callback<YammAPIService.VeriExp>() {
            @Override
            public void success(YammAPIService.VeriExp s, Response response) {
                Log.i("NewJoinActivity/getVeriDialogPositiveListener", "VeriExpires at " + s);
                makeYammToast(R.string.verification_sent, Toast.LENGTH_SHORT);
                dialog.dismiss();
                createPhoneAuthFragment();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("NewJoinActivity/getVeriDialogPositiveListener", "Veri Failed ");
                retrofitError.printStackTrace();
                dialog.dismiss();
                makeYammToast(R.string.verification_error_message, Toast.LENGTH_SHORT);
            }
        });
    }
}
