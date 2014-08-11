package com.teamyamm.yamm.app;

import android.widget.ArrayAdapter;

/**
 * Created by parkjiho on 8/11/14.
 */
public interface DatePickerFragmentInterface {
    public ArrayAdapter<CharSequence> getSpinnerAdapter();
    public YammDatePickerFragment getDatePickerFragment();
    public void setDatePickerFragment(YammDatePickerFragment fragment);
}
