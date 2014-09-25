package com.teamyamm.yamm.app.network;

import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by parkjiho on 9/25/14.
 */
public class MixpanelController {
    public static MixpanelAPI mixpanel = null;

    public static void setMixpanel(MixpanelAPI m){
        mixpanel = m;
    }

    public static boolean checkMixpanelAPI(){
        if (mixpanel==null)
            Log.e("MixpanelController/checkMixpanelAPI","Set MixpanelAPI First.");
        return !(mixpanel == null);
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


    public static void trackJoiningMixpanel(){
        if (!checkMixpanelAPI())
            return ;

        JSONObject props = new JSONObject();
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


}
