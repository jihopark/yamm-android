package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by parkjiho on 8/30/14.
 */
public class PushContent {
    public final static String POKE = "POKE";

    private String type;
    private DishItem dish;
    private String date;
    private String meal;
    private Friend sender;

    public PushContent(Bundle extras){
        Gson gson = new Gson();
        type = extras.getString("type");
        if (type.equals(POKE)){
            date = extras.getString("date");
            meal = extras.getString("meal");
            dish = gson.fromJson(extras.getString("dish"), DishItem.class);
            sender = gson.fromJson(extras.getString("sender"), Friend.class);
            Log.i("PushContent/constructor", toString());
        }
    }

    public String getType(){ return type; }
    public String getDate(){ return date; }
    public String getMeal(){ return meal; }
    public String getTime(){ return date + " " + meal; }
    public Friend getSender(){ return sender; }
    public DishItem getDish(){ return dish; }

    public String toString(){
        return "Type:" + type + " Date/Meal:" + date + "/" + meal + " Dish:" + dish + " Sender:" + sender;
    }
}
