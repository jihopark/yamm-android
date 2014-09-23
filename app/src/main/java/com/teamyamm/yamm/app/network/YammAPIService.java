package com.teamyamm.yamm.app.network;

import com.teamyamm.yamm.app.pojos.BattleItem;
import com.teamyamm.yamm.app.pojos.DishItem;
import com.teamyamm.yamm.app.pojos.Friend;
import com.teamyamm.yamm.app.pojos.GridItem;

import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

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

    public class YammRetrofitException extends RuntimeException{
        public static final String UNIDENTIFIED = "WTF"; //unidentified
        public static final String NETWORK = "NET"; //network error
        public static final String AUTHENTICATION = "AUT"; //authentication is wrong
        public static final String DUPLICATE_ACCOUNT = "DUP"; // same email or phone
        public static final String INCORRECT_AUTHCODE = "SMS"; //wrong sms authcode
        public static final String PASSWORD_MIN = "PWM"; //password is too short
        public static final String PASSWORD_FORMAT = "PWF"; //password should contain at least one number or special char
        public static final String EMAIL_FORMAT = "EMF"; //email format is wrong
        public static final String PHONE_FORMAT = "PHF"; //phone format is wrong


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
    * Gets GridItems from Server in GridActivity
    * */

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

    @GET("/group/suggestions")
    void getGroupSuggestions(@Query("mealType") String meal, @Query("userIds") String userIDs, Callback<List<DishItem>> callback);

    @POST("/group/next-suggestion")
    void postDislikeDishGroup(@Body RawDislike dislike, Callback<DishItem> callback);

    @FormUrlEncoded
    @POST("/password-recovery/request")
    void requestPasswordRecovery(@Field("email") String email, Callback<String> cb);

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

    public class RawInfo{
        public long uid;
        public String email;
        public String phone;
        public String name;

        public RawInfo(long uid, String email, String phone, String name){
            this.uid = uid;
            this.email = email;
            this.phone = phone;
            this.name = name;
        }
    }

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
