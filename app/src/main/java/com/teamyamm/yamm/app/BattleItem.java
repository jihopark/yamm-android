package com.teamyamm.yamm.app;

/**
 * Created by parkjiho on 5/12/14.
 */
public class BattleItem {
    public static final int NO_RESPONSE = 0;
    public static final int NONE = -1;
    public static final int FIRST = 1;
    public static final int SECOND = 2;

    private DishItem first, second;
    private int result = BattleItem.NO_RESPONSE;

    public BattleItem(DishItem a, DishItem b){
        first = a;
        second = b;
        result = NO_RESPONSE;
    }

    public BattleItem(DishItem a, DishItem b, int c){
        this(a,b);
        result = c;
    }

    public void setResult(int r){
        if (r == FIRST)
            result = getFirst().getId();
        else if (r== SECOND)
            result = getSecond().getId();
        else
            result = NO_RESPONSE;
    }

    public int getResult(){
        return result;
    }

    public DishItem getFirst(){
        return first;
    }
    public DishItem getSecond(){
        return second;
    }

    public String toString(){
        return getFirst().getId() + "," + getSecond().getId() + "," + getResult() + ";";
    }

}
