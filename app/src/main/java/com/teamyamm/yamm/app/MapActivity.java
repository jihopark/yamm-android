package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.teamyamm.yamm.app.util.LocationSearchHelper;

/**
 * Created by parkjiho on 10/2/14.
 */
public class MapActivity extends BaseActivity {
    private GoogleMap map;
    private double x,y;
    private String dishName;
    private int dishId;
    private LatLng myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initActivity();
        TextView title = (TextView) findViewById(R.id.map_title);
        title.setText(dishName);
    }

    @Override
    protected void onResume(){
        super.onResume();

        initMap();
    }

    private void initActivity(){
        dishId = getIntent().getExtras().getInt(LocationSearchHelper.DISH_ID);
        dishName = getIntent().getExtras().getString(LocationSearchHelper.DISH_NAME);
        x = getIntent().getExtras().getDouble(LocationSearchHelper.LANG);
        y= getIntent().getExtras().getDouble(LocationSearchHelper.LONG);

        if (dishId == 0 || dishName == null){
            Log.e("MapActivity/initActivity", "Dish isn't Set.");
            finish();
        }
        Log.i("MapActivity/initActivity", "Dish: " + dishName + " x,y:" + x + "," + y);
    }

    private void initMap(){
        if (map==null){
            map =  ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
            if (map!=null){
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                if (x!=0 && y!=0){
                    myLocation = new LatLng(x,y);
                }
                map.animateCamera(CameraUpdateFactory.newLatLng(myLocation));

            }

        }
    }
}
