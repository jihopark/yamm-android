package com.teamyamm.yamm.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 5/19/14.
 */
public class Team extends YammItem{
    List<Friend> teamList;

    public Team(long id, String name){
        super(id,name);
        teamList = new ArrayList<Friend>();
    }

    public Team(long id, String name, List<Friend> list){
        super(id,name);
        teamList = list;
    }
    public void setName(String s){
        name = s;
    }

    public String getProfileImageURL(){
        return "";
    }

    public int compareTo(Team i){
        return super.compareTo(i);
    }

}
