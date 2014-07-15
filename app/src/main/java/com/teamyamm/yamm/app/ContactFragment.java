package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by parkjiho on 7/15/14.
 */
public class ContactFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_contact, container, false);

        return layout;
    }


}
