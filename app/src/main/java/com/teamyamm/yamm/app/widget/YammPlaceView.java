package com.teamyamm.yamm.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamyamm.yamm.app.R;
import com.teamyamm.yamm.app.pojos.YammPlace;

/**
 * Created by parkjiho on 10/7/14.
 */
public class YammPlaceView extends RelativeLayout {

    private RelativeLayout layout;
    private TextView nameText, addressText, distanceText;

    public YammPlaceView(Context context) {
        super(context);
    }

    public YammPlaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YammPlaceView(Context context, YammPlace item) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (YammPlaceView) inflater.inflate(R.layout.yamm_place_view, this, true);
        nameText = (TextView) layout.findViewById(R.id.name_text);
        addressText = (TextView) layout.findViewById(R.id.address_text);
        distanceText = (TextView) layout.findViewById(R.id.distance_text);
        setItem(item);
    }

    public void setItem(YammPlace item){
        nameText.setText(item.name);
        addressText.setText(item.address);
        distanceText.setText(item.getDistanceString());
    }
}
