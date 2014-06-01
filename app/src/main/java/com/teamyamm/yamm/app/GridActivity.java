package com.teamyamm.yamm.app;

import android.os.Bundle;

/**
 * Created by parkjiho on 6/1/14.
 */
public class GridActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
    }

    @Override
    public void onBackPressed() {
        goBackHome();
    }

}
