package com.teamyamm.yamm.app.pojos;

import android.util.Log;

/**
 * Created by parkjiho on 10/7/14.
 */
public class YammPlace implements Comparable<YammPlace> {
    public int id;
    public String name;
    public String address;
    public double distance;
    public double lat, lng;

    public YammPlace(int id, String name, String address, double distance, double lat, double lng){
        this.id = id;
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
    }

    public int compareTo(YammPlace compare){
        return Double.compare(distance, compare.distance);
    }

    public String getDistanceString(){
        int meters =  (int) (distance*1000);
        meters -= meters%100;

        Log.i("YammPlace/getDistanceString", meters+"");

        if (meters >= 1000)
            return "1km이상";
        else if (meters == 900)
            return "1km이내";
        else
            return meters+"m이내";
    }
}
