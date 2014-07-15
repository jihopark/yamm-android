package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * Created by parkjiho on 6/18/14.
 */
public class IntroImageFragment extends Fragment {
    private int position;
    private ImageView imageView;

    public IntroImageFragment(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_intro_image, container, false);
        Bundle b = getArguments();

        position = b.getInt("position");
        Log.i("IntroImageFragment","got position " + position);

        imageView = (ImageView) rootView.findViewById(R.id.intro_image_view);

        //imageView = new IntroImageView(getActivity());

        //rootView.addView(imageView, 0);
        setImageView();

        return rootView;
    }

    private void setImageView(){
        Log.i("IntroImageFragment/setImageView","Created Image for " + position);
        imageView.setImageDrawable(getActivity().getResources().getDrawable(getResources().getIdentifier("@drawable/intro_0" + (position + 1), "drawable", getActivity().getPackageName())));
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
    }
}