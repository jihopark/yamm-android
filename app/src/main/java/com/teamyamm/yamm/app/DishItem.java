package com.teamyamm.yamm.app;

/**
 * Created by parkjiho on 5/12/14.
 */
public class DishItem {
    private int id;
    private String name;
    private String comment;

    public DishItem(int id, String name){
        this.id = id;
        this.name = name;
    }

    public DishItem(int id, String name, String comment){
        this.id = id;
        this.name = name;
        this.comment = comment;
    }

    public String getComment(){ return comment; }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String toString(){
        return id+":"+name;
    }

    public boolean equals(DishItem i){
        return i.getId() == id;
    }
}
