package com.example.papreeca.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;

public class IntroActivity extends BaseActivity {
    private ViewPager introPager;
    private PagerAdapter adapter;
    protected final int NUMBER_OF_PAGE = 4;

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
        introPager = (MyIntroViewPager)findViewById(R.id.intro_pager);
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
                    Toast.makeText(getApplicationContext(), "인트로 끝", Toast.LENGTH_SHORT).show(); //To be deleted

                    //Go to Battle Activity
                    Intent battleActivity = new Intent(getBaseContext(), BattleActivity.class);
                    startActivity(battleActivity);
                    //configIntroButton();
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

    }

    /**
     * A pager adapter that represents 4 ScreenSlidePageFragment objects, in
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
                    resId = R.layout.intro_one;
                    break;
                case 1:
                    resId = R.layout.intro_grid;
                    break;
                case 2:
                    resId = R.layout.intro_three;
                    break;
                case 3:
                    resId = R.layout.intro_final;
                    break;
            }

            View view = inflater.inflate(resId, null);

            ((ViewPager) collection).addView(view, 0);

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
}
