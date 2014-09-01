package com.teamyamm.yamm.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

/**
 * Created by parkjiho on 8/30/14.
 */
public class PokeAlertActivity extends Activity {

    private PushContent content = null;
    private YammImageView image;
    private TextView title, dish;
    private Button confirm, positive, negative;

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

        image.loadImage(content.getDish().getId());
        title.setText(content.getSender().getName() + "님이 같이 먹재요~");
        dish.setText(content.getDish().getName());

        setButton();
    }

    private void setButton(){
        confirm = (Button) findViewById(R.id.poke_alert_confirm);
        positive = (Button) findViewById(R.id.poke_alert_positive);
        negative = (Button) findViewById(R.id.poke_alert_negative);
        confirm = (Button) findViewById(R.id.poke_alert_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
