package com.teamyamm.yamm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by parkjiho on 5/26/14.
 */
public class FriendActivity extends BaseActivity implements FriendListInterface, DatePickerFragmentInterface {


    public final static int FRIEND_ACTIVITY_REQUEST_CODE = 1001;

    public final static String SELECTED_FRIEND_LIST = "fl";

    private boolean enableButtonFlag = false;
    private FriendsFragment friendsFragment;
    private List<Friend> friends;

    private Spinner datePickSpinner;
    public YammDatePickerFragment datePickerFragment;
    public ArrayAdapter<CharSequence> spinnerAdapter;

    private Button friendConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        setActionBarBackButton(true);

        friends = loadFriends();
        Log.i("FriendActivity/onResume","Loaded Friends");


        setConfirmButton();


        //Set Fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        friendsFragment = new FriendsFragment();
        fragmentTransaction.add(R.id.friend_activity_container, friendsFragment, getFragmentTag(friendsFragment.getContentType()));
        fragmentTransaction.commit();

        setDatePickSpinner();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.invite_button_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.friend_invite_button:
                startInviteActivity(FriendActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    * For DatePickerFragmentInterface
    * */
    public YammDatePickerFragment getDatePickerFragment(){return datePickerFragment;}
    public void setDatePickerFragment(YammDatePickerFragment fragment){ datePickerFragment = fragment; }
    public ArrayAdapter<CharSequence> getSpinnerAdapter(){return spinnerAdapter; }


    /*
    * For FriendListInterface
    * */

    public void setConfirmButtonEnabled(boolean b, int type){
        enableButtonFlag = b;
        confirmButtonAnimation(FriendActivity.this, friendConfirmButton, enableButtonFlag, type );
        /*if (!enableButtonFlag && friendConfirmButton.getVisibility() == View.VISIBLE){
            Animation slideOut = new TranslateAnimation(0, 0, 0,
                    getResources().getDimension(R.dimen.friends_list_confirm_button_height));
            slideOut.setDuration(getResources().getInteger(R.integer.confirm_button_slide_duration));
            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    friendConfirmButton.setVisibility(View.GONE);
                    setConfirmButtonEnabled(enableButtonFlag, ty);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            friendConfirmButton.startAnimation(slideOut);
        }
        else if (enableButtonFlag && friendConfirmButton.getVisibility() == View.GONE){
            Animation slideIn = new TranslateAnimation(0, 0,
                    getResources().getDimension(R.dimen.friends_list_confirm_button_height), 0);
            slideIn.setDuration(getResources().getInteger(R.integer.confirm_button_slide_duration));
            slideIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    setConfirmButtonEnabled(enableButtonFlag, ty);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            friendConfirmButton.setVisibility(View.VISIBLE);
            friendConfirmButton.startAnimation(slideIn);
        }*/
    }

    public List<YammItem> getList(int type){
        return FriendsFragment.setFriendListToYammItemList(friends);
    }

    public String getFragmentTag(int type){
        return "ff";
    }

    private void setConfirmButton(){
        friendConfirmButton = (Button) findViewById(R.id.friend_confirm_button);
        friendConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
    }


    private void finishActivity(){
        Intent intent = new Intent(FriendActivity.this, GroupRecommendationActivity.class);
        Type type = new TypeToken<ArrayList<Friend>>(){}.getType();

        intent.putExtra("friendlist",new Gson().toJson(friendsFragment.selectedItems, type));
        intent.putExtra("time", datePickSpinner.getSelectedItem().toString());

        startActivity(intent);
    }

    private List<Friend> loadFriends(){
        //Load Contacts From SharedPrefs
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);

        List<Friend> list = fromStringToFriendList(prefs.getString(getString(R.string.FRIEND_LIST),"none"));

        if (list == null){
            Log.e("FriendActivity/loadFriends","Failed to load contacts from shared pref");
            makeYammToast(getString(R.string.friend_not_loaded_message), Toast.LENGTH_SHORT);
            finish();
        }
        else{
            Log.i("FriendActivity/loadFriends", "Successfully loaded friends");
            Log.i("FriendActivity/loadFriends",list.toString());

        }
        return list;
    }

    private void setDatePickSpinner(){
        datePickSpinner = (Spinner) findViewById(R.id.date_pick_spinner);
        spinnerAdapter = ArrayAdapter.createFromResource(FriendActivity.this, R.array.date_spinner_array, R.layout.closed_spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        datePickSpinner.setAdapter(spinnerAdapter);
        datePickSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                if (pos == spinnerAdapter.getCount()-1 ){
                    datePickerFragment = new YammDatePickerFragment(FriendActivity.this);
                    if (checkIfAppIsRunning())
                        datePickerFragment.show(getSupportFragmentManager(), "timePicker");
                }
            }
            public void onNothingSelected(AdapterView<?> parent) { }

        });
    }

}
