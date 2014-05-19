package com.teamyamm.yamm.app;

/**
 * Created by parkjiho on 5/19/14.
 */
public class Friend {
    private int id;
    private String name;

    public Friend(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getID(){ return id; }
    public String getName(){ return name; }
    public String getProfileImageURL(){ return ""; }

}
