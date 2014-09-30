package com.teamyamm.yamm.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText phoneField, veriField;
    private SmsListener smsListener;
    private ImageView successImage;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phone_auth);

        phoneField = (EditText) findViewById(R.id.phone_text);
        veriField = (EditText) findViewById(R.id.verification_field);
        submit = (Button) findViewById(R.id.verification_submit);
        successImage = (ImageView) findViewById(R.id.veri_success_image);

        phoneField.setText(getPhoneNumber());

        configSendButton();

        MixpanelController.trackEnteredPhoneAuthMixpanel();
    }

    @Override
    public void onBackPressed() {
        goBackHome();
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
