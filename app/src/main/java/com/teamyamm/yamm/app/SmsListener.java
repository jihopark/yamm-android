package com.teamyamm.yamm.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by parkjiho on 6/19/14.
 */
public class SmsListener extends BroadcastReceiver {
    private EditText editText;

    public SmsListener(EditText editText){
        super();
        this.editText = editText;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("SmsListener/onReceive","onReceived called");


        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        Log.i("SmsListener/onReceive","Message Received " + msgBody + " from " + msg_from);

                        if (msgBody.substring(0,4).equals("YAMM")){
                            setVeriCodeText(msgBody.substring(msgBody.length() - 4, msgBody.length()));
                            BaseActivity activity = (BaseActivity) context;
                            Toast.makeText(context, activity.getString(R.string.verification_done_message),Toast.LENGTH_SHORT).show();
                        }
                    }

                }catch(Exception e){
                    Log.e("Exception caught", e.getMessage());
                }
            }
        }
    }

    private void setVeriCodeText(String code){
        Log.i("SmsListener/setVeriCodeText","EditText Set to " + code);
        editText.setText(code);
    }
}