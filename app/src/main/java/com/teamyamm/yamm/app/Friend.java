package com.teamyamm.yamm.app;

/**
 * Created by parkjiho on 5/19/14.
 */
public class Friend extends YammItem {
    private String phone;

    public Friend(String id, String name){
        super(id,name);
    }

    public Friend(String id, String name, String phone){
        super(id,name);
        this.phone = phone;
    }

    public String getProfileImageURL(){
        return "";
    }

    public int compareTo(Friend i){
        return super.compareTo(i);
    }

    public String toString(){
        return name + ":" + phone + ":" + id;
    }

}
