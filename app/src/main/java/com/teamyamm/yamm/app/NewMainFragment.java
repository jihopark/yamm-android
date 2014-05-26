package com.teamyamm.yamm.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by parkjiho on 5/24/14.
 */
public class NewMainFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private final static int FRIEND_ACTIVITY_REQUEST_CODE = 1001;
    private FrameLayout main_layout;
    private ImageView imageOne, imageTwo;
    private int currentImage = 1;

    private Button friendPickButton, nextButton;
    private Spinner datePickSpinner;
    public ArrayAdapter<CharSequence> spinnerAdapter;
    public YammDatePickerFragment datePickerFragment;


    private ArrayList<Integer> selectedFriendList = new ArrayList<Integer>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MainFragment/onCreateView", "onCreateView started");

        main_layout = (FrameLayout) inflater.inflate(R.layout.new_main_fragment, container, false);
        friendPickButton = (Button) main_layout.findViewById(R.id.friends_pick_button);
        nextButton = (Button) main_layout.findViewById(R.id.next_button);
        datePickSpinner = (Spinner) main_layout.findViewById(R.id.date_pick_spinner);

        setYammImageView();
        setFriendPickButton();
        setNextButton();
        setDatePickSpinner();

        return main_layout;
    }

    private void setDatePickSpinner(){
        spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.date_spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        datePickSpinner.setAdapter(spinnerAdapter);
        datePickSpinner.setOnItemSelectedListener(this);
    }

    /*
    * For implementing AdapterView.OnItemSelectedListener
    * For Date Pick Spinner
    * */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        if (pos == getResources().getInteger(R.integer.spinner_datepick_pos) ){
            datePickerFragment = new YammDatePickerFragment();
            datePickerFragment.show(getChildFragmentManager(), "timePicker");
        }
    }
    public void onNothingSelected(AdapterView<?> parent) { }


    private void setYammImageView(){
        imageOne = (ImageView) main_layout.findViewById(R.id.main_image_view_one);
        imageOne.setAdjustViewBounds(true);
        imageOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageTwo = (ImageView) main_layout.findViewById(R.id.main_image_view_two);
        imageTwo.setAdjustViewBounds(true);
        imageTwo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageTwo.setVisibility(View.GONE);

        imageOne.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.example2));
        imageTwo.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.example));
    }

    private void setNextButton(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentImage == 1){
                    imageOne.setVisibility(View.GONE);
                    imageTwo.setVisibility(View.VISIBLE);
                    loadNextImage();
                    currentImage = 2;
                }
                else{
                    imageTwo.setVisibility(View.GONE);
                    imageOne.setVisibility(View.VISIBLE);
                    loadNextImage();
                    currentImage = 1;
                }
            }
        });
    }

    /*
    * Loads next image on main imageview
    * */
    private void loadNextImage(){

    }

    private void setFriendPickButton(){
        friendPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendActivity.class);
                v.setEnabled(false); //To prevent double fire

                intent.putIntegerArrayListExtra(FriendActivity.FRIEND_LIST, selectedFriendList); //send previously selected friend list
                startActivityForResult(intent, FRIEND_ACTIVITY_REQUEST_CODE);
                Log.i("MainFragment/onClick","FriendActivity called");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FRIEND_ACTIVITY_REQUEST_CODE){
            Log.i("MainFragment/onActivityResult","Got back from FriendActivity; resultcode: " + resultCode);

            friendPickButton.setEnabled(true);

            //Get Friend List
            selectedFriendList = data.getIntegerArrayListExtra(FriendActivity.FRIEND_LIST);

            Toast.makeText(getActivity(),"Got Back from Friend" + selectedFriendList, Toast.LENGTH_LONG).show();
        }
    }

}
