package com.teamyamm.yamm.app.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.teamyamm.yamm.app.BaseActivity;
import com.teamyamm.yamm.app.R;

/**
 * Created by parkjiho on 6/19/14.
 */
public class SmsListener extends BroadcastReceiver {
    private EditText editText;
    private ImageView confirmImage;
    public SmsListener(EditText editText){
        super();
        this.editText = editText;
    }

    public SmsListener(EditText editText, ImageView confirmImage){
        super();
        this.editText = editText;
        this.confirmImage = confirmImage;
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
                            setVeriCodeText(msgBody.substring(11, 15));
                            BaseActivity activity = (BaseActivity) context;
                            activity.makeYammToast(activity.getString(R.string.verification_done_message),Toast.LENGTH_SHORT);
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
      //  confirmImage.setVisibility(View.VISIBLE);
    }
}