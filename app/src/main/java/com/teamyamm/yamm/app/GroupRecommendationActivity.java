package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
    public void onStop(){
        closeFullScreenDialog();
        super.onStop();
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

    public boolean isLoading(){return false; }

    public boolean shouldTutorialOpen(){ return false; }

    public void showTutorial(){ return ; }

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

    public void sendPokeMessage(DishItem dish){
        final DishItem fDish = dish;
        final List<Long> sendIds = new ArrayList<Long>();
        String time = selectedTime;
        final String meal = time.substring(time.length() - 2, time.length());
        final String date = time = time.substring(0, time.length() - 3);

        for (YammItem i : selectedFriend)
            sendIds.add(i.getID());


        View.OnClickListener positiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            YammAPIService.RawPokeMessage msg = new YammAPIService.RawPokeMessage(sendIds, fDish.getId(), date, meal);

                makeYammToast("친구들한테 " + selectedTime + "에 "
                        + fDish.getName() + " 먹자고 했어요!", Toast.LENGTH_LONG);
                YammAPIAdapter.getTokenService().sendPokeMessage(msg, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Log.i("GroupRecommendationActivity/sendPushMessage", "Push " + s);
                        trackGroupPokeFriendMixpanel(selectedFriend.size(), selectedTime, fDish.getName());
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        String msg = retrofitError.getCause().getMessage();
                        if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION)) {
                            Log.e("GroupRecommendationActivity/sendPushMessage", "Invalid Token, Logging out");
                            invalidToken();
                            return ;
                        }
                        Log.e("GroupRecommendationActivity/sendPushMessage", "Error In Push Message");
                        makeYammToast(R.string.unidentified_error_message, Toast.LENGTH_SHORT);
                    }
                });
                dismissCurrentDialog();
            }
        };

        Dialog askPoke = createDialog(GroupRecommendationActivity.this,
                R.string.dialog_group_poke_title, R.string.dialog_group_poke_message,
                R.string.dialog_positive, R.string.dialog_negative, positiveListener, null);
        askPoke.show();
    }

    private void setSelectedItems(){
        TextView selectedItemsText = (TextView) findViewById(R.id.selected_items_textview);
        String s = "";
        int count = 0;

        if (selectedFriend.size() == 1)
            selectedItemsText.setText(selectedFriend.get(0).getName() + "님과의 추천입니다" );
        else
            selectedItemsText.setText(selectedFriend.get(0).getName()
                    + "님 외 " +(selectedFriend.size() - 1) + "명과의 추천입니다" );

       /* for (Friend f : selectedFriend) {
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
        }*/
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

        try {
            FragmentTransaction tact = getSupportFragmentManager().beginTransaction();
            tact.add(R.id.main_fragment_container, mainFragment, MainFragment.MAIN_FRAGMENT);
            tact.commitAllowingStateLoss();
        }catch (IllegalStateException e){
            Log.e("GroupRecommendation/setFragment","Activity Destroyed");
        }
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
                fullScreenDialog.dismiss();
                String msg = retrofitError.getCause().getMessage();
                if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION)) {
                    Log.e("PokeActivity/pokeWithYamm", "Invalid Token, Logging out");
                    invalidToken();
                    return ;
                }
                Log.e("GroupRecommendationActivity/getGroupSuggestions", "Something went wrong");
                finishActivityForError();
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
        makeYammToast(R.string.unidentified_error_message, Toast.LENGTH_SHORT);
        finish();
    }

    private void showFinishDialog(){
        Dialog dialog = createDialog(GroupRecommendationActivity.this, R.string.group_finish_dialog_title,
                R.string.group_finish_dialog_message,R.string.group_finish_dialog_positive, R.string.group_finish_dialog_negative,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        dismissCurrentDialog();
                    }
                },
            null);
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

    private void trackGroupPokeFriendMixpanel(int count, String time, String dish){
        JSONObject props = new JSONObject();
        try{
            props.put("Method", "YAMM");
            props.put("Count", count);
            props.put("Time", time);
            props.put("Dish", dish);
        }catch(JSONException e){
            Log.e("GroupRecommendationActivity/trackGroupPokeFriend","JSON Error");
        }
        mixpanel.track("Group Poke Friend", props);
        Log.i("GroupRecommendationActivity/trackGroupPokeFriend","Group Poke Friend Tracked");
    }
}
