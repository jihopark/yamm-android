package com.teamyamm.yamm.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by parkjiho on 5/16/14.
 */
public class YammDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public String result = "";
    private boolean invalid = false;
    private int todayY, todayM, todayD;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        todayY = c.get(Calendar.YEAR);
        todayM = c.get(Calendar.MONTH);
        todayD = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this,todayY, todayM, todayD);
    }

    public void onDateSet(DatePicker picker, int year, int month, int day){
        //Prevent Doublefire
        if (result != "" || invalid) {
            invalid = false;
            return;
        }

        Log.i("onDateSet","onDateSet called");

        //Date Validation
        if (!isDateValid(year, month, day)){
            Log.i("onDateSet","invalid date");
            //Alert invalid date
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final FriendActivity activity = (FriendActivity) getActivity();
            builder.setMessage(R.string.invalid_date_message)
                    .setTitle(R.string.invalid_date_title)
                    .setPositiveButton(R.string.dialog_positive,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.datePickerFragment = new YammDatePickerFragment();
                            activity.datePickerFragment.show(activity.getSupportFragmentManager(), "timePicker");

                        }
                    });
            builder.create().show();
            invalid = true;
            dismiss();
            return ;
        }

        result = dateToString(year, month, day);

        //Show Meal Picker Dialog
        DialogFragment newFragment = new MealPickerDialog();
        newFragment.show(getActivity().getSupportFragmentManager(), "mealPicker");
        dismiss();
    }

    public boolean isDateValid(int y, int m, int d){
        if (todayY > y)
            return false;
        else if (todayY < y)
            return true;

        if (todayM > m)
            return false;
        else if (todayM < m)
            return true;

        if (todayD > d)
            return false;
        return true;
    }


    public String dateToString(int year, int month, int day){
        //Android month starts from 0
        return year + "년 " + (month + 1) + "월 " + day + "일";
    }
}
