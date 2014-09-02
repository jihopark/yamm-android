package com.teamyamm.yamm.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

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

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

            SharedPreferences prefs =  getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
            String s = prefs.getString(getString(R.string.FRIEND_LIST),"");
            if (s.isEmpty())
                return null;
            return gson.fromJson(s, typeOfDest);
        }
        return friendList;
    }

}
