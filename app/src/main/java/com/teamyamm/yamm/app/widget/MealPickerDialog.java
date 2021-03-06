package com.teamyamm.yamm.app.widget;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.teamyamm.yamm.app.interfaces.DatePickerFragmentInterface;
import com.teamyamm.yamm.app.R;

import java.util.ArrayList;

/**
 * Created by parkjiho on 5/17/14.
 */
public class MealPickerDialog extends DialogFragment{
    String[] listContent = {"점심", "저녁"};
    ListView listView;
    public MealPickerDialog(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_picker, container);
        getDialog().setTitle(getString(R.string.meal_picker_dialog_title));
        listView = (ListView) view.findViewById(R.id.meal_picker_list);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,listContent));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("onItemClick", "onItemClick called");
                DatePickerFragmentInterface activity = (DatePickerFragmentInterface) getActivity();
                Spinner s = (Spinner) getActivity().findViewById(R.id.date_pick_spinner);

                ArrayAdapter<CharSequence> adapter = activity.getSpinnerAdapter();

                //Add item
                ArrayList<CharSequence> list = new ArrayList<CharSequence>();
                for(int i=0 ; i< adapter.getCount() ; i++) list.add(adapter.getItem(i));
                list.add(((YammDatePickerFragment)activity.getDatePickerFragment()).result+ " " + parent.getItemAtPosition(position));

                //Reset Adapter to spinner
                adapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.closed_spinner_item, list);
                adapter.setDropDownViewResource(R.layout.spinner_item);

                s.setAdapter(adapter);
                s.setSelection(list.size()-1);

                //Return some result
                dismiss();
            }
        });
        return view;
    }
}
