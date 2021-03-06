package com.teamyamm.yamm.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.teamyamm.yamm.app.interfaces.DatePickerFragmentInterface;
import com.teamyamm.yamm.app.interfaces.FriendListInterface;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.network.YammAPIService;
import com.teamyamm.yamm.app.pojos.DishItem;
import com.teamyamm.yamm.app.pojos.Friend;
import com.teamyamm.yamm.app.pojos.YammItem;
import com.teamyamm.yamm.app.util.WTFExceptionHandler;
import com.teamyamm.yamm.app.widget.YammDatePickerFragment;
import com.teamyamm.yamm.app.widget.YammIconPageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 8/11/14.
 */
public class PokeActivity extends BaseActivity implements FriendListInterface, DatePickerFragmentInterface {

    private Spinner datePickSpinner;
    public YammDatePickerFragment datePickerFragment;
    public ArrayAdapter<CharSequence> spinnerAdapter;

    private ViewPager pager;
    private List<YammItem> yammFriendsList, contactFriendsList;
    private FriendsFragment yammFriendsFragment, contactFriendsFragment;
    private boolean yammEnableButtonFlag = false, contactEnableButtonFlag = false;
    private Button yammConfirmButton, contactConfirmButton;

    private int suggestionType = YammFragment.TODAY;

    private DishItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poke);

        setActionBarBackButton(true);

        getBundle();
        setContactList();
        setFriendList();
        setDatePickSpinner();

        setButtons();
        setViewPager();

        MixpanelController.trackEnteredPokeFriendMixpanel();

    }
    private void getBundle(){
        currentItem = new Gson().fromJson(getIntent().getExtras().getString("dish"), DishItem.class);
        suggestionType = getIntent().getExtras().getInt("suggestionType");
        Log.i("PokeActivity/getBundle","Current Dish Item " + currentItem.getName() + " SuggestionType " + suggestionType);
    }

    private void setContactList(){
        HashMap<String, String> phoneNameMap;
        int count = 0;

        contactFriendsList = new ArrayList<YammItem>();
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
        String s = prefs.getString(getString(R.string.PHONE_NAME_MAP),"none");

        if (!s.equals("none")){
            phoneNameMap = fromStringToHashMap(s);
            for (String i : phoneNameMap.keySet()){
                YammItem item = new Friend(count++, phoneNameMap.get(i), i);
                contactFriendsList.add(item);
            }
        }
        Log.i("PokeActivity/setContactList", "Successfully loaded contacts");
    }
    private void setFriendList(){
        //Load Contacts From SharedPrefs
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);

        List<Friend> list = fromStringToFriendList(prefs.getString(getString(R.string.FRIEND_LIST),"none"));

        if (list == null){
            Log.e("PokeActivity/setFriendList", "Failed to load contacts from shared pref");
            makeYammToast(getString(R.string.friend_not_loaded_message), Toast.LENGTH_SHORT);
            finish();
        }
        else{
            Log.i("PokeActivity/setFriendList", "Successfully loaded friends");
        }
        yammFriendsList = FriendsFragment.setFriendListToYammItemList(list);
    }

    public String getSelectedTime(){
        return datePickSpinner.getSelectedItem().toString();
    }

    private void setButtons(){
        yammConfirmButton = (Button) findViewById(R.id.poke_yamm_confirm);
        contactConfirmButton = (Button) findViewById(R.id.poke_contact_confirm);

        yammConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("PokeActivity/onClickListener","YammFriend Onclicklistener " + yammFriendsFragment.selectedItems);
                Log.i("PokeActivity/onClickListener","Selected Time" + datePickSpinner.getSelectedItem());
                pokeWithYamm();
            }
        });

        contactConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("PokeActivity/onClickListener","ContactFriend Onclicklistener " + contactFriendsFragment.selectedItems);
                Log.i("PokeActivity/onClickListener","Selected Time" + datePickSpinner.getSelectedItem());
                pokeWithSMS();
            }
        });
    }

    private void pokeWithYamm(){

        List<Long> sendIds = new ArrayList<Long>();
        String time = datePickSpinner.getSelectedItem().toString();
        String meal = time.substring(time.length() - 2, time.length());
        time = time.substring(0, time.length() - 3);

        for (YammItem i : yammFriendsFragment.selectedItems)
            sendIds.add(i.getID());

        makeYammToast(friendKoreanPlural(yammFriendsFragment.selectedItems.size()) + " " + datePickSpinner.getSelectedItem().toString() + "에 "
                + currentItem.getName() + " 먹자고 했어요!", Toast.LENGTH_SHORT);


        YammAPIService service = YammAPIAdapter.getTokenService();

        if (service==null) {
            invalidToken();
            WTFExceptionHandler.sendLogToServer(PokeActivity.this, "WTF Invalid Token Error @PokeActivity/pokeWithYamm");
            return ;
        }
        service.sendPokeMessage(new YammAPIService.RawPokeMessage(sendIds, currentItem.getId(), time, meal), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.i("PokeActivity/sendPushMessage", "Push " + s);
                MixpanelController.trackPokeFriendMixpanel("YAMM", yammFriendsFragment.selectedItems.size(), datePickSpinner.getSelectedItem().toString(), currentItem.getName());
            }

            @Override
            public void failure(RetrofitError retrofitError) {

                String msg = retrofitError.getCause().getMessage();
                if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION)) {
                    Log.e("PokeActivity/pokeWithYamm", "Invalid Token, Logging out");
                    invalidToken();
                    return ;
                }
                Log.e("PokeActivity/sendPushMessage", "Error In Push Message");
                makeYammToast(R.string.unidentified_error_message, Toast.LENGTH_SHORT);
            }
        });
        finish();
    }

    private void pokeWithSMS(){
        startSMSIntent(getPokeMessage(datePickSpinner.getSelectedItem().toString(),currentItem.getName()
                ) + " " + appURL,contactFriendsFragment.getSelectedItems());
        MixpanelController.trackPokeFriendMixpanel("SMS",contactFriendsFragment.selectedItems.size(), datePickSpinner.getSelectedItem().toString(), currentItem.getName() );
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

    public List<YammItem> getList(int type){
        Log.i("PokeActivity/getList","Type is " + type);
        if (type == FriendsFragment.YAMM)
            return yammFriendsList;

        return contactFriendsList;
    }

    public void setConfirmButtonEnabled(boolean b, int type) {
        Log.i("PokeActivity/setConfirmButtonEnabled","Type is " + type);

        if (type == FriendsFragment.YAMM){
            yammEnableButtonFlag = b;
            confirmButtonAnimation(PokeActivity.this, yammConfirmButton, yammEnableButtonFlag, type);

        }
        else if (type == FriendsFragment.CONTACT){
            contactEnableButtonFlag = b;
            confirmButtonAnimation(PokeActivity.this, contactConfirmButton, contactEnableButtonFlag, type);
        }
    }

    public String getFragmentTag(int type){
        if (type == FriendsFragment.YAMM)
            return "android:switcher:" + pager.getId() + ":" + 0;
        return "android:switcher:" + pager.getId() + ":" + 1;
    }

    private void setViewPager(){
        YammIconPageIndicator indicator = (YammIconPageIndicator) findViewById(R.id.poke_page_indicator);
        PokeFragmentPagerAdapter adapter= new PokeFragmentPagerAdapter(getSupportFragmentManager());

        pager = (ViewPager) findViewById(R.id.poke_view_pager);
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(adapter);
    }

    private class PokeFragmentPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener, IconPagerAdapter {
        private final int NUMBER_OF_PAGES = 3;
        private String[] titles = {"얌친","주소록","카카오톡"};

        protected final int[] ICONS = new int[] {
                R.drawable.poke_yamm, R.drawable.poke_phonebook,R.drawable.poke_kakao
        };

        public PokeFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getIconResId(int index) {
            return ICONS[index % ICONS.length];
        }


        @Override
        public Fragment getItem(int index) {

            switch (index){
                case 0:
                    Bundle bundle = new Bundle();
                    bundle.putInt("contentType", FriendsFragment.YAMM);
                    yammFriendsFragment = new FriendsFragment();
                    yammFriendsFragment.setArguments(bundle);

                    return yammFriendsFragment;
                case 1:
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("contentType",FriendsFragment.CONTACT);

                    contactFriendsFragment = new FriendsFragment();
                    contactFriendsFragment.setArguments(bundle2);

                    return contactFriendsFragment;
                case 2:
                    Bundle bundle3 = new Bundle();

                    bundle3.putInt(KakaoFragment.TYPE, KakaoFragment.POKE);
                    bundle3.putString(KakaoFragment.DISH, getIntent().getExtras().getString("dish"));
                    bundle3.putString(KakaoFragment.TIME, datePickSpinner.getSelectedItem().toString());

                    KakaoFragment fragment = new KakaoFragment();
                    fragment.setArguments(bundle3);

                    return fragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUMBER_OF_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position){
            return titles[position];
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }

        @Override
        public void onPageSelected(int i) {
            if (i==0) {
                contactConfirmButton.setVisibility(View.GONE);
                if (yammEnableButtonFlag)
                    yammConfirmButton.setVisibility(View.VISIBLE);
                else
                    yammConfirmButton.setVisibility(View.GONE);
            }
            else if (i==1){
                yammConfirmButton.setVisibility(View.GONE);
                if (contactEnableButtonFlag)
                    contactConfirmButton.setVisibility(View.VISIBLE);
                else
                    contactConfirmButton.setVisibility(View.GONE);
            }
            else{
                contactConfirmButton.setVisibility(View.GONE);
                yammConfirmButton.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    }

    private void setDatePickSpinner(){
        datePickSpinner = (Spinner) findViewById(R.id.date_pick_spinner);
        spinnerAdapter = ArrayAdapter.createFromResource(PokeActivity.this, R.array.date_spinner_array, R.layout.closed_spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        datePickSpinner.setAdapter(spinnerAdapter);
        datePickSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                if (pos == spinnerAdapter.getCount()-1 ){
                    datePickerFragment = new YammDatePickerFragment(PokeActivity.this);
                    datePickerFragment.show(getSupportFragmentManager(), "timePicker");
                }
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        setDefaultValueForSpinner(datePickSpinner, suggestionType);
    }

    public String getPokeMessage(String time, String name){
        return time + "에 우리 " + name + " 먹을래요?";
    }
}
