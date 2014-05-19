package com.teamyamm.yamm.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by parkjiho on 5/15/14.
 */

public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    public FrameLayout yammFrameLayout;
    public ListView streamListView;
    public LinearLayout yammLayout1, yammLayout2;
    public EditText friendPickEditText;
    public AutoCompleteTextView placePickEditText;
    public StreamListAdapter adapter;
    public Button yammButton;
    public Spinner yammDateSpinner;
    public ArrayAdapter<CharSequence> spinnerAdapter;
    public GestureDetector detector;
    public DialogFragment datePickerFragment;
    public boolean yammLayoutToggling = false;
    public LinearLayout layout;

    public RelativeLayout friendsFragmentContainer;
    public FragmentManager fragmentManager;
    public FriendsFragment friendsFragment;
    private boolean friendDown = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("MainFragment/onCreateView", "onCreateView started");
        layout = (LinearLayout) inflater.inflate(R.layout.main_fragment, container, false);

        Log.v("MainFragment/onCreateView", "xml inflated");
        yammFrameLayout = (FrameLayout) layout.findViewById(R.id.yamm_framelayout);
        Log.v("MainFragment/onCreateView", "yamm layout found");
        streamListView = (ListView) layout.findViewById(R.id.stream_list_view);
        Log.v("MainFragment/onCreateView", "stream list view found");
        adapter = setStreamListAdapter();
        streamListView.setAdapter(adapter);
        streamListView.setOnScrollListener(new StreamScrollListener());
        Log.v("MainFragment/onCreateView", "stream list adapter set");

        //Set YammLayout
        yammLayout1 = (LinearLayout) layout.findViewById(R.id.yamm_layout1);
        yammLayout2 = (LinearLayout) layout.findViewById(R.id.yamm_layout2);
        friendsFragmentContainer = (RelativeLayout) layout.findViewById(R.id.friends_fragment_container);

        friendPickEditText = (EditText) layout.findViewById(R.id.friend_pick_edit_text);
        friendPickEditText.setOnTouchListener(setFriendPickEditTextOnTouchListener());

        //Set Place Pick Edit Text - autocomplete
        setPlacePickEditText();

        yammButton = (Button) layout.findViewById(R.id.yamm_button);
        yammButton.setOnClickListener(getYammButtonOnClickListener());

        //Set Date Spinner
        yammDateSpinner = (Spinner) layout.findViewById(R.id.yamm_date_spinner);
        setDateSpinner();

        //Set Layout Weight of yammFrameLayout & streamListView
        setYammAndStreamLayoutWeights(1f, 3f);

        //Add Gesture Detector to streamListView
        detector = new GestureDetector(getActivity(), new StreamGestureListener());
        streamListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                detector.onTouchEvent(event);
                return getActivity().onTouchEvent(event);
            }
        });

        //Fragment manager
        fragmentManager = getChildFragmentManager();

        return layout;
    }

    public boolean isFriendsListDown(){
        return friendDown;
    }

    public void putFriendsListUp(){
        if (!isFriendsListDown())
            return ;

        friendDown = false;

        //change layout weight
        setYammAndStreamLayoutWeights(1f, 3f);

        //create fragment
        FragmentTransaction t = fragmentManager.beginTransaction();
        friendsFragment = new FriendsFragment();
        t.remove(friendsFragment);
        t.commit();

        //Animation
    }

    public void putFriendsListDown(){
        if (isFriendsListDown())
            return ;

        friendDown =true;

        //change layout weight
        setYammAndStreamLayoutWeights(7f, 1f);
        friendsFragmentContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 5f));

        //create fragment
        FragmentTransaction t = fragmentManager.beginTransaction();
        friendsFragment = new FriendsFragment();
        t.add(R.id.friends_fragment_container, friendsFragment);
        t.commit();

        //Animation
    }


    ////////////////////////////////Private Methods
    private View.OnTouchListener setFriendPickEditTextOnTouchListener(){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //disable keyboard
                ((EditText)v).setInputType(InputType.TYPE_NULL);

                putFriendsListDown();

                v.onTouchEvent(event);
                return true;
            }
        };
    }


    private void setPlacePickEditText(){
        placePickEditText = (AutoCompleteTextView) layout.findViewById(R.id.place_pick_edit_text);
        ArrayAdapter<String> place_adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.places_array));
        placePickEditText.setAdapter(place_adapter);
        placePickEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    /*
    * Sets Date Spinner
    * */
    private void setDateSpinner(){
        spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.date_spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yammDateSpinner.setAdapter(spinnerAdapter);
        yammDateSpinner.setOnItemSelectedListener(this);
    }



    /*
    * Set Layout weights of yammFrameLayout and streamListView
    * */

    private void setYammAndStreamLayoutWeights(float a, float b){
        yammFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, a));
        streamListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, b));
    }



    /*
    * Changes visibility of yammLayout1 and yammLayout2
    * */
    private void toggleYammLayoutVisibility(){
        Animation alpha = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha);
        Animation slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        Animation slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);

        if (yammLayout1.getVisibility()==LinearLayout.GONE){
            yammLayout2.startAnimation(alpha);
            yammLayout1.startAnimation(slide_up);
            yammLayout2.setVisibility(LinearLayout.GONE);
            yammLayout1.setVisibility(LinearLayout.VISIBLE);
            setYammAndStreamLayoutWeights(1f, 7f);
        }
        else{
            yammLayout1.startAnimation(alpha);
            yammLayout2.startAnimation(slide_down);
            yammLayout1.setVisibility(LinearLayout.GONE);
            yammLayout2.setVisibility(LinearLayout.VISIBLE);
            setYammAndStreamLayoutWeights(1f, 3f);
        }
    }




    private StreamListAdapter setStreamListAdapter(){
        //GET INITIAL LIST FROM SERVER

        //FOR TESTING
        ArrayList<DishItem> list = new ArrayList<DishItem>();
        list.add(new DishItem(1,"설렁탕"));
        list.add(new DishItem(2,"된장국"));
        list.add(new DishItem(3,"치킨"));
        list.add(new DishItem(4,"피자"));
        list.add(new DishItem(5,"비빔냉면"));
        list.add(new DishItem(6,"순대국"));
        list.add(new DishItem(7,"물냉면"));
        list.add(new DishItem(8,"김치찜"));
        list.add(new DishItem(9,"꼼장어"));
        list.add(new DishItem(10,"똠얌꿍"));

        //Passes Screen Width
        return new StreamListAdapter(getActivity(), list, ((BaseActivity)getActivity()).getScreenWidth());
    }
    /*
    * Load more items on StreamListViewAdapter; returns false if no more to add
    * */
    private boolean loadMoreItemsOnAdapter(){
        Log.v("MainFragment/loadMoreItemsOnAdapter","More Items loaded");
        adapter.addDishItem(new DishItem(11,"샐러드"));
        adapter.addDishItem(new DishItem(12,"국밥"));
        adapter.addDishItem(new DishItem(13, "해장국"));
        adapter.addDishItem(new DishItem(14, "짜장면"));
        adapter.addDishItem(new DishItem(15, "짬뽕"));
        adapter.addDishItem(new DishItem(16, "탕수육"));
        adapter.addDishItem(new DishItem(17, "우동"));
        adapter.addDishItem(new DishItem(18, "라면"));
        adapter.addDishItem(new DishItem(19, "쫄면"));
        adapter.addDishItem(new DishItem(20, "막국수"));

        adapter.notifyDataSetChanged();
        return false;
    }

    ////////////////////////////////Listeners
    /*
    * For implementing AdapterView.OnItemSelectedListener
    * For Date Spinner
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

    /*
    * Yamm Button OnClickListener
    * */
    private View.OnClickListener getYammButtonOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleYammLayoutVisibility();
                Toast.makeText(getActivity(),"GONE",Toast.LENGTH_SHORT).show();
            }
        };
    }

    /*
    * Detects Scroll and manipulates yammlayout1 & 2
    * */
    private class StreamGestureListener extends GestureDetector.SimpleOnGestureListener{
        final int SCROLL_TOLERANCE = 10;
        float dX=0, dY=0;

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
            if (dX == 0 && dY == 0){
                dX = e1.getX();
                dY = e1.getY();
            }
            else{
                if (dX == e1.getX() && dY == e1.getY()){
                    dX = e1.getX();
                    dY = e1.getY();
                    return true;
                }
                dX = e1.getX();
                dY = e1.getY();
            }

            if (Math.abs(e1.getY() - e2.getY()) > SCROLL_TOLERANCE) {
                if (e1.getY() < e2.getY()) {
                    if (yammLayout2.getVisibility() == LinearLayout.GONE)
                        toggleYammLayoutVisibility();
                } else {
                    if (yammLayout1.getVisibility() == LinearLayout.GONE)
                        toggleYammLayoutVisibility();
                }
            }
            return true;
        }
    }

    /*
   * Custom Scroll Listener that loads more items if end of scroll detected in ListView
   * */
    private class StreamScrollListener implements AbsListView.OnScrollListener{
        private int visibleThreshold = 3; // how many items before loading new contents
        private boolean loading = true;
        private boolean data = true;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean toastShown = false;

        public void onScrollStateChanged(AbsListView view, int scrollState){ }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
            //         Log.v("ScrollListener","data "+ data + " firstVisibleItem "+ firstVisibleItem + "/visibleItemCount - " + visibleItemCount + "/totalItemCount - " + totalItemCount);

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (data && !loading && (totalItemCount - visibleItemCount) < (firstVisibleItem + visibleThreshold)) {  //End of Scroll
                // load more items
                data = loadMoreItemsOnAdapter();
                loading = true;
            }
            else if (!toastShown && !data && (totalItemCount - visibleItemCount) <= firstVisibleItem){
                Log.v("ScrollListener","Toast should come");
                Toast.makeText(getActivity().getApplicationContext(),R.string.stream_end_message,Toast.LENGTH_SHORT).show();
                toastShown = true;
            }
        }
    }

}
