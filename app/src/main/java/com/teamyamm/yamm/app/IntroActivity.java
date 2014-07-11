package com.teamyamm.yamm.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by parkjiho on 5/31/14.
 */
public class IntroActivity extends BaseActivity {

    private Button joinButton, loginButton;
    private final static int NUM_PAGES = 3;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_intro);

        setButtons();
        setViewPager();
        hideActionBar();
    }


    @Override
    public void onBackPressed() {
        goBackHome();
    }

    private void setViewPager(){
        pager = (ViewPager) findViewById(R.id.intro_view_pager);
        pager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.intro_view_pager_indicator);

        indicator.setFillColor(Color.WHITE);
        indicator.setStrokeColor(Color.WHITE);
        indicator.setPageColor(Color.BLACK);
        indicator.setRadius(8.0f);
        indicator.setViewPager(pager);
    }

    private void setButtons(){
        joinButton = (Button) findViewById(R.id.join_button);
        loginButton = (Button) findViewById(R.id.login_button);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(JoinActivity.class);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(LoginActivity.class);
            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = new IntroImageFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position",position);
            f.setArguments(bundle);
            return f;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
