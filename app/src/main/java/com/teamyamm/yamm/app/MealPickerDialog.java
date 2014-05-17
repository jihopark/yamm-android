package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by parkjiho on 5/17/14.
 */
public class MealPickerDialog extends DialogFragment {
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
        return view;
    }
}
