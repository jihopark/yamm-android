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
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

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

        trackEnteredInviteMixpanel();
    }

    @Override
    public void onBackPressed() {
        finishInvite();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finishInvite();
                break;
        }

        return true;
    }


    private void finishInvite(){
        finish();
        this.overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_slide_out);
    }


    /*
    * For FriendListInterface
    * */

    public List<YammItem> getList(int type){
        return contactList;
    }

    public void setConfirmButtonEnabled(boolean b, int type) {
        final int ty = type;
        enableButtonFlag = b;
        if (!enableButtonFlag && confirmButton.getVisibility() == View.VISIBLE){
            Animation slideOut = new TranslateAnimation(0, 0, 0,
                    getResources().getDimension(R.dimen.friends_list_confirm_button_height));
            slideOut.setDuration(getResources().getInteger(R.integer.confirm_button_slide_duration));
            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    confirmButton.setVisibility(View.GONE);
                    setConfirmButtonEnabled(enableButtonFlag, ty);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            confirmButton.startAnimation(slideOut);
        }
        else if (enableButtonFlag && confirmButton.getVisibility() == View.GONE){
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
            confirmButton.setVisibility(View.VISIBLE);
            confirmButton.startAnimation(slideIn);
        }
    }

    public String getFragmentTag(int type){
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

            trackSendInviteMixpanel("SMS", friendsFragment.getSelectedItems().size());

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
        IconPageIndicator indicator = (IconPageIndicator) findViewById(R.id.invite_page_indicator);
        InviteFragmentPagerAdapter adapter= new InviteFragmentPagerAdapter(getSupportFragmentManager());

        pager = (ViewPager) findViewById(R.id.invite_view_pager);
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(adapter);
    }

    private class InviteFragmentPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener, IconPagerAdapter {
        private final int NUMBER_OF_PAGES = 2;
        private String[] titles = {"주소록","카카오톡"};
        protected final int[] ICONS = new int[] {
                R.drawable.invite_phonebook,R.drawable.invite_kakao
        };
        public InviteFragmentPagerAdapter(FragmentManager fm) {
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
                    friendsFragment = new FriendsFragment();
                    return friendsFragment;
                case 1:
                    Bundle bundle = new Bundle();
                    bundle.putInt(KakaoFragment.TYPE, KakaoFragment.INVITE);
                    KakaoFragment fragment = new KakaoFragment();
                    fragment.setArguments(bundle);

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

    public void trackSendInviteMixpanel(String method, int count){
        JSONObject props = new JSONObject();

        try{
            props.put("method", method);
            props.put("count", count);
        }catch(JSONException e){
            Log.e("InviteActivity/trackSendInviteMixpanel","JSON Error");
        }

        mixpanel.track("Send Invite", props);
        Log.i("InviteActivity/trackSendInviteMixpanel","Send Invite Tracked " + method);
    }

    private void trackEnteredInviteMixpanel(){
        JSONObject props = new JSONObject();
        mixpanel.track("Entered Invite", props);
        Log.i("InviteActivity/trackEnteredInviteMixpanel","Entered Invite Tracked ");
    }



}