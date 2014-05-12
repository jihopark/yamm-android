package com.teamyamm.yamm.app;

/**
 * Created by parkjiho on 5/12/14.
 */
public class DishItem {
    private int id;
    private String name;

    public DishItem(int i, String n){
        id = i;
        name = n;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String toString(){
        return "Dish : " + "id -" + id + ",name - " + name;
    }
}
