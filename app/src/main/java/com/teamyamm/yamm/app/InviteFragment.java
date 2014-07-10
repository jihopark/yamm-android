package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by parkjiho on 7/10/14.
 */
public class InviteFragment extends Fragment {
    private LinearLayout mainLayout;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainLayout = (LinearLayout) inflater.inflate(R.layout.invite_fragment, container, false);

        return mainLayout;

    }
}