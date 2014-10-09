package com.teamyamm.yamm.app.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamyamm.yamm.app.R;

/**
 * Created by parkjiho on 10/9/14.
 */
public class PhoneAuthFragment extends Fragment {
    public PhoneAuthFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_phone_auth, container, false);

        return rootView;
    }
}
