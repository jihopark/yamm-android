package com.teamyamm.yamm.app;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface YammAPIService {


    //User Registration

    @FormUrlEncoded
    @POST("/registration/user")
    void userRegistration(@Field("name") String name, @Field("email") String email,
                            @Field("password") String password, @Field("phone") String phone, Callback<String> cb);
}