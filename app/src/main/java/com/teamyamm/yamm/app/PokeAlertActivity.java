package com.teamyamm.yamm.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 8/30/14.
 */
public class PokeAlertActivity extends Activity {
    private static List<Friend> friendList = null;

    private PushContent content = null;
    private YammImageView image;
    private TextView title, dish;
    private Button positive, negative;
    private ImageButton confirm;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            content = new Gson().fromJson(getIntent().getExtras().getString("pushcontent"), PushContent.class);
        }
        else{
            Log.e("PokeAlertActivity/onCreate","PokeAlertActivity created without bundle");
            finish();
        }
        setContentView(R.layout.activity_poke_alert);

        setDialogContent();

    }

    private void setDialogContent(){
        image = (YammImageView) findViewById(R.id.poke_alert_image);
        title = (TextView) findViewById(R.id.poke_alert_title);
        dish = (TextView) findViewById(R.id.poke_alert_dish);

        image.setID(content.getDish().getId());
        image.setPath(YammImageView.DISH);
        title.setText(findFriendName(content.getSender(), getFriendList()) + "님이 같이 먹재요~");
        dish.setText(content.getDish().getName());

        setButton();
    }

    private void setButton(){
        confirm = (ImageButton) findViewById(R.id.poke_alert_confirm);
        positive = (Button) findViewById(R.id.poke_alert_positive);
        negative = (Button) findViewById(R.id.poke_alert_negative);

        final List<Long> uids = new ArrayList<Long>();
        uids.add(content.getSender().getID());

        final Callback<String> callback = new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.i("PokeAlertActivity/sendPokeResponse", "Push " + s);

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String msg = retrofitError.getCause().getMessage();
                Log.e("PokeAlertActivity/sendPokeMessage", "Error In Push Message");
                makeYammToast(R.string.unidentified_error_message, Toast.LENGTH_SHORT);
            }
        };

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeYammToast(findFriendName(content.getSender(), getFriendList()) + "님한테 좋다고 했어요!", Toast.LENGTH_SHORT);
                YammAPIAdapter.getTokenService().sendPokeResponse(new YammAPIService.RawPokeMessage(uids, true, content.getDish().getId()), callback);
                trackPokeResponseMixpanel(true);
                finish();
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeYammToast(findFriendName(content.getSender(), getFriendList()) + "님한테 딴거 먹자고 했어요!", Toast.LENGTH_SHORT);
                YammAPIAdapter.getTokenService().sendPokeResponse(new YammAPIService.RawPokeMessage(uids, false, content.getDish().getId()), callback);
                trackPokeResponseMixpanel(false);
                finish();
            }
        });
    }

    public static String findFriendName(Friend f, List<Friend> list){
        if (list == null) {
            Log.i("PokeAlertActivity/findFriendName","Cannot load list");
            return f.getName();
        }

        for (Friend i : list){
            if (i.getID() == f.getID()) {
                return i.getName();
            }
        }
        Log.i("PokeAlertActivity/findFriendName","Could not find matching name");
        return f.getName();
    }

    private List<Friend> getFriendList(){
        if (friendList == null){
            Gson gson = new Gson();
            Type typeOfDest = new TypeToken<List<Friend>>() {
            }.getType();

            SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
            String s = prefs.getString(getString(R.string.FRIEND_LIST),"");
            if (s.isEmpty())
                return null;
            return gson.fromJson(s, typeOfDest);
        }
        return friendList;
    }

    protected void makeYammToast(int rId, int duration){
        makeYammToast(getString(rId), duration);
    }

    protected void makeYammToast(String message, int duration){
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.yamm_toast,
                (ViewGroup) findViewById(R.id.toast_layout));

        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        // Set the Text to show in TextView
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());

        toast.setGravity(Gravity.CENTER_VERTICAL, 0 ,0);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    public void trackPokeResponseMixpanel(boolean response){
        JSONObject props = new JSONObject();
        try {
            if (response)
                props.put("Response", "OK");
            else
                props.put("Response", "NO");
        }catch(JSONException e){
            Log.e("PokeAlertActivity/trackPokeResponseMixpanel","JSON Error");
        }
        MixpanelAPI.getInstance(PokeAlertActivity.this, BaseActivity.MIXPANEL_TOKEN)
                .track("Poke Response", props);
        Log.i("PokeAlertActivity/trackPokeResponseMixpanel","Poke Response Tracked " + response);
    }

}
