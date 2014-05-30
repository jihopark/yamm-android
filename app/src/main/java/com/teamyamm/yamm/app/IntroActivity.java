package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;

public class IntroActivity extends BaseActivity {
    private IntroViewPager introPager;
    private PagerAdapter adapter;
    private GridFragment gridFragment;
    protected final int NUMBER_OF_PAGE = 4;
    protected final int INTRO_JOIN_PAGE = 0;
    protected final int INTRO_VERI_PAGE = 1;

    //For intro_join

    private LinearLayout verificationLayout;
    private LinearLayout joinLayout;
    private boolean isVerificationLayoutInflated = false;
    private final int JOIN_LAYOUT = 1;
    private final int VERI_LAYOUT = 2;
    private int currentFrame = JOIN_LAYOUT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        hideActionBar();
        configViewPager();

    }


    /*
    * Go to Home Screen When Back Button of IntroActivity
    * */
    @Override
    public void onBackPressed() {
        goBackHome();
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

    /*
    * Config ViewPager and Indicator(OnPageChangeListener to config intro_button
    * https://github.com/JakeWharton/Android-ViewPagerIndicator
    * */

    private void configViewPager(){
        introPager = (IntroViewPager)findViewById(R.id.intro_pager);
        adapter = new IntroPagerAdapter(getApplicationContext());

        introPager.setAdapter(adapter);

        //Bind the title indicator to the adapter
        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);

        //Set Indicator Color
        indicator.setFillColor(Color.BLACK);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if (position == NUMBER_OF_PAGE - 1) {
                    //Save Grid Result to pref & Send GridResult to Server & Go To Battle Activity
                    gridFragment = (GridFragment) getSupportFragmentManager().findFragmentById(R.id.grid_fragment);
                    finishIntro();
                }

                if (position == INTRO_VERI_PAGE){
                    Log.i("IntroActivity/onPageSelected", "Intro Verification Page Initiated");
                    verificationLayout = (LinearLayout)findViewById(R.id.verification_layout);
                    setVerificationLayout();
                }

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        indicator.setViewPager(introPager);
        indicator.onPageSelected(0);
    }
    /*
    * Finshes Intro and saves&sends gridresults and go to BattleActivity
    * */
    private void finishIntro(){
        boolean resultSent = true;
        Log.i("IntroActivity/finishBattle", "FinishIntro Started");

        //Save to Shared Pref
        String result = saveGridResult(gridFragment);

        //Send to Server

        if (!sendGridResult(result)){
            Log.e("Server Communication Error", "Sending Battle Results Failed");
            showInternetConnectionAlert(new CustomInternetListener(internetAlert));
            resultSent=false;
        }
        //If sendBattle Result Failed, don't go to Battle Activity
        if (resultSent!=false) {
            goToActivity(BattleActivity.class);
        }
    }

    /*
    * Send Grid Selected Result to server
    * Only executed right before stating Battle Activity
    * */
    private boolean sendGridResult(String s){
        Log.i("IntroActivity/sendGridResults", "sendGridResults Started");
        //Check internet connection
        if (!checkInternetConnection()){
            return false;
        }
        return true;
    }

    /*
   * Custom Listener for Intro Activity InternetDialog
   * */
    private class CustomInternetListener implements View.OnClickListener {
        private final Dialog dialog;
        public CustomInternetListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            Log.i("IntroActivity/CustomInternetListener", "Listener activated");
            if (checkInternetConnection()) {
                Log.i("IntroActivity/CustomInternetListener","Internet came back");
                dialog.dismiss();
                finishIntro();
            }
        }
    }

     /*
    * Save Grid Selected Result to shared preferences
    * Only executed right before stating Battle Activity
    * */
    private String saveGridResult(GridFragment f){
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
        String s = "";
        for (GridItem i : f.getSelectedItems())
            s = s +i.getId()+",";
        BaseActivity.putInPref(prefs,getString(R.string.GRID_RESULT),s);
        Log.i("IntroActivity/saveGridResult","Grid Result Saved - "+ f.getSelectedItems());
        return s;
    }

    /**
     * A pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class IntroPagerAdapter extends PagerAdapter {
        private LayoutInflater mInflater;

        public IntroPagerAdapter(Context c){
            super();
            mInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return NUMBER_OF_PAGE;
        }

        @Override
        public Object instantiateItem(View collection, int position) {

            LayoutInflater inflater = (LayoutInflater) collection.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.layout.intro_join;
                    break;
                case 1:
                    resId = R.layout.intro_veri;
                    break;
                case 2:
                    resId = R.layout.intro_grid;
                    break;
                case 3:
                    resId = R.layout.intro_three;
                    break;
                case 4:
                    resId = R.layout.intro_final;
                    break;
            }

            View view = inflater.inflate(resId, null);

            ((ViewPager) collection).addView(view, 0);
            Log.i("IntroActivity/instantiateItem", "Pager Item " + position + " inflated");

            if (position == INTRO_JOIN_PAGE){
                Log.i("IntroActivity/onPageSelected", "Intro Join Page Initiated");
                joinLayout = (LinearLayout)view.findViewById(R.id.join_layout);
                ((EditText) joinLayout.findViewById(R.id.pw_field)).setTransformationMethod(new HiddenPassTransformationMethod());
                configSendButton(joinLayout);
                introPager.setPagingEnabled(false);
            }

            return view;
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager)pager).removeView((View)view);
        }

        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //For intro_join

    /*
    * When SendVerificationCode Button is pressed, inflates next frame
    * */

    private void configSendButton(LinearLayout layout){
        Button sendV = (Button) layout.findViewById(R.id.send_verification_code);

        sendV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introPager.setCurrentItem(INTRO_VERI_PAGE, true);
            }
        });
    }

    /*
    * Changes Frame
    * */
    private void changeFrame(){
        if (currentFrame==JOIN_LAYOUT){
            verificationLayout.setVisibility(View.VISIBLE);
            joinLayout.setVisibility(View.INVISIBLE);
            currentFrame = VERI_LAYOUT;
        }
        else{
            verificationLayout.setVisibility(View.INVISIBLE);
            joinLayout.setVisibility(View.VISIBLE);
            currentFrame = JOIN_LAYOUT;
        }
    }

    /*
    * Set VerificationLayout
    * */

    private void setVerificationLayout(){
        configVeriConfirmButton();
        configVeriAgainButton();
        configVeriResendButton();
    }

    /*
    * Config Verification Confirm Button that goes to next activity
    * */

    private void configVeriConfirmButton(){
        Button veriConfirmButton = (Button) findViewById(R.id.verification_confirm_button);

        veriConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"인증되었습니다",Toast.LENGTH_SHORT).show();

            }
        });
    }

    /*
    * Config Verification Again Button that goes back to previous stage
    * */

    private void configVeriAgainButton(){
        TextView veriAgainButton = (TextView) findViewById(R.id.verification_again_button);

        veriAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introPager.setCurrentItem(INTRO_JOIN_PAGE, true);
            }
        });
    }

    /*
    * Config Verification Resend Button that resends veri code sms
    * */
    private void configVeriResendButton(){
        TextView veriResendButton = (TextView) findViewById(R.id.verification_resend_button);
        veriResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), getString(R.string.verification_resend_message),Toast.LENGTH_SHORT).show();
                    }
                };

                createDialog(IntroActivity.this, R.string.verification_dialog_title, R.string.verification_dialog_message,
                        R.string.dialog_positive, R.string.dialog_negative,positiveListener, null).show();
            }
        });
    }

    /*
    * To show the last character of password
    * */
    private class HiddenPassTransformationMethod implements TransformationMethod {

        private char DOT = '\u2022';

        @Override
        public CharSequence getTransformation(final CharSequence charSequence, final View view) {
            return new PassCharSequence(charSequence);
        }

        @Override
        public void onFocusChanged(final View view, final CharSequence charSequence, final boolean b, final int i,
                                   final Rect rect) {
            //nothing to do here
        }

        private class PassCharSequence implements CharSequence {

            private final CharSequence charSequence;

            public PassCharSequence(final CharSequence charSequence) {
                this.charSequence = charSequence;
            }

            @Override
            public char charAt(final int index) {
                if (index == length() - 1)
                    return charSequence.charAt(index);
                return DOT;
            }

            @Override
            public int length() {
                return charSequence.length();
            }

            @Override
            public CharSequence subSequence(final int start, final int end) {
                return new PassCharSequence(charSequence.subSequence(start, end));
            }
        }
    }
}
