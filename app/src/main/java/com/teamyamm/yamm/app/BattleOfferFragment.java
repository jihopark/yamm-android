package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by parkjiho on 7/17/14.
 */
public class BattleOfferFragment extends Fragment {

    private RelativeLayout main_layout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main_layout = (RelativeLayout) inflater.inflate(R.layout.fragment_battle_offer, container, false);

        return main_layout;
    }
}
