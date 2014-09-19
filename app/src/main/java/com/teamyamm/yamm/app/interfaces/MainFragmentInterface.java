package com.teamyamm.yamm.app.interfaces;

import android.app.Dialog;

import com.teamyamm.yamm.app.pojos.DishItem;

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

    public boolean isLoading();

    public boolean shouldTutorialOpen();

    public void showTutorial();
}
