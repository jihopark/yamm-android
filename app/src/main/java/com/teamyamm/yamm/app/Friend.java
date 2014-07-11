package com.teamyamm.yamm.app;

/**
 * Created by parkjiho on 5/19/14.
 */
public class Friend extends YammItem {
    private String phone;
    private String contactName;

    public Friend(long id, String name){
        super(id,name);
        contactName = "";
    }

    public Friend(long id, String name, String phone){
        super(id,name);
        this.phone = phone;
        contactName = "";
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

    public void setContactName(String s){ contactName = s; }

    public String getPhone(){ return phone; }

    public String getName(){
        if (contactName == null || contactName == "")
            return name;
        return contactName;
    }

}
