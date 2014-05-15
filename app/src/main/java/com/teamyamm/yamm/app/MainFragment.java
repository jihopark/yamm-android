package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by parkjiho on 5/15/14.
 */

public class MainFragment extends Fragment {
    LinearLayout yammLayout;
    ListView streamListView;
    StreamListAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("MainFragment/onCreateView", "onCreateView started");
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.main_fragment, container, false);

        Log.v("MainFragment/onCreateView", "xml inflated");
        yammLayout = (LinearLayout) layout.findViewById(R.id.yamm_layout);
        Log.v("MainFragment/onCreateView", "yamm layout found");
        streamListView = (ListView) layout.findViewById(R.id.stream_list_view);
        Log.v("MainFragment/onCreateView", "stream list view found");
        adapter = setStreamListAdapter();
        streamListView.setAdapter(adapter);
        streamListView.setOnScrollListener(new StreamScrollListener());
        Log.v("MainFragment/onCreateView", "stream list adapter set");


        return layout;
    }

    ////////////////////////////////Private Methods
    /*
    * Custom Scroll Listener that loads more items if end of scroll detected in ListView
    * */
    private class StreamScrollListener implements AbsListView.OnScrollListener{

        public void onScrollStateChanged(AbsListView view, int scrollState){ }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
            Log.v("ScrollListener","firstVisibleItem "+ firstVisibleItem + "/visibleItemCount - " + visibleItemCount + "/totalItemCount - " + totalItemCount);
            //if (firstVisibleItem + visibleItemCount totalItemCount)
        }
    }


    private StreamListAdapter setStreamListAdapter(){
        //GET INITIAL LIST FROM SERVER

        //FOR TESTING
        ArrayList<DishItem> list = new ArrayList<DishItem>();
        list.add(new DishItem(1,"설렁탕"));
        list.add(new DishItem(2,"된장국"));
        list.add(new DishItem(3,"치킨"));
        list.add(new DishItem(4,"피자"));
        list.add(new DishItem(5,"비빔냉면"));
        /*
        list.add(new DishItem(6,"샐러드"));
        list.add(new DishItem(7,"국밥"));
        list.add(new DishItem(8,"해장국"));
        list.add(new DishItem(9,"짜장면"));
        list.add(new DishItem(10,"짬뽕"));*/

        return new StreamListAdapter(getActivity(), list);
    }
}
