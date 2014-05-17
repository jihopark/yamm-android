package com.teamyamm.yamm.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by parkjiho on 5/16/14.
 */
public class YammDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    ArrayList<CharSequence> list;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    public void onDateSet(DatePicker picker, int year, int month, int day){
        Toast.makeText(getActivity(), "Date Picked " + dateToString(year, month, day) ,Toast.LENGTH_SHORT).show();
        Spinner s = (Spinner) getActivity().findViewById(R.id.yamm_date_spinner);
        ArrayAdapter<CharSequence> adapter = ((MainFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment)).spinnerAdapter;

        //Add item
        list = new ArrayList<CharSequence>();
        for(int i=0 ; i< adapter.getCount() ; i++) list.add(adapter.getItem(i));
        list.add(dateToString(year, month, day));

        //Reset Adapter to spinner
        adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
        s.setAdapter(adapter);
        s.setSelection(list.size()-1);
    }

    public String dateToString(int year, int month, int day){
        //Android month starts from 0
        return year + "년 " + (month + 1) + "월 " + day + "일";
    }
}
