package com.teamyamm.yamm.app;

import android.app.Dialog;

import java.util.List;

/**
 * Created by parkjiho on 8/3/14.
 * Certain activity implements MainFragmentInterface to use MainFragment
 */
public interface MainFragmentInterface {

    public void changeInDishItem(List<DishItem> list);
    public Dialog getFullScreenDialog();
    public boolean isFullScreenDialogOpen();
    public void setFullScreenDialogOpen(boolean b);
    public void closeFullScreenDialog();
}
