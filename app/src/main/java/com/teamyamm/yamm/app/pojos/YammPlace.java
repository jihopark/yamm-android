package com.teamyamm.yamm.app.pojos;

import android.util.Log;

import java.util.List;

/**
 * Created by parkjiho on 10/7/14.
 */
public class YammPlace implements Comparable<YammPlace> {
    public int id;
    public String name;
    public String address;
    public double distance;
    public double lat, lng;
    public String phone;
    public String type;
    public List<DishItem> dishes;

    public YammPlace(int id, String name, String address, double distance, double lat, double lng,
                     String phone, String type, List<DishItem> dishes){
        this.id = id;
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
        this.phone = phone;
        this.type = type;
        this.dishes = dishes;
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
        else if (meters==0){
            return "바로 앞";
        }
        else
            return meters+"m이내";
    }

    public String getDishesString(){
        String s = "";
        for (DishItem dish : dishes) {
            s += dish.getName() + " ";
        }
        s+=" 등";
        return s;
    }
    public String getShortenedAddress(){
        String[] array = address.split(" ");
        String result = "";
        for (int i =1; i < array.length ; i++) {
            result += array[i];
            result +=" ";
        }
        if (result.length() < 25)
            return result;
        else
            return result.substring(0,24)+"...";
    }
}
