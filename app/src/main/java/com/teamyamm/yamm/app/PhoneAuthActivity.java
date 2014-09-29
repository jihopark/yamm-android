package com.teamyamm.yamm.app;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.teamyamm.yamm.app.util.SmsListener;

/**
 * Created by parkjiho on 9/29/14.
 */
public class PhoneAuthActivity extends BaseActivity {
    private EditText phone;
    private Button veri;
    private SmsListener smsListener;
    private ImageView successImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phone_auth);

        phone = (EditText) findViewById(R.id.phone_text);
        veri = (Button) findViewById(R.id.veri_button);
        successImage = (ImageView) findViewById(R.id.veri_success_image);

        String p = getPhoneNumber();
        phone.setText(p);
        Log.i("PhoneAuthActivity/onCreate","Set Phone Number" + p);

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
        smsListener = new SmsListener(phone, successImage);
        registerReceiver(smsListener, filter);
        Log.i("PhoneAuthActivity/configSmsListener", "SMS Listener Registered");
    }
}
