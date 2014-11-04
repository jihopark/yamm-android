package com.teamyamm.yamm.app;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.teamyamm.yamm.app.pojos.DishItem;
import com.teamyamm.yamm.app.widget.YammImageView;

import java.lang.reflect.Field;

/**
 * Created by parkjiho on 7/17/14.
 */
public class DishFragment extends Fragment {
    private final static long LOCATION_MIN_TIME = 200; //0.1sec
    private final static float LOCATION_MIN_DISTANCE = 1.0f; //1 meters


    public final static String TOO_MANY_DISLIKE = "dis";
    public final static String SHARE = "SHARE";
    public final static String SEARCH_MAP = "SEARCHMAP";

    private RelativeLayout main_layout;
    private DishItem item;
    private int index;
    private ImageButton searchMap, pokeFriend, dislike, nextRight, nextLeft;
    private boolean isGroup;
    private Activity activity;
    private MainFragment parentFragment;
    private ImageView mainBar;
    private TextView nameText, commentText;
    private YammImageView image;

    //private AutoCompleteTextView placePickEditText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        main_layout = (RelativeLayout) inflater.inflate(R.layout.fragment_dish, container, false);
        Log.d("DishFragment/onCreateView","onCreateView Started");

        isGroup = this.getArguments().getBoolean("isGroup");
        index = this.getArguments().getInt("index");

        mainBar = (ImageView) main_layout.findViewById(R.id.main_image_bar);
        nameText = (TextView) main_layout.findViewById(R.id.dish_name_text);
        commentText = (TextView) main_layout.findViewById(R.id.dish_comment_text);

        loadDish();
        Log.d("DishFragment/onCreateView","DishFragment Created for " +item.getName());
        return main_layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("DishFragment/onActivityCreated","onActivityCreated");
        if (getParentFragment() instanceof MainFragment){
            parentFragment = (MainFragment) getParentFragment();
            if (parentFragment.isPerforming){
                mainBar.setVisibility(View.INVISIBLE);
                nameText.setVisibility(View.INVISIBLE);
                commentText.setVisibility(View.INVISIBLE);
            }

        }
        else{
            Log.d("DishFragment/onActivityCreated", "Parent Fragment of DishFragment should be instanceof MainFragment!");
            return ;
        }
    }

    public DishItem getDishItem(){
        return item;
    }
    public ImageView getMainBar(){ return mainBar; }
    public TextView getNameText(){ return nameText; }
    public TextView getCommentText(){ return commentText; }

    public void setParentFragment(MainFragment f){

        Log.d("DishFragment/setParentFragment","Set ParentFragment");
        parentFragment = f;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (parentFragment!=null) {
            Log.d("DishFragment/onDetatch","Detatch DishFragment " + index);
            parentFragment.detachDishFragment(index);
        }

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        if (getParentFragment() == null){
            Log.e("DishFragment/getParentFragment", "DishFragment Removed, because ParentFragment is null");
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    private void loadDish(){
        String s = this.getArguments().getString("dish");
        item = new Gson().fromJson(s, DishItem.class);

        TextView name = (TextView) main_layout.findViewById(R.id.dish_name_text);
        name.setText(item.getName());

        TextView comment = (TextView) main_layout.findViewById(R.id.dish_comment_text);
        comment.setText(item.getComment());

        image = (YammImageView) main_layout.findViewById(R.id.dish_image);
        image.setID(item.getId());
        if (isGroup)
            image.setPath(YammImageView.GROUP);
        else
            image.setPath(YammImageView.MAIN);
    }

    public void showTexts(){
        mainBar.setVisibility(View.VISIBLE);
        nameText.setVisibility(View.VISIBLE);
        commentText.setVisibility(View.VISIBLE);
    }
}