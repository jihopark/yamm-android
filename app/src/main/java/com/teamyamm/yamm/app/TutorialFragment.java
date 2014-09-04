package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by parkjiho on 9/3/14.
 */
public class TutorialFragment extends Fragment {
    public final static String TAG = "tutorial";
    public final int NUM_PAGES = 6;

    private RelativeLayout main_layout;
    private ViewPager pager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("TutorialFragment/onCreateView", "onCreateView started");

        main_layout = (RelativeLayout) inflater.inflate(R.layout.fragment_tutorial, container, false);

        setViewPager();
        setCloseButton();

        return main_layout;
    }

    public void toggleView(int resId){
        View v = main_layout.findViewById(resId);
        if (v!=null) {
            if (v.getVisibility()==View.VISIBLE)
                v.setVisibility(View.INVISIBLE);
            else
                v.setVisibility(View.VISIBLE);
        }
    }

    public void disableAllView(){
        int[] resIds = {R.id.fake_dish_dislike_button, R.id.fake_dish_next_button, R.id.fake_poke_friend_button,
                            R.id.fake_friend_pick_button, R.id.fake_search_map_button};
        for (int id : resIds){
            main_layout.findViewById(id).setVisibility(View.INVISIBLE);
        }
    }

    private void setCloseButton(){
        ImageButton close = (ImageButton) main_layout.findViewById(R.id.tutorial_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity){
                    MainActivity activity = (MainActivity) getActivity();
                    activity.closeTutorialFragment();
                }

            }
        });
    }

    private void setViewPager(){
        TutorialPagerAdapter adapter = new TutorialPagerAdapter(getChildFragmentManager());

        pager = (ViewPager) main_layout.findViewById(R.id.tutorial_view_pager);
        pager.setAdapter(adapter);
        YammCirclePageIndicator indicator = (YammCirclePageIndicator) main_layout.findViewById(R.id.tutorial_view_pager_indicator);

        indicator.setRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));

        indicator.setViewPager(pager);
        pager.setOnPageChangeListener(adapter);
    }

    private class TutorialPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
        public TutorialPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = new TutorialItemFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position",position);
            f.setArguments(bundle);
            return f;
        }
        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }

        @Override
        public void onPageSelected(int i) {
            switch(i) {
                case 0:
                    disableAllView();
                    main_layout.setBackgroundResource(R.drawable.tutorial_background);
                    break;
                case 1:
                    disableAllView();
                    main_layout.setBackgroundColor(getResources().getColor(R.color.tutorial_background));
                    toggleView(R.id.fake_poke_friend_button);
                    break;
                case 2:
                    disableAllView();
                    toggleView(R.id.fake_search_map_button);
                    break;
                case 3:
                    disableAllView();
                    toggleView(R.id.fake_dish_dislike_button);
                    break;
                case 4:
                    disableAllView();
                    toggleView(R.id.fake_dish_next_button);
                    break;
                case 5:
                    disableAllView();
                    toggleView(R.id.fake_friend_pick_button);
                    break;
            }
        }


        @Override
        public void onPageScrollStateChanged(int i) {
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
