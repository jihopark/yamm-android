package com.teamyamm.yamm.app.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamyamm.yamm.app.BaseActivity;
import com.teamyamm.yamm.app.R;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.pojos.DishItem;
import com.teamyamm.yamm.app.util.LocationSearchHelper;

/**
 * Created by parkjiho on 10/16/14.
 */
public class SearchDishItemView extends RelativeLayout {

    private Context context;
    private DishItem item;
    private TextView tv;

    public SearchDishItemView(Context context, DishItem item){
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SearchDishItemView layout = (SearchDishItemView) inflater.inflate(R.layout.place_pick_item, this, true);
        tv = (TextView) layout.findViewById(R.id.pick_field);
        setItem(item);
       // setClick();
    }

    public void setItem(DishItem item){
        this.item = item;
        tv.setText(item.getName());
    }

    private void setClick(){
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.hideSoftKeyboard((Activity) context);
                MixpanelController.trackSearchDishMixpanel(item);
                LocationSearchHelper.startMapActivity(context, item);
            }
        });
    }
}
