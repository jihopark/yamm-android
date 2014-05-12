package com.teamyamm.yamm.app;

import java.util.ArrayList;

/**
 * Created by parkjiho on 5/12/14.
 */
public class BattleItem {
    public static final int NO_RESPONSE = 0;
    public static final int NONE = -1;

    private ArrayList<DishItem> list;
    private String battleName = "defaultBattle";
    private int result = BattleItem.NO_RESPONSE;

    public BattleItem(ArrayList<DishItem> l){
        list = l;
    }

    public BattleItem(ArrayList<DishItem> l, String s){
        this(l);
        battleName = s;
    }

    public BattleItem(DishItem a, DishItem b){
        ArrayList<DishItem> l = new ArrayList<DishItem>();
        l.add(a); l.add(b);
        list = l;
    }

    public BattleItem(DishItem a, DishItem b, String s){
        this(a,b);
        battleName = s;
    }

    public void setResult(int r){
        result = r;
    }

    public int getResult(){
        return result;
    }

    public ArrayList<DishItem> getList(){
        return list;
    }

    public DishItem getFirst(){
        return list.get(0);
    }
    public DishItem getSecond(){
        return list.get(1);
    }

    public int getCount(){
        return list.size();
    }

    public String toString(){
        return getFirst() +" vs. " + getSecond();
    }

}
