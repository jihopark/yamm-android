package com.teamyamm.yamm.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;


public class MainActivity extends BaseActivity {
    public final static String MAIN_FRAGMENT = "mf";

    private String[] navMenuTitles;
    private DrawerLayout drawerLayout;
    private ListView leftDrawer;
    private NewMainFragment mainFragment;
    private List<YammItem> selectedYammItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTransparent();
        setContentView(R.layout.activity_main);


        Log.i("MainActivity/onCreate", "onCreate started");

        navMenuTitles = getResources().getStringArray(R.array.nav_menu_titles);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (ListView) findViewById(R.id.left_drawer);

        Log.i("MainActivity/onCreate","Drawer Initialized");
        // Set the adapter for the list view
        leftDrawer.setAdapter(new ArrayAdapter<String>(this,
                R.layout.left_drawer_item, navMenuTitles));

        //Set up Main Fragment
        mainFragment = new NewMainFragment();
        FragmentTransaction tact = getSupportFragmentManager().beginTransaction();
        tact.add(R.id.main_content_frame, mainFragment, MAIN_FRAGMENT);
        tact.commit();
    }

    @Override
    public void onBackPressed() {
        goBackHome();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////



}
