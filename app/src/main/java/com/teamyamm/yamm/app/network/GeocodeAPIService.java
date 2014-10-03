package com.teamyamm.yamm.app.network;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.client.Response;


/**
 * Created by parkjiho on 10/3/14.
 */
public interface GeocodeAPIService {
    public final static String googleGeocodeAPI = "http://maps.google.com/maps/api/geocode";

    @GET("/json")
    void getAddressFromLocation(@Query("address") String address, Callback<Response> callback);
}


