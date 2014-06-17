package com.teamyamm.yamm.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 5/12/14.
 */
public class BattleItem {
    public static final int NO_RESPONSE = 2;
    public static final int NONE = 0;
    public static final int FIRST = 1;
    public static final int SECOND = -1;

    private List<DishItem> list;
    private int result = BattleItem.NO_RESPONSE;

    public BattleItem(List<DishItem> l){
        list = l;
        result = NO_RESPONSE;
    }

    public BattleItem(DishItem a, DishItem b){
        List<DishItem> l = new ArrayList<DishItem>();
        l.add(a); l.add(b);
        list = l;
        result = NO_RESPONSE;
    }

    public BattleItem(DishItem a, DishItem b, int c){
        this(a,b);
        result = c;
    }

    public void setResult(int r){
        result = r;
    }

    public int getResult(){
        return result;
    }

    public List<DishItem> getList(){
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

    //BattleItem.toString = (firstdishitem.id),(seconddishitem.id),(result)
    public String toString(){
        return getFirst().getId() + "." + getSecond().getId() + "." + getResult();
    }

}
