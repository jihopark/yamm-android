package com.teamyamm.yamm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.viewpagerindicator.TabPageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by parkjiho on 7/10/14.
 */
public class InviteActivity extends BaseActivity implements FriendListInterface {

    private ViewPager pager;
    private List<YammItem> contactList;
    private boolean enableButtonFlag = false;
    private Button confirmButton;
    private FriendsFragment friendsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);


        setActionBarBackButton(true);
        setContactList();
        setViewPager();
        setConfirmButton();

    }


    /*
    * For FriendListInterface
    * */

    public List<YammItem> getList(){
        return contactList;
    }

    public void setConfirmButtonEnabled(boolean b) {

        if (enableButtonFlag != b) {
            enableButtonFlag = b;
            Log.i("InviteActivity/setConfirmButtonEnabled","ConfirmButton" + enableButtonFlag + (confirmButton.getVisibility() == View.GONE));
            if (!enableButtonFlag && confirmButton.getVisibility() == View.VISIBLE){
                confirmButton.setVisibility(View.GONE);
            }
            else if (enableButtonFlag && confirmButton.getVisibility() == View.GONE){
                confirmButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public String getFragmentTag(){
        return "android:switcher:" + pager.getId() + ":" + 0;
    }

    private void setConfirmButton(){
        confirmButton = (Button) findViewById(R.id.invite_contact_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("InviteActivity/confirmButtonOnClick",friendsFragment.getSelectedItems().toString());
                startSMSIntent();
            }
        });
    }

    private void startSMSIntent(){
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.putExtra("sms_body", getString(R.string.invite_sms_body));

        //Get Senders

        String separator = "; ";
        if(android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")){
            separator = ", ";
        }
        try {
            String s = "";
            List<YammItem> items = friendsFragment.getSelectedItems();
            if (items.size() == 1){
                s = ((Friend)items.get(0)).getPhone();
            }
            else {
                for (YammItem i : items){
                    s += ((Friend)i).getPhone();
                    s += separator;
                }
                s = s.substring(0, s.length() - 1);
            }
            Log.d("aa",s);
            sendIntent.setData(Uri.parse("smsto:"+s));

            trackInviteMixpanel("SMS", friendsFragment.getSelectedItems().size());

        } catch (Exception e) {
            makeErrorToast(getString(R.string.invite_sms_error_message),Toast.LENGTH_SHORT);
            Log.e("InviteActivity/startSMSIntent","SMS Error");
            e.printStackTrace();
        }
        startActivity(sendIntent);
    }

    private void setContactList(){
        HashMap<String, String> phoneNameMap;
        int count = 0;

        contactList = new ArrayList<YammItem>();
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
        String s = prefs.getString(getString(R.string.PHONE_NAME_MAP),"none");

        if (!s.equals("none")){
            phoneNameMap = fromStringToHashMap(s);
            for (String i : phoneNameMap.keySet()){
                YammItem item = new Friend(count++, phoneNameMap.get(i), i);
                contactList.add(item);
            }
        }
    }

    private void setViewPager(){
        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.invite_page_indicator);
        InviteFragmentPagerAdapter adapter= new InviteFragmentPagerAdapter(getSupportFragmentManager());

        pager = (ViewPager) findViewById(R.id.invite_view_pager);
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(adapter);
    }

    private class InviteFragmentPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        private final int NUMBER_OF_PAGES = 2;
        private String[] titles = {"주소록","카카오톡"};

        public InviteFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index){
                case 0:
                    friendsFragment = new FriendsFragment();
                    return friendsFragment;
                case 1:
                    return new KakaoFragment();
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
            if (i!=0) {
                confirmButton.setVisibility(View.GONE);
            }
            else if (i==0 && enableButtonFlag){
                confirmButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    }

    public void trackInviteMixpanel(String method, int count){
        JSONObject props = new JSONObject();

        try{
            props.put("method", method);
            props.put("count", count);
        }catch(JSONException e){
            Log.e("InviteActivity/trackInviteMixpanel","JSON Error");
        }

        mixpanel.track("Invite", props);
        Log.i("InviteActivity/trackInviteMixpanel","Invite Tracked " + method);
    }



}