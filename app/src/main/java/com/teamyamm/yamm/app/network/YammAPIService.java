package com.teamyamm.yamm.app.network;

import com.teamyamm.yamm.app.pojos.BattleItem;
import com.teamyamm.yamm.app.pojos.DishItem;
import com.teamyamm.yamm.app.pojos.Friend;
import com.teamyamm.yamm.app.pojos.GridItem;
import com.teamyamm.yamm.app.pojos.YammPlace;

import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

public interface YammAPIService {


    @GET("/client-info")
    void getClientInfo(Callback<RawClientInfo> callback);

    public static class RawClientInfo{
        public String android_version;
    }

    //User Registration
    @FormUrlEncoded
    @POST("/registration/facebook")
    void facebookRegistration(@Field("name") String name, @Field("fb_token") String token, @Field("phone") String phone, @Field("authcode") String code, Callback<YammToken> callback);

    @FormUrlEncoded
    @POST("/registration/kakao")
    void kakaoRegistration(@Field("name") String name, @Field("kakao_token") String token, @Field("phone") String phone, @Field("authcode") String code, Callback<YammToken> callback);

    @FormUrlEncoded
    @POST("/registration/password")
    void pwRegistration(@Field("name") String name, @Field("password") String password, @Field("phone") String phone, @Field("authcode") String code, Callback<YammToken> callback);

    @Deprecated
    @FormUrlEncoded
    @POST("/registration/user")
    void userRegistration(@Field("name") String name, @Field("email") String email,
                            @Field("password") String password, @Field("phone") String phone, @Field("authcode") String authcode, Callback<String> cb);

    public static class YammToken{
        public int uid;
        public String access_token;
        public String token_type;

        public YammToken(int uid, String access_token, String token_type){
            this.uid = uid;
            this.access_token = access_token;
            this.token_type = token_type;
        }

        public String toString(){ return access_token; }
    }

    public class YammRetrofitException extends RuntimeException{
        public static final String INVALID_TOKEN = "TOK"; //Invalid OAuth Token
        public static final String UNIDENTIFIED = "WTF"; //unidentified
        public static final String NETWORK = "NET"; //network error
        public static final String AUTHENTICATION = "AUT"; //authentication is wrong
        public static final String DUPLICATE_ACCOUNT = "DUP"; // same email or phone
        public static final String INCORRECT_AUTHCODE = "SMS"; //wrong sms authcode
        public static final String PASSWORD_MIN = "PWM"; //password is too short
        public static final String PASSWORD_FORMAT = "PWF"; //password should contain at least one number or special char
        public static final String EMAIL_FORMAT = "EMF"; //email format is wrong
        public static final String PHONE_FORMAT = "PHF"; //phone format is wrong
        public static final String NO_OTHER_AUTHENTICATION = "NOA"; //no other authentication method (email, facebook, kakao)


        public YammRetrofitException(Throwable e, String message){
            super(message, e);
        }
    }


    @FormUrlEncoded
    @POST("/registration/generate-phone-auth-code")
    void phoneVerification(@Field("phone") String phone, Callback<VeriExp> cb);

    public static class VeriExp{
        long expires;

        public VeriExp(long expires){
            this.expires = expires;
        }

        public String toString(){
            return expires+"";
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

    /*
    * FB Login. Should send FB AccessToken
    * */

    @GET("/auth/facebook")
    void fbLogin(@Query("fb_token") String token, Callback<RawOAuthToken> callback);

    @GET("/auth/kakao")
    void kakaoLogin(@Query("kakao_token") String token, Callback<RawOAuthToken> callback);

    public static class RawOAuthToken {
        public String access_token;
        public String token_type;
        public String uid;
    }

    /*
    * Gets GridItems from Server in GridActivity
    * */
    @Deprecated
    @GET("/cannot-eat-choices")
    void getGridItems(Callback<Choices> cb);

    public static class Choices{
        public List<GridItem> choices;

        public Choices(List<GridItem> list){
            choices = list;
        }
        public List<GridItem> getList() { return choices; }
    }

    /*
    * POST GridItems to Server in GridActivity
    * */

    @FormUrlEncoded
    @POST("/preferences/cannot-eat-choices")
    void postGridItems(@Field("choices") String items, Callback<String> cb);
    //need to fix field

    /*
    * GET BattleItem Result & Get Next Battle Item
    * To start battle, send ""
    * */

    @Deprecated
    @GET("/battle/next-round")
    void getBattleItem(@Query("results") String result, Callback<RawBattleItem> callback);

    @GET("/battle/entries")
    void getBattleItems(Callback<RawBattleItem> callback);

    @POST("/battle/results")
    void postBattleItem(@Body RawBattleItemList results, Callback<String> callback);

    public static class RawBattleItem{
        private int rounds;
        private List<List<DishItem>> dishes;

        public RawBattleItem(int rounds, List<List<DishItem>> dishes){
            this.rounds = rounds;
            this.dishes = dishes;
        }

        public List<List<DishItem>> getDishes(){
            return dishes;
        }

        public BattleItem getBattleItem(int i){
            return new BattleItem(dishes.get(i).get(0), dishes.get(i).get(1));
        }

        public int getRounds(){
            return rounds;
        }
    }

    public static class RawBattleItemList{
        private List<RawBattleItemForPost> results;

        public RawBattleItemList(List<RawBattleItemForPost> results){
            this.results = results;
        }
    }

    //For Post
    public static class RawBattleItemForPost{
        private int first_dish_id;
        private int second_dish_id;
        private int result_dish_id;

        public RawBattleItemForPost(BattleItem item){
            first_dish_id = item.getFirst().getId();
            second_dish_id = item.getSecond().getId();
            result_dish_id = item.getResult();
        }
    }




    /*
    * POST phone numbers and get yamm_friends
    * */
    @POST("/friends/from-phone")
    void findFriendsFromPhone(@Body RawPhones phones, Callback<RawFriends> callback);

    public static class RawPhones{
        private Set<String> phones;

        public RawPhones(Set<String> phones){
            this.phones = phones;
        }
    }
    public static class RawFriends{
        private List<Friend> yamm_users;

        public RawFriends(List<Friend> yamm_users){
            this.yamm_users = yamm_users;
        }

        public List<Friend> getFriendsList(){
            return yamm_users;
        }
    }

    @Deprecated
    @GET("/preferences/suggestions")
    void getPersonalDishes(Callback<List<DishItem>> cb);

    @POST("/preferences/next-suggestion")
    void postDislikeDish(@Body RawDislike dislike, Callback<DishItem> callback);

    public static class RawDislike{
        int dislikeId;

        public RawDislike(int dislikeId){ this.dislikeId = dislikeId; }
    }

    @POST("/preferences/like")
    void postLikeDish(@Body RawLike like, Callback<String> callback);

    public static class RawLike{
        int dishId;
        String category;
        String detail;

        public RawLike(int dishId, String category, String detail){
            this.dishId = dishId;
            this.category = category;
            if (detail == null)
                this.detail = "";
            else
                this.detail = detail;
        }
    }

    @GET("/dish")
    void getDishes(Callback<List<DishItem>> callback);

    /*
    * Personal Recommendation
    * */

    //suggestionType SHOULD BE "lunch" or "dinner"
    @GET("/suggestion/personal")
    void getSuggestion(@Query("suggestionType") String type, Callback<RawSuggestion> cb);

    public static class RawSuggestion{
        public String title;
        public List<DishItem> dishes;
    }

    @GET("/suggestion/personal-check-new")
    void checkIfNewSuggestion(Callback<RawCheck> cb);

    public static class RawCheck{
        public boolean lunch, dinner, alcohol, today;
    }
    /*
    * Group Recommendation
    * */

    @GET("/group/suggestions")
    void getGroupSuggestions(@Query("mealType") String meal, @Query("userIds") String userIDs, Callback<List<DishItem>> callback);

    @POST("/group/next-suggestion")
    void postDislikeDishGroup(@Body RawDislike dislike, Callback<DishItem> callback);

    @Deprecated
    @FormUrlEncoded
    @POST("/password-recovery/request")
    void requestPasswordRecovery(@Field("email") String email, Callback<String> cb);

    @FormUrlEncoded
    @POST("/password-recovery/request")
    void requestPasswordRecoveryFromPhone(@Field("phone") String phone, Callback<String> cb);

    /*
    * About Push Messages
    * */

    @FormUrlEncoded
    @POST("/push/token")
    void registerPushToken(@Field("pushToken") String items, @Field("deviceId") String id, Callback<String> cb);

    @DELETE("/push/token")
    void unregisterPushToken(@Query("deviceId") String id, Callback<String> cb);

    @POST ("/push/poke")
    void sendPokeMessage(@Body RawPokeMessage message, Callback<String> callback);

    public static class RawPokeMessage{
        private List<Long> uids;
        private int dishId;
        private String date;
        private String meal;
        private boolean response;

        public RawPokeMessage(List<Long> uids, int dishId, String date, String meal){
            this.uids = uids;
            this.dishId = dishId;
            this.date = date;
            this.meal = meal;
        }

        public RawPokeMessage(List<Long> uids, boolean response, int dishId){
            this.response = response;
            this.uids = uids;
            this.dishId = dishId;
        }
    }

    @POST ("/push/poke-response")
    void sendPokeResponse(@Body RawPokeMessage message, Callback<String> callback);


    @GET ("/user/info")
    void getUserInfo(Callback<RawInfo> callback);

    @FormUrlEncoded
    @PUT ("/user/facebook")
    void connectFacebook(@Field("fb_short_lived_token") String token, Callback<String> callback);

    @DELETE("/user/facebook")
    void disconnectFacebook(Callback<String> callback);

    @FormUrlEncoded
    @PUT ("/user/kakao")
    void connectKakao(@Field("kakao_access_token") String token, Callback<String> callback);

    @DELETE("/user/kakao")
    void disconnectKakao(Callback<String> callback);

    @FormUrlEncoded
    @PUT ("/user/password")
    void changePassword(@Field("password") String password, Callback<String> callback);


    @Deprecated
    @FormUrlEncoded
    @PUT("/user/phone")
    void registerPhone(@Field("newPhone") String phone, @Field("authcode") String code, Callback<String> callback);

    public class RawInfo{
        public long uid;
        public String email;
        public String phone;
        public String name;
        public String facebook_uid;
        public String kakao_uid;

        public RawInfo(long uid, String email, String phone, String name, String facebook_uid, String kakao_uid){
                this.uid = uid;
            this.email = email;
            this.phone = phone;
            this.name = name;
            this.facebook_uid =facebook_uid;
            this.kakao_uid = kakao_uid;
        }
    }

    @GET("/map/nearby")
    void getPlacesNearby(@Query("lat") double lat, @Query("lng") double lng,
                               @Query("rad") double rad, @Query("dish_id") int id, Callback<List<YammPlace>> callback);
    /*
    * Error
    * */

    public class YammRetrofitError{
        private String code;
        private String message;

        public YammRetrofitError(){
            code = "";
            message = "";
        }
        public YammRetrofitError(String code, String message){
            this.code = code;
            this.message = message;
        }

        public String getCode(){ return code; }
        public String getMessage(){ return message; }

        public String toString(){ return code + " " + message; }
    }
}
