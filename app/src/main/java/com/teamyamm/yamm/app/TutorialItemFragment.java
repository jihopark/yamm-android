package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by parkjiho on 9/4/14.
 */
public class TutorialItemFragment extends Fragment {

    private int position;
    private TextView explanation;
    private TutorialFragment parentFragment;

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

        if (getParentFragment() instanceof TutorialFragment)
            parentFragment = (TutorialFragment) getParentFragment();
        else {
            Log.e("TutorialItemFragment/onCreateView", "Wrong Parent View");
            return null;
        }

        return rootView;
    }
}
