package com.teamyamm.yamm.app.pojos;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by parkjiho on 8/30/14.
 */
public class PushContent {
    public final static String ADMIN = "ADMIN";
    public final static String POKE = "POKE";
    public final static String FACEBOOK ="FB";
    public final static String POKE_RESPONSE = "POKE_RESPONSE";

    private String type;
    private Friend sender;

    /*
    for POKE
    * */
    private DishItem dish;
    private String date;
    private String meal;
    /*
    for ADMIN
    * */
    private String title;
    private String message;

    /*
    * for POKE Response
    * */
    private boolean response;

    public PushContent(Bundle extras){
        Gson gson = new Gson();
        type = extras.getString("type");
        if (type.equals(POKE)){
            date = extras.getString("date");
            meal = extras.getString("meal");
            dish = gson.fromJson(extras.getString("dish"), DishItem.class);
            sender = gson.fromJson(extras.getString("sender"), Friend.class);
        }
        else if (type.equals(ADMIN)){
            message = extras.getString("mp_message");
            if (extras.containsKey("title"))
                title = extras.getString("title");
            else
                title = "";
        }
        else if (type.equals(POKE_RESPONSE)){
            String r = extras.getString("response");
            if (!r.isEmpty())
                response = r.equals("true");
            else{
                Log.e("PushContent/constructor", "Poke Response should contain response");
                return ;
            }
            sender = gson.fromJson(extras.getString("sender"), Friend.class);
            dish = gson.fromJson(extras.getString("dish"), DishItem.class);
        }
    }

    public Boolean getResponse(){return response; }
    public String getType(){ return type; }
    public String getDate(){ return date; }
    public String getMeal(){ return meal; }
    public String getTime(){ return date + " " + meal; }
    public Friend getSender(){ return sender; }
    public DishItem getDish(){ return dish; }
    public String getTitle(){return title; }
    public String getMessage(){ return message; }

    public String toString(){
        return "Type:" + type + " Date/Meal:" + date + "/" + meal + " Dish:" + dish + " Sender:" + sender;
    }
}
