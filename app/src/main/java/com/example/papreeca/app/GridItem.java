package com.example.papreeca.app;

/**
 * Created by parkjiho on 5/10/14.
 */
public class GridItem {
    private int id;
    private String name = "default";

    public GridItem(){
        id = -1;
    }

    public GridItem(int ID){
        id = ID;
    }

    public GridItem(int ID, String n){
        id = ID;
        name = n;
    }

    public String getName(){
        return name;
    }

    public void setName(String n){
        name = n;
    }

    public long getId(){
        return id;
    }

    public String toString(){
        return String.valueOf(getId())+ ":" + name;
    }

    public String getImageURL(){
        return "";
    }
}