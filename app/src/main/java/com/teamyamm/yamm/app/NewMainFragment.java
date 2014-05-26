package com.teamyamm.yamm.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by parkjiho on 5/24/14.
 */
public class NewMainFragment extends Fragment {
    private final static int FRIEND_ACTIVITY_REQUEST_CODE = 1001;
    private FrameLayout main_layout;
    private ImageView main_imageview;
    private Button friendPickButton;
    private ArrayList<Integer> selectedFriendList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MainFragment/onCreateView", "onCreateView started");

        main_layout = (FrameLayout) inflater.inflate(R.layout.new_main_fragment, container, false);
        friendPickButton = (Button) main_layout.findViewById(R.id.friends_pick_button);

        setYammImageView();
        setFriendPickButton();

        return main_layout;
    }

    private void setYammImageView(){
        main_imageview = (ImageView) main_layout.findViewById(R.id.main_image_view);
        main_imageview.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.example));
        main_imageview.setAdjustViewBounds(true);
        main_imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private void setFriendPickButton(){
        friendPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendActivity.class);
                v.setEnabled(false); //To prevent double fire

                selectedFriendList = new ArrayList<Integer>();
                selectedFriendList.add(1);
                selectedFriendList.add(2);


                intent.putIntegerArrayListExtra(FriendActivity.FRIEND_LIST, selectedFriendList); //send previously selected friend list
                startActivityForResult(intent, FRIEND_ACTIVITY_REQUEST_CODE);
                Log.i("MainFragment/onClick","FriendActivity called");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FRIEND_ACTIVITY_REQUEST_CODE){
            Log.i("MainFragment/onActivityResult","Got back from FriendActivity; resultcode: " + resultCode);

            friendPickButton.setEnabled(true);

            //Get Friend List

            selectedFriendList = data.getIntegerArrayListExtra(FriendActivity.FRIEND_LIST);

            Toast.makeText(getActivity(),"Got Back from Friend" + selectedFriendList, Toast.LENGTH_LONG).show();
        }
    }

}
