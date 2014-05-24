package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * Created by parkjiho on 5/24/14.
 */
public class NewMainFragment extends Fragment {
    LinearLayout main_layout;
    ImageView main_imageview;
    Spinner yammDateSpinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("MainFragment/onCreateView", "onCreateView started");
        main_layout = (LinearLayout) inflater.inflate(R.layout.new_main_fragment, container, false);
        main_imageview = (ImageView) main_layout.findViewById(R.id.main_imageview);

        main_imageview.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.example));
        main_imageview.setAdjustViewBounds(true);
        main_imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);

        yammDateSpinner = (Spinner) main_layout.findViewById(R.id.yamm_date_spinner);
        setDateSpinner();

        return main_layout;
    }

    private void setDateSpinner(){
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.date_spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yammDateSpinner.setAdapter(spinnerAdapter);
        //yammDateSpinner.setOnItemSelectedListener(this); // need to set this
    }
}
