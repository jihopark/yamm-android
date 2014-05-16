package com.teamyamm.yamm.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by parkjiho on 5/16/14.
 */
public class YammDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    public void onDateSet(DatePicker picker, int year, int month, int day){

    }
}
