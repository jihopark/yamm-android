package com.teamyamm.yamm.app;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface YammAPIService {


    //User Registration

    @FormUrlEncoded
    @POST("/registration/user")
    void userRegistration(@Field("name") String name, @Field("email") String email,
                            @Field("password") String password, @Field("phone") String phone, @Field("authcode") String authcode, Callback<String> cb);

    public static class YammToken{
        private String access_token;
        private String token_type;

        public YammToken(String access_token, String token_type){
            this.access_token = access_token;
            this.token_type = token_type;
        }

        public String toString(){ return access_token; }
    }

    public class YammJoinException extends Exception{
        public YammJoinException(String message){
            super(message);
        }
    }


    @FormUrlEncoded
    @POST("/registration/generate-phone-auth-code")
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


    /*
    * For User Login - returns Access Token from Server through YammToken
    * Need to Pass Grant Type as default JSON. Form-url-encoded DOES NOT work
    * MUST set RequestInterceptor for Basic AUTH through HEADER Manipulation
    * */

     @POST("/token")
    void userLogin(@Body GrantType type, Callback<YammToken> cb);

    public static class GrantType{
        private String grant_type = "client_credentials";

        public GrantType(){
            grant_type = "client_credentials";
        }
    }




}
