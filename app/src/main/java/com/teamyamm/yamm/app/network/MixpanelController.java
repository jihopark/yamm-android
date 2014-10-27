package com.teamyamm.yamm.app.network;

import android.content.Context;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.teamyamm.yamm.app.BaseActivity;
import com.teamyamm.yamm.app.pojos.DishItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

/**
 * Created by parkjiho on 9/25/14.
 */
public class MixpanelController {

    public static final String MIXPANEL_RECOMMENDATIONS_TOKEN = "9d35ac37a71aac289e80f8a906771327";
    public static final String MIXPANEL_TOKEN_PRODUCTION = "5bebb04a41c88c1fad928b5526990d03";
    public static final String MIXPANEL_TOKEN_DEVELOPMENT= "4a63eee3969860701f1e1c8189c127e0";


    public static MixpanelAPI mixpanel = null;
    public static MixpanelAPI mixpanelRecommendation = null;

    public static void setMixpanel(MixpanelAPI m){
        mixpanel = m;
    }
    public static void setMixpanelRecommendation(MixpanelAPI m){
        mixpanelRecommendation = m;
    }

    public static void flushAll(){
        if (mixpanel!=null)
            mixpanel.flush();
        if (mixpanelRecommendation!=null)
            mixpanelRecommendation.flush();
    }

    public static boolean checkMixpanelAPI(){
        if (mixpanel==null)
            Log.e("MixpanelController/checkMixpanelAPI","Set MixpanelAPI First.");
        return !(mixpanel == null);
    }

    public static void logOut(){
        String uuid = UUID.randomUUID().toString();

        Log.d("MixpanelController/logOut","Mixpanel distinct id was " + mixpanel.getDistinctId());
        if (mixpanel!=null){
            mixpanel.clearSuperProperties();
            mixpanel.identify(uuid);
            Log.d("MixpanelController/logOut","Reidentified with "  + uuid);
        }
        if (mixpanelRecommendation!=null){
            mixpanelRecommendation.clearSuperProperties();
            mixpanelRecommendation.identify(uuid);
         }
        Log.d("MixpanelController/logOut","Mixpanel distinct id is " + mixpanel.getDistinctId());
    }

    public static void setMixpanelAlias(String email){
        if (!checkMixpanelAPI())
            return ;

        mixpanel.alias(email, null);
        Log.i("MixpanelController/setMixpanelAlias", "Mixpanel - Setting Unique ID with email " + email);

        mixpanel.getPeople().identify(mixpanel.getDistinctId());
        mixpanel.getPeople().set("$email",email);
        Log.i("MixpanelController/setMixpanelAlias","Mixpanel - Setting Name for Account"+ email);
    }

    public static void setMixpanelIdentity(String email){
        if (!checkMixpanelAPI())
            return ;

        mixpanel.identify(email);
        Log.i("MixpanelController/setMixpanelIdentity","Setting Unique ID with email "+ email);
    }


    public static void trackJoiningMixpanel(String type){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        try {
            props.put("AUTH_TYPE", type);
        }catch(JSONException e){
            Log.e("MixpanelController/trackJoiningMixpanel","JSON Error");
        }
        mixpanel.track("Joining", props);
        Log.i("MixpanelController/trackJoiningMixpanel","Joining Tracked");
    }

    public static void trackLoginErrorMixpanel(String errorMessage, int count){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        try {
            props.put("count", count);
            props.put("Message", errorMessage);
        }catch(JSONException e){
            Log.e("MixpanelController/trackLoginErrorMixpanel","JSON Error");
        }
        mixpanel.track("Login Error", props);
        Log.i("MixpanelController/trackJoiningMixpanel","Login Error Tracked");
    }

    public static void trackBattleMixpanel(int battleCount){
        if (!checkMixpanelAPI())
            return ;


        JSONObject props = new JSONObject();
        try{
            props.put("Battle Count", battleCount);
        }catch(JSONException e){
            Log.e("MixpanelController/trackBattleMixpanel","Error in JSON");
            props = new JSONObject();
        }
        mixpanel.track("Battle", props);
    }

    public static void trackSelectingDislkeFoodMixpanel(int count){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        try{
            props.put("count", count);

        }catch(JSONException e){
            Log.e("MixpanelController/trackSelectingDislikeFoodMixpanel","JSON Error");
        }
        mixpanel.track("Selecting Dislike Food", props);
        Log.i("MixpanelController/trackSelectingDislkeFoodMixpanell","Selecting Dislike Food Tracked ");
    }

    public static void trackReceivedGroupRecommendation(int count, String meal){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        try{
            props.put("count", count);
            props.put("meal", meal);

        }catch(JSONException e){
            Log.e("MixpanelController/trackReceivedGroupRecommendation","JSON Error");
        }
        mixpanel.track("Received Group Recommendation", props);
        Log.i("MixpanelController/trackReceivedGroupRecommendation","Received Group Recommendation Tracked");
    }

    public static void trackGroupPokeFriendMixpanel(int count, String time, String dish){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        try{
            props.put("Method", "YAMM");
            props.put("Count", count);
            props.put("meal", time);
            props.put("Dish", dish);
        }catch(JSONException e){
            Log.e("MixpanelController/trackGroupPokeFriend","JSON Error");
        }
        mixpanel.track("Group Poke Friend", props);
        Log.i("MixpanelController/trackGroupPokeFriend","Group Poke Friend Tracked");
    }

    public static void trackSendInviteMixpanel(String method, int count){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();

        try{
            props.put("method", method);
            props.put("count", count);
        }catch(JSONException e){
            Log.e("MixpanelController/trackSendInviteMixpanel","JSON Error");
        }

        mixpanel.track("Send Invite", props);
        Log.i("MixpanelController/trackSendInviteMixpanel","Send Invite Tracked " + method);
    }

    public static void trackEnteredInviteMixpanel(){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        mixpanel.track("Entered Invite", props);
        Log.i("MixpanelController/trackEnteredInviteMixpanel","Entered Invite Tracked ");
    }

    public static void trackPokeFriendMixpanel(String method, int count, String time, String dish){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        try {
            props.put("Method", method);
            props.put("Count", count);
            props.put("Time", time);
            props.put("Dish", dish);
        }catch(JSONException e){
            Log.e("MixpanelController/trackPokeFriendMixpanel","JSON Error");
        }

        mixpanel.track("Poke Friend", props);
        Log.i("MixpanelController/trackPokeFriendMixpanel","Poke Friend Tracked " + method + count + time);
    }

    public static void trackEnteredPokeFriendMixpanel(){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        mixpanel.track("Entered Poke Friend", props);
        Log.i("MixpanelController/trackEnteredPokeFriendMixpanel","Entered Poke Friend Tracked ");
    }

    public static void trackPokeResponseMixpanel(boolean response, Context context){
        if (!checkMixpanelAPI()){
            if (BaseActivity.CURRENT_APPLICATION_STATUS.equals(BaseActivity.TESTING))
                setMixpanel(MixpanelAPI.getInstance(context, MIXPANEL_TOKEN_DEVELOPMENT));
            else
                setMixpanel(MixpanelAPI.getInstance(context, MIXPANEL_TOKEN_PRODUCTION));

            if (!checkMixpanelAPI())
                return ;
        }

        JSONObject props = new JSONObject();
        try {
            if (response)
                props.put("Response", "OK");
            else
                props.put("Response", "NO");
        }catch(JSONException e){
            Log.e("MixpanelController/trackPokeResponseMixpanel","JSON Error");
        }
        mixpanel.track("Poke Response", props);
        Log.i("MixpanelController/trackPokeResponseMixpanel","Poke Response Tracked " + response);
    }

    public static void trackEnteredPhoneAuthMixpanel(){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        mixpanel.track("Entered Phone Auth", props);
        Log.i("MixpanelController/trackEnteredPhoneAuthMixpanel","Entered Phone Auth Tracked ");
    }
    public static void trackSubmitPhoneAuthMixpanel(){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        mixpanel.track("Submit Phone Auth", props);
        Log.i("MixpanelController/trackSubmitPhoneAuthMixpanel","Submit Phone Auth Tracked ");
    }

    public static void trackEndOfRecommendationMixpanel(){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        mixpanel.track("End Of Recommendation", props);
        Log.i("MixpanelController/trackEndOfRecommendationMixpanel","End of Recommendation Tracked");
    }

    public static void trackSearchMapMixpanel(String place){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        try {
            props.put("Place", place);
        }catch(JSONException e){
            Log.e("MixpanelController/trackSearchMapMixpanel","JSON Error");
        }

        mixpanel.track("Search Map", props);
        Log.i("MixpanelController/trackSearchMapMixpanel","Search Map Tracked " + place);
    }

    public static void trackChangeMapLocationMixpanel(String place){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        try {
            props.put("Place", place);
        }catch(JSONException e){
            Log.e("MixpanelController/trackChangeMapLocationMixpanel","JSON Error");
        }

        mixpanel.track("Change Search Map Location", props);
        Log.i("MixpanelController/trackChangeMapLocationMixpanel","Change Search Map Location Tracked " + place);

    }

    public static void trackDislikeMixpanel(DishItem item) {
        if (!checkMixpanelAPI())
            return;

        JSONObject props = new JSONObject();
        try {
            props.put("Dish", item.getName());
        } catch (JSONException e) {
            Log.e("MixpanelController/trackDislikeMixpanel", "JSON Error");
        }
        mixpanel.track("Dislike", props);
        Log.i("MixpanelController/trackDislikeMixpanel", "Dislike Tracked");
    }


    public static void trackClickedDislikeMixpanel(DishItem item){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        try{
            props.put("Dish", item.getName());
        }catch (JSONException e){
            Log.e("MixpanelController/trackClickedDislikeMixpanel","JSON Error");
        }
        mixpanel.track("Clicked Dislike", props);
        Log.i("MixpanelController/trackClicked DislikeMixpanel","Clicked Dislike Tracked");
    }

    @Deprecated
    public static void trackNewRecommendationMixpanel(){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        mixpanel.track("New Recommendation", props);
        Log.i("MixpanelController/trackNewRecommendationMixpanel","New Recommendation Tracked ");

    }

    public static void trackTodayLunchMixpanel(){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        mixpanel.track("Today Lunch", props);
        Log.i("MixpanelController/trackTodayLunchMixpanel","Today Lunch Tracked ");
    }

    public static void trackTodayDinnerMixpanel(){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        mixpanel.track("Today Dinner", props);
        Log.i("MixpanelController/trackTodayDinnerMixpanel","Today Dinner Tracked ");
    }

    public static void trackTodayAlcoholMixpanel(){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        mixpanel.track("Today Alcohol", props);
        Log.i("MixpanelController/trackTodayAlcoholMixpanel","Today Alcohol Tracked ");
    }

    public static void trackTodayYammMixpanel(){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        mixpanel.track("Today Yamm", props);
        Log.i("MixpanelController/trackTodayYammMixpanel","Today Yamm Tracked ");
    }

    public static void trackEnteredGroupRecommendationMixpanel(){
        JSONObject props = new JSONObject();
        mixpanel.track("Entered Group Recommendation", props);
        Log.i("MixpanelController/trackEnteredGroupRecommendationMixpanel","Entered Group Recommendation Tracked ");
    }

    /*
    *
    *  Mixpanel Recommendation Tracking Project
    * */


    public static void trackRecommendationsMixpanel(DishItem i, String type){
        if (mixpanelRecommendation==null || i==null)
            return ;
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
        try{
            props.put("Dish", i.getName());
            props.put("Id", i.getId());
            props.put("RecommendationType", type);
        }catch (JSONException e){
            Log.e("MixpanelController/trackRecommendationsMixpanel","JSON Error");
        }
        mixpanelRecommendation.track("Recommendation", props);
        Log.i("MixpanelController/trackRecommendationsMixpanel","Recommendation Tracked for " +i.getName());
    }

    public static final String GROUP = "GROUP";
    public static final String PERSONAL = "PERSONAL";

    public static void trackRecommendationsMixpanel(List<DishItem> items, String type){
        for (DishItem i : items)
            trackRecommendationsMixpanel(i, type);
    }

    public static void trackEnteredSearchDish() {
        if (!checkMixpanelAPI())
            return;

        JSONObject props = new JSONObject();

        mixpanel.track("Entered Search Dish", props);
        Log.i("MixpanelController/trackEnteredSearchDishMixpanel", "Entered Search Dish Tracked");
    }

    public static void trackSearchDishMixpanel(DishItem item) {
        if (!checkMixpanelAPI())
            return;

        JSONObject props = new JSONObject();
        try {
            props.put("Dish", item.getName());
        } catch (JSONException e) {
            Log.e("MixpanelController/trackSearchDishMixpanel", "JSON Error");
        }
        mixpanel.track("Search Dish", props);
        Log.i("MixpanelController/trackSearchDishMixpanel", "Search Dish Tracked");
    }

    public static void trackEnteredPlaceMixpanel() {
        if (!checkMixpanelAPI())
            return;

        JSONObject props = new JSONObject();

        mixpanel.track("Entered Place", props);
        Log.i("MixpanelController/trackEnteredPlaceMixpanel", "Entered Place Tracked");
    }

    public static void trackCannotFindPlaceMixpanel(String dishName){
        if (!checkMixpanelAPI())
            return;

        JSONObject props = new JSONObject();
        try {
            props.put("Dish", dishName);
        } catch (JSONException e) {
            Log.e("MixpanelController/trackCannotFindPlaceMixpanel", "JSON Error");
        }
        mixpanel.track("Cannot Find Place", props);
        Log.i("MixpanelController/trackCannotFindPlaceMixpanel", "Cannot Find Place");
    }

}
