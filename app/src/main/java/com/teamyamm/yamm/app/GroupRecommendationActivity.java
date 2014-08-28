package com.teamyamm.yamm.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 7/23/14.
 */
public class GroupRecommendationActivity extends BaseActivity implements MainFragmentInterface {

    ArrayList<Friend> selectedFriend;
    List<DishItem> dishItems;
    String selectedTime;
    MainFragment mainFragment;
    private Dialog fullScreenDialog;
    private boolean isDialogOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_recommendation);

        setActionBarBackButton(true);
        loadBundle();
        setSelectedItems();
        loadDishes();
        trackReceivedGroupRecommendation();
    }

    @Override
    public void onBackPressed() {
        showFinishDialog();
    }

    public Dialog getFullScreenDialog(){
        return fullScreenDialog;
    }

    public boolean isFullScreenDialogOpen(){
        return isDialogOpen;
    }

    public void setFullScreenDialogOpen(boolean b){
        isDialogOpen = b;
    }

    public void closeFullScreenDialog(){
        if (fullScreenDialog!= null) {
            fullScreenDialog.dismiss();
            isDialogOpen = false;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                showFinishDialog();
                break;
        }

        return true;
    }

    private void setSelectedItems(){
        TextView selectedItemsText = (TextView) findViewById(R.id.selected_items_textview);
        String s = "";
        int count = 0;

        for (Friend f : selectedFriend) {
            if (count++ != 0) {
                Spannable newSpan = new SpannableString(" ");
                newSpan.setSpan(new BackgroundColorSpan(Color.TRANSPARENT),
                        0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                selectedItemsText.append(newSpan);
            }

            Spannable newSpan = new SpannableString(f.getName());
            newSpan.setSpan(new NameSpan(getResources(), getResources().getDimension(R.dimen.selected_item_x_padding)
                            , getResources().getDimension(R.dimen.selected_item_y_padding), getResources().getDimension(R.dimen.selected_item_round),
                            getResources().getDimension(R.dimen.selected_item_line_spacing_plus_padding_for_group), getResources().getDimension(R.dimen.selected_item_height)),
                    0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            selectedItemsText.append(newSpan);
        }
    }

    private void setFragment(){
        if (dishItems == null)
            return ;

        Type type = new TypeToken<List<DishItem>>(){}.getType();


        Bundle bundle = new Bundle();
        bundle.putString("dishes", new Gson().toJson(dishItems, type));
        bundle.putBoolean("isGroup", true);

        mainFragment = new MainFragment();
        mainFragment.setArguments(bundle);

        FragmentTransaction tact = getSupportFragmentManager().beginTransaction();
        tact.add(R.id.main_fragment_container, mainFragment, MainFragment.MAIN_FRAGMENT);
        tact.commit();
    }

    private void loadDishes(){
        YammAPIService service = YammAPIAdapter.getTokenService();
        String userIds = "";
        for (Friend f : selectedFriend)
            userIds = userIds + f.getID() + ",";
        userIds = userIds.substring(0, userIds.length() - 1);
        Log.i("GroupRecommendationActivity/loadDishes", userIds);
        fullScreenDialog = createFullScreenDialog(GroupRecommendationActivity.this, getString(R.string.dialog_group_recommendation));
        isDialogOpen = true;

        fullScreenDialog.show();

        service.getGroupSuggestions(userIds, new Callback<List<DishItem>>() {
            @Override
            public void success(List<DishItem> dishes, Response response) {
                Log.i("GroupRecommendationActivity/getGroupSuggestions", "Group Recommendation Success " + dishItems);
                dishItems = dishes;
                setFragment();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("GroupRecommendationActivity/getGroupSuggestions", "Something went wrong");
                finishActivityForError();
                fullScreenDialog.dismiss();
            }
        });
    }

    private void loadBundle(){
        String jsonMyObject = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            jsonMyObject = bundle.getString("friendlist");
        }

        Type type = new TypeToken<ArrayList<Friend>>(){}.getType();


        selectedFriend = new Gson().fromJson(jsonMyObject, type);
        selectedTime = bundle.getString("time");

        Log.i("GroupRecommendationActivity/loadBundle", "Selected Friends : " + selectedFriend);
        Log.i("GroupRecommendationActivity/loadBundle","Selected Time : " + selectedTime);

    }

    public void changeInDishItem(List<DishItem> list){
        dishItems = list;
        Log.i("MainActivity/changeInDishItem", "Dish Item changed to " + dishItems);
    }

    private void finishActivityForError(){
        Toast.makeText(GroupRecommendationActivity.this, R.string.unidentified_error_message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showFinishDialog(){
        AlertDialog dialog = createDialog(GroupRecommendationActivity.this, R.string.group_finish_dialog_title,
                R.string.group_finish_dialog_message,R.string.group_finish_dialog_positive, R.string.group_finish_dialog_negative,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                },
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        dialog.show();
    }

    private void trackReceivedGroupRecommendation(){
        JSONObject props = new JSONObject();
        try{
            props.put("count", selectedFriend.size());
            props.put("time", selectedTime);

        }catch(JSONException e){
            Log.e("GroupRecommendationActivity/trackReceivedGroupRecommendation","JSON Error");
        }
        mixpanel.track("Received Group Recommendation", props);
        Log.i("GroupRecommendationActivity/trackReceivedGroupRecommendation","Received Group Recommendation Tracked");
    }
}
