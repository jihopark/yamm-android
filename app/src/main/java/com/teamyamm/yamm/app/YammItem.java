package com.teamyamm.yamm.app;

/**
 * Created by parkjiho on 5/19/14.
 */
public abstract class YammItem implements Comparable<YammItem> {
    protected int id;
    protected String name;
    private boolean selected = false;
    public YammItem(int id){
        this.id = id;
    }

    public YammItem(int id, String name){
        this.id = id;
        this.name = name;
    }
    public int getID(){ return id; }
    public String getName(){ return name; }
    public abstract String getProfileImageURL();
    public boolean isDummy(){ return id==-1; }
    /*
    * Put Team Objects first and then Friend
    * */

    public int compareTo(YammItem compare){
        if (this instanceof Team && compare instanceof Team) {
            return this.name.compareTo(compare.getName());
        }
        else if (this instanceof Team && compare instanceof Friend) {
            return -1;
        }
        else if (this instanceof Friend && (compare instanceof Team)) {
            return 1;
        }
        else {
            return this.name.compareTo(compare.getName());
        }
    }

    public void setSelected(boolean b){
        selected = b;
    }

    public void toggle(){
        setSelected(!selected);
    }

    public boolean getSelected(){
        return selected;
    }

    public String toString(){
        return getID()+ ":" + getName();
    }
}
