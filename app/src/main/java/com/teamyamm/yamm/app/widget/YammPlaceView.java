package com.teamyamm.yamm.app.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamyamm.yamm.app.PlaceActivity;
import com.teamyamm.yamm.app.R;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.pojos.YammPlace;

/**
 * Created by parkjiho on 10/7/14.
 */
public class YammPlaceView extends RelativeLayout {

    private RelativeLayout layout;
    private TextView nameText, addressText, distanceText;
    private ImageButton placeDetailButton;
    private Context context;

    public YammPlaceView(Context context) {
        super(context);
    }

    public YammPlaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YammPlaceView(Context context, YammPlace item) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (YammPlaceView) inflater.inflate(R.layout.yamm_place_view, this, true);
        nameText = (TextView) layout.findViewById(R.id.name_text);
        addressText = (TextView) layout.findViewById(R.id.address_text);
        distanceText = (TextView) layout.findViewById(R.id.distance_text);
        placeDetailButton = (ImageButton) layout.findViewById(R.id.place_detail_button);

        setItem(item);
    }

    public void setItem(YammPlace item){
        final YammPlace i = item;
        nameText.setText(item.name);
        addressText.setText(item.getShortenedAddress());
        distanceText.setText(item.getDistanceString());

        View.OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlaceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Gson gson = new Gson();
                intent.putExtra(PlaceActivity.YAMM_PLACE, gson.toJson(i, new TypeToken<YammPlace>() {
                }.getType()));

                MixpanelController.trackEnteredPlaceMixpanel();
                context.startActivity(intent);
            }
        };
        placeDetailButton.setOnClickListener(listener);
        this.setOnClickListener(listener);

    }
}
