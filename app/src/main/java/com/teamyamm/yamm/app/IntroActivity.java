package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by parkjiho on 5/31/14.
 */
public class IntroActivity extends BaseActivity {

    private Button joinButton, loginButton;
    private final static int NUMBER_OF_PAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_intro);

        setButtons();
        setViewPager();
        hideActionBar();
    }

    private void setViewPager(){
    /*    ViewPager pager = (ViewPager) findViewById(R.id.intro_view_pager);
        pager.setAdapter(new IntroPagerAdapter(getApplicationContext()));
        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.intro_view_pager_indicator);
        indicator.setFillColor(Color.BLACK);
        indicator.setViewPager(pager);*/
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

    /**
     * A pager adapter that represents 3 ScreenSlidePageFragment objects, in
     * sequence.
     */
    /*private class IntroPagerAdapter extends PagerAdapter {
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
    }*/
}
