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
                            @Field("password") String password, @Field("phone") String phone, @Field("authcode") String authcode, Callback<String> cb);

    @FormUrlEncoded
    @POST("/registration/phone/generate-auth-code")
    void phoneVerification(@Field("phone") String phone, Callback<VeriExp> cb);

    public static class VeriExp{
        String expires;

        public VeriExp(String expires){
            this.expires = expires;
        }

        public String toString(){
            return expires;
        }
    }

    public class YammJoinException extends Exception{
        public YammJoinException(String message){
            super(message);
        }
    }
}
