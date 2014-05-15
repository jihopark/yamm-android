package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends BaseActivity {
    private String[] navMenuTitles;
    private DrawerLayout drawerLayout;
    private ListView leftDrawer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("MainActivity/onCreate", "onCreate started");

        navMenuTitles = getResources().getStringArray(R.array.nav_menu_titles);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (ListView) findViewById(R.id.left_drawer);

        Log.v("MainActivity/onCreate","Drawer Initialized");
        // Set the adapter for the list view
        leftDrawer.setAdapter(new ArrayAdapter<String>(this,
                R.layout.left_drawer_item, navMenuTitles));

        //Set up Main Fragment


    }


    ////////////////////////////////Private Methods/////////////////////////////////////////////////



}
