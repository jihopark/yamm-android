package com.teamyamm.yamm.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 9/29/14.
 */
public class PhoneAuthActivity extends BaseActivity {
    private EditText nameField, phoneField, veriField;
    private SmsListener smsListener;
    private ImageView successImage;
    private Button submit;
    private int authType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivity();
        setContentView(R.layout.activity_phone_auth);
        setActionBarBackButton(true);

        nameField = (EditText) findViewById(R.id.name_field);
        phoneField = (EditText) findViewById(R.id.phone_text);
        veriField = (EditText) findViewById(R.id.verification_field);
        submit = (Button) findViewById(R.id.verification_submit);
        successImage = (ImageView) findViewById(R.id.veri_success_image);

        phoneField.setText(getPhoneNumber());

        configNameEditText();
        configPhoneEditText();
        configSendButton();

        MixpanelController.trackEnteredPhoneAuthMixpanel();
    }

    private void initActivity(){
        if (getIntent().getExtras()!=null){
            authType = getIntent().getExtras().getInt(IntroActivity.AUTH_TYPE);
            Log.i("PhoneAuthActivity/initActivity", "Auth Type is " + authType);
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
        createDialog(PhoneAuthActivity.this, 0,
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
                        Log.d("PhoneAuthActivity/onSessionClosed","Kakao Session Closed");
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
        unregisterReceiver(smsListener);
    }

    private void configNameEditText(){
        if (authType == IntroActivity.FB){
            if (Session.getActiveSession()!=null) {
                Log.d("PhoneAuthActivity/configNameEditText", "Fetching Name From FB");
                Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, com.facebook.Response response) {
                        if (response != null) {
                            try {
                                String name = user.getName();
                                // If you asked for email permission
                                Log.d("PhoneAuthActivity/configNameEditText", "Name Fetched from FB: " + name);
                                if (nameField!=null)
                                    nameField.setText(name);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("PhoneAuthActivity/configNameEditText", "Exception in loading FB Name");
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
                    Log.d("PhoneAuthActivity/configNameEditField", "Name Fetched from Kakao: " + name);
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
                    Log.e("PhoneAuthActivity/configNameEditField","Failed to Retrieve Kakao Name");
                }
            });
        }
    }

    private void configPhoneEditText(){
        phoneField.setText(getPhoneNumber());
    }

    private void configSmsListener(){
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(10000);
        smsListener = new SmsListener(veriField, successImage);
        registerReceiver(smsListener, filter);
        Log.i("PhoneAuthActivity/configSmsListener", "SMS Listener Registered");
    }

    private void configSendButton() {
        final Button sendVButton = (Button) findViewById(R.id.verification_button);
        final Button resendVButton = (Button) findViewById(R.id.verification_resend_button);
        Log.d("PhoneAuthActivity/configSendButton","Config");
        sendVButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PhoneAuthActivity/onClick","Clicked");
                hideSoftKeyboard(PhoneAuthActivity.this);

                String phone = phoneField.getText().toString();
                if (phone!=null && (phone.length() == 10 || phone.length() == 11)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PhoneAuthActivity.this);

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
                        Log.i("PhoneAuthActivity/getVeriDialogPositiveListener", "Verification API Called for " + phone);
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
                        hideSoftKeyboard(PhoneAuthActivity.this);
                        String phone = phoneField.getText().toString();
                        sendVeriMessage(phone);
                        dismissCurrentDialog();
                    }
                };
                createDialog(PhoneAuthActivity.this, R.string.verification_resend_dialog_title, R.string.verification_resend_dialog_message,
                        R.string.dialog_positive, R.string.dialog_negative, positiveListener, null).show();
            }

        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = createFullScreenDialog(PhoneAuthActivity.this, getString(R.string.progress_dialog_message));
                dialog.show();
                YammAPIAdapter.getTokenService().registerPhone(phoneField.getText().toString(), veriField.getText().toString(), new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        dialog.dismiss();
                        makeYammToast(R.string.join_progress_dialog_title, Toast.LENGTH_SHORT);
                        MixpanelController.trackSubmitPhoneAuthMixpanel();
                        goToActivity(GridActivity.class);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void sendVeriMessage(String phone){
        YammAPIService service = YammAPIAdapter.getService();

        service.phoneVerification(phone, new Callback<YammAPIService.VeriExp>() {
            @Override
            public void success(YammAPIService.VeriExp s, Response response) {
                Log.i("PhoneAuthActivity/getVeriDialogPositiveListener", "VeriExpires at " + s);
                makeYammToast(R.string.verification_sent, Toast.LENGTH_SHORT);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("PhoneAuthActivity/getVeriDialogPositiveListener", "Veri Failed ");
                retrofitError.printStackTrace();
                makeYammToast(R.string.verification_error_message, Toast.LENGTH_SHORT);
            }
        });
    }
}
