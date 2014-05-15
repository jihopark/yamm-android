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
import android.widget.Toast;

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
        private int visibleThreshold = 1; // how many items before loading new contents
        private boolean loading = true;
        private boolean data = true;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean toastShown = false;

        public void onScrollStateChanged(AbsListView view, int scrollState){ }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
            Log.v("ScrollListener","data "+ data + " firstVisibleItem "+ firstVisibleItem + "/visibleItemCount - " + visibleItemCount + "/totalItemCount - " + totalItemCount);
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (data && !loading && (totalItemCount - visibleItemCount) < (firstVisibleItem + visibleThreshold)) {  //End of Scroll
                // load more items
                data = loadMoreItemsOnAdapter();
                loading = true;
            }
            else if (!toastShown && !data && (totalItemCount - visibleItemCount) <= firstVisibleItem){
                Log.v("ScrollListener","Toast should come");
                Toast.makeText(getActivity().getApplicationContext(),R.string.stream_end_message,Toast.LENGTH_SHORT).show();
                toastShown = true;
            }
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
        */

        return new StreamListAdapter(getActivity(), list);
    }
    /*
    * Load more items on StreamListViewAdapter; returns false if no more to add
    * */
    private boolean loadMoreItemsOnAdapter(){
        Log.v("MainFragment/loadMoreItemsOnAdapter","More Items loaded");
        adapter.addDishItem(new DishItem(6,"샐러드"));
        adapter.addDishItem(new DishItem(7,"국밥"));
        adapter.addDishItem(new DishItem(8,"해장국"));
        adapter.addDishItem(new DishItem(9,"짜장면"));
        adapter.addDishItem(new DishItem(10,"짬뽕"));
        adapter.notifyDataSetChanged();
        return false;
    }
}
