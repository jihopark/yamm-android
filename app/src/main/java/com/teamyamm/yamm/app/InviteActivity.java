package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.TabPageIndicator;

/**
 * Created by parkjiho on 7/10/14.
 */
public class InviteActivity extends BaseActivity {

    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        setActionBarBackButton(true);

        setViewPager();
    }

    private void setViewPager(){
        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.invite_page_indicator);
        pager = (ViewPager) findViewById(R.id.invite_view_pager);
        pager.setAdapter(new InviteFragmentPagerAdapter(getSupportFragmentManager()));
        indicator.setViewPager(pager);
    }

    private static class InviteFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int NUMBER_OF_PAGES = 2;
        private String[] titles = {"주소록","카카오톡"};

        public InviteFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index){
                case 0:
                    return new ContactFragment();
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
    }
}