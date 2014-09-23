package com.teamyamm.yamm.app.interfaces;

import android.widget.ArrayAdapter;

import com.teamyamm.yamm.app.widget.YammDatePickerFragment;

/**
 * Created by parkjiho on 8/11/14.
 */
public interface DatePickerFragmentInterface {
    public ArrayAdapter<CharSequence> getSpinnerAdapter();
    public YammDatePickerFragment getDatePickerFragment();
    public void setDatePickerFragment(YammDatePickerFragment fragment);
}
