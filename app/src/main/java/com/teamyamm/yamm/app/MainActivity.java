package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;
import java.util.Vector;


public class MainActivity extends BaseActivity {
    private String[] navMenuTitles;
    private DrawerLayout drawerLayout;
    private ListView leftDrawer;
    private NewMainFragment mainFragment;
    private List<YammItem> selectedYammItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mainFragment = (NewMainFragment) getSupportFragmentManager().findFragmentById(R.id.new_main_fragment);

    }

    @Override
    public void onBackPressed() {
        goBackHome();
    }

    public List<YammItem> getYammItemSelectedList(){
        if (selectedYammItems==null){
            selectedYammItems = new Vector<YammItem>();
        }
        Collections.sort(selectedYammItems);
        return selectedYammItems;
    }

    public boolean addItemToSelectedList(YammItem a){
        if (selectedYammItems == null)
            selectedYammItems = new Vector<YammItem>();
        if (!selectedYammItems.contains(a))
            selectedYammItems.add(a);
        return true;
    }

    public boolean removeItemToSelectedList(YammItem a){
        if (selectedYammItems == null)
            return false;
        return selectedYammItems.remove(a);
    }
    ////////////////////////////////Private Methods/////////////////////////////////////////////////



}
