package com.teamyamm.yamm.app.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.teamyamm.yamm.app.R;

/**
 * Created by parkjiho on 9/4/14.
 */
public class TutorialItemFragment extends Fragment {

    private int position;
    private TextView explanation, title;
    private TutorialFragment parentFragment;

    private int[] resId  = {R.string.tutorial_page_1, R.string.tutorial_page_2, R.string.tutorial_page_3,
            R.string.tutorial_page_4, R.string.tutorial_page_5, R.string.tutorial_page_6, R.string.tutorial_page_7};
    private int[] titleResId  = {R.string.tutorial_page_1_title, R.string.tutorial_page_2_title, R.string.tutorial_page_3_title,
            R.string.tutorial_page_4_title, R.string.tutorial_page_5_title, R.string.tutorial_page_6_title, R.string.tutorial_page_7_title};


    public TutorialItemFragment(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_tutorial_item, container, false);
        Bundle b = getArguments();

        position = b.getInt("position");

        explanation = (TextView) rootView.findViewById(R.id.tutorial_explanation);
        title = (TextView) rootView.findViewById(R.id.tutorial_explanation_title);
        Button button = (Button) rootView.findViewById(R.id.start_button);
        explanation.setText(getString(resId[position]));
        title.setText(getString(titleResId[position]));

        if (getParentFragment() instanceof TutorialFragment)
            parentFragment = (TutorialFragment) getParentFragment();
        else {
            Log.e("TutorialItemFragment/onCreateView", "Wrong Parent View");
            return null;
        }

        if (position == (parentFragment.NUM_PAGES - 1)){
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getParentFragment() instanceof TutorialFragment)
                        ((TutorialFragment) getParentFragment()).dismissAllowingStateLoss();
                }
            });
        }



        return rootView;
    }
}