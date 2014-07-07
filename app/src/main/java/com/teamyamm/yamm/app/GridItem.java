package com.teamyamm.yamm.app;

/**
 * Created by parkjiho on 5/10/14.
 */
public class GridItem {
    private int id;
    private String name = "default";

    public GridItem(){
        id = -1;
    }

    public GridItem(GridItem a){
        name = a.getName();
        id = a.getId();
    }

    public GridItem(int ID){
        id = ID;
    }

    public GridItem(int ID, String n){
        id = ID;
        name = n;
    }

    @Override
    public boolean equals(Object i){
        if (!(i instanceof GridItem))
            return false;
        if (this.getId() == ((GridItem)i).getId())
            return true;
        return false;
    }

    public String getName(){
        return name;
    }

    public void setName(String n){
        name = n;
    }

    public int getId(){
        return id;
    }

    public String toString(){
        return getId() + ":" + name;
    }

    public String getImageURL(){
        return "";
    }


}