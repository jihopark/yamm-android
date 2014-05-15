package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by parkjiho on 5/15/14.
 */

public class MainFragment extends Fragment {
    LinearLayout yammLayout;
    ListView streamListView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.main_fragment, container, false);
        yammLayout = (LinearLayout) layout.findViewById(R.id.yamm_layout);
        streamListView = (ListView) layout.findViewById(R.id.stream_list_view);



        return layout;
    }
}
