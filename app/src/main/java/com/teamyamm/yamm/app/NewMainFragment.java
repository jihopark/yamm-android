package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;

/**
 * Created by parkjiho on 5/24/14.
 */
public class NewMainFragment extends Fragment {
    FrameLayout main_layout;
    ImageView main_imageview;
    Spinner yammDateSpinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MainFragment/onCreateView", "onCreateView started");

        main_layout = (FrameLayout) inflater.inflate(R.layout.new_main_fragment, container, false);

        setYammImageView();

        return main_layout;
    }

    private void setYammImageView(){
        main_imageview = (ImageView) main_layout.findViewById(R.id.main_image_view);
        main_imageview.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.example));
        main_imageview.setAdjustViewBounds(true);
        main_imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }
}
