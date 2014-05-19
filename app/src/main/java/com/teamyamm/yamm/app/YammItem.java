package com.teamyamm.yamm.app;

/**
 * Created by parkjiho on 5/19/14.
 */
public abstract class YammItem {
    protected int id;
    protected String name;


    public YammItem(int id, String name){
        this.id = id;
        this.name = name;
    }
    public int getID(){ return id; }
    public String getName(){ return name; }
    public abstract String getProfileImageURL();
}
