package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by parkjiho on 6/18/14.
 */
public class IntroImageFragment extends Fragment {
    private int position;
    private TextView textView;
    private ImageView imageView;


    public IntroImageFragment(int position){
        super();
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.intro_image_fragment, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.intro_image_view);
        textView = (TextView) rootView.findViewById(R.id.intro_image_fragment_text);
        textView.setText("Page" + position);

        return rootView;
    }
}