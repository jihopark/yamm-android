package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by parkjiho on 5/12/14.
 */
public class BattleFragment extends Fragment{
    private TextView tv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.battle_fragment, container, true);
        tv = (TextView) layout.findViewById(R.id.fragment_text);
        return layout;
    }

    public void setText(String s){
        tv.setText(s);
    }
}
