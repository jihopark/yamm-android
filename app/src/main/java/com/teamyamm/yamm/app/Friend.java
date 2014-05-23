package com.teamyamm.yamm.app;

/**
 * Created by parkjiho on 5/19/14.
 */
public class Friend extends YammItem {

    public Friend(int id){
        super(id);
        this.name = "Yamm친이 없으시네요";
    }

    public Friend(int id, String name){
        super(id,name);
    }

    public String getProfileImageURL(){
        return "";
    }

    public int compareTo(Friend i){
        return super.compareTo(i);
    }

}
