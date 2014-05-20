package com.teamyamm.yamm.app;

import android.util.Log;

/**
 * Created by parkjiho on 5/19/14.
 */
public abstract class YammItem implements Comparable<YammItem> {
    protected int id;
    protected String name;


    public YammItem(int id, String name){
        this.id = id;
        this.name = name;
    }
    public int getID(){ return id; }
    public String getName(){ return name; }
    public abstract String getProfileImageURL();

    /*
    * Put Team Objects first and then Friend
    * */

    public int compareTo(YammItem compare){
        if (this instanceof Team && compare instanceof Team) {
            Log.i("compareTo",this.getName()+" "+compare.getName()+" same team!");
            return this.name.compareTo(compare.getName());
        }
        else if (this instanceof Team && compare instanceof Friend) {
            Log.i("compareTo",this.getName()+" "+compare.getName()+" firstone is team");
            return -1;
        }
        else if (this instanceof Friend && (compare instanceof Team)) {
            Log.i("compareTo",this.getName()+" "+compare.getName()+" secondone is team!");
            return 1;
        }
        else {
            Log.i("compareTo",this.getName()+" "+compare.getName()+" both not team");
            return this.name.compareTo(compare.getName());
        }
    }
}
