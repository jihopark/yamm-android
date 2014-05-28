package com.teamyamm.yamm.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
    private AutoCompleteTextView placePickEditText;
    private RelativeLayout mainButtonsContainer;
    private ArrayList<Integer> selectedFriendList = new ArrayList<Integer>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MainFragment/onCreateView", "onCreateView started");

        main_layout = (FrameLayout) inflater.inflate(R.layout.new_main_fragment, container, false);
        friendPickButton = (Button) main_layout.findViewById(R.id.friends_pick_button);
        nextButton = (Button) main_layout.findViewById(R.id.next_button);
        datePickSpinner = (Spinner) main_layout.findViewById(R.id.date_pick_spinner);
        mainButtonsContainer = (RelativeLayout) main_layout.findViewById(R.id.main_buttons_container);

        setYammImageView();
        setFriendPickButton();
        setNextButton();
        setDatePickSpinner();
        setPlacePickEditText();

        return main_layout;
    }

    private void setPlacePickEditText(){
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        placePickEditText = new CustomAutoCompleteTextView(getActivity());
        placePickEditText.setId(R.id.place_pick_edit_text);
        placePickEditText.setText(getString(R.string.place_pick_edit_text));
        placePickEditText.setThreshold(1);
        placePickEditText.setSelectAllOnFocus(true);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.friends_pick_button);
        placePickEditText.setLayoutParams(params);

        mainButtonsContainer.addView(placePickEditText);

        unfocusPlacePickEditText(main_layout);
        ArrayAdapter<String> place_adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.places_array));
        placePickEditText.setAdapter(place_adapter);
        placePickEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    Log.i("NewMainFragment/placePickEditText","focus gone");
                    if ( ((TextView)v).getText().toString().equals("") ) {
                        ((TextView) v).setText(getActivity().getString(R.string.place_pick_edit_text));
                    }
                }
                else{
                    ((TextView)v).setText("");
                }
            }
        });
    }

    /*
    * For unfocusing PlacePickEditText when other views are touched
    * */
    private void unfocusPlacePickEditText(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof CustomAutoCompleteTextView)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    BaseActivity.hideSoftKeyboard(getActivity());
                    placePickEditText.clearFocus();
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                unfocusPlacePickEditText(innerView);
            }
        }
    }

    /*
     * For unfocusing PlacePickEditText when back button is pressed
     * */
    public class CustomAutoCompleteTextView extends AutoCompleteTextView{
        public CustomAutoCompleteTextView(Context context){ super(context); }
        public CustomAutoCompleteTextView(Context context, AttributeSet attrs){ super(context, attrs); }
        public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle){ super(context, attrs, defStyle); }

        @Override
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                clearFocus();
            }
            return super.dispatchKeyEvent(event);
        }
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
                //Read Contact for update
                ((MainActivity)getActivity()).readContacts();

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

            if (resultCode == BaseActivity.SUCCESS_RESULT_CODE) {
                //Get Friend List
                selectedFriendList = data.getIntegerArrayListExtra(FriendActivity.FRIEND_LIST);

                Toast.makeText(getActivity(), "Got Back from Friend" + selectedFriendList, Toast.LENGTH_LONG).show();
            }
        }
    }

}
