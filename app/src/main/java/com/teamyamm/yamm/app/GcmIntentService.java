package com.teamyamm.yamm.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by parkjiho on 8/13/14.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public static final String facebookFirstTime = "FB";
    public static List<Friend> friendList = null;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty())
            Log.i("GcmIntentService/onHandleIntent","Intent Received " + extras.toString());

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
          //      sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(new PushContent(extras));
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(PushContent content) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = null;
        NotificationCompat.Builder mBuilder = null;


        if (content.getType().equals(PushContent.POKE)) {
            Intent intent = new Intent(this, PokeAlertActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("pushcontent", new Gson().toJson(content, PushContent.class));

            contentIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);


            if (BaseActivity.checkIfAppIsRunning()){
                makeNotificationSound();
                startActivity(intent);
                return ;
            }


            String msg = PokeAlertActivity.findFriendName(content.getSender(), getFriendList()) + "님이 " + content.getTime() + "에 이거 먹재요~";
            String title = getString(R.string.poke_push_title);
            mBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.yamm_stat_notify)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(msg))
                            .setContentText(msg)
                            .setAutoCancel(true)
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        }
        else if (content.getType().equals(PushContent.FACEBOOK)){
            SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
            boolean fb = prefs.getBoolean(facebookFirstTime, true);


            if (fb){
                contentIntent = PendingIntent.getActivity(this, 0,
                        goToYammFacebook(), PendingIntent.FLAG_UPDATE_CURRENT);

                String msg = getString(R.string.push_facebook_message);
                String title = getString((R.string.push_facebook_title));
                mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.yamm_stat_notify)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(facebookFirstTime, false);
                editor.commit();
                Log.i("GcmIntentService/sendNotification","FB Notification Done");
            }
            else
                Log.i("GcmIntentService/sendNotification","FB Notification came but ignored");
        }
        else if (content.getType().equals(PushContent.ADMIN)){
            Log.i("GcmIntentService/sendNotification","ADMIN Notification");

            Intent intent = new Intent(this, MainActivity.class);

            contentIntent = PendingIntent.getActivity(this, 0,
                    intent, 0);


            String title = content.getTitle();
            if (title.equals(""))
                title = getString(R.string.push_admin_default_title);

            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.yamm_stat_notify)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(content.getMessage()))
                    .setContentText(content.getMessage())
                    .setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        }


        if (contentIntent!= null && mBuilder!=null) {
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    private void makeNotificationSound(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Intent goToYammFacebook(){
        Intent intent;

        try {
            getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            Log.i("tried", "facebook");
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://page/251075981744124")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/yammapp")); //catches and opens a url to the desired page
        }
        return intent;
    }

    private List<Friend> getFriendList(){
        if (friendList == null){
            Gson gson = new Gson();
            Type typeOfDest = new TypeToken<List<Friend>>() {
            }.getType();

            SharedPreferences prefs =  getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
            String s = prefs.getString(getString(R.string.FRIEND_LIST),"");
            if (s.isEmpty())
                return null;
            return gson.fromJson(s, typeOfDest);
        }
        return friendList;
    }
}