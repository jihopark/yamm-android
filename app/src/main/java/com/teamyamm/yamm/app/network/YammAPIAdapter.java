package com.teamyamm.yamm.app.network;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.logentries.android.AndroidLogger;
import com.teamyamm.yamm.app.BaseActivity;
import com.teamyamm.yamm.app.DishFragment;
import com.teamyamm.yamm.app.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 8/3/14.
 */
public class YammAPIAdapter {
    public static String apiURL = "";
    public static final String testURL = "https://yammapitest-22293.onmodulus.net/";
    public static final String prodURL = "https://api.yamm.me";
    public static final String stagURL = "https://yammapistaging-22297.onmodulus.net";


    private static YammAPIService service = null;
    public static YammAPIService tokenService = null;
    private static YammAPIService dislikeService = null;
    private static YammAPIService joinService = null;
    private static YammAPIService loginService = null;
    private static YammAPIService oAuthLoginService = null;
    private static YammAPIService fbConnectService = null;

    private static String token = null;
    private static Context context = null;
    private static AndroidLogger logger = null;

    private static void checkAPIURL(){
        Log.i("YammAPIAdapter/checkAPIURL","Checking URL");
        if (BaseActivity.CURRENT_APPLICATION_STATUS.equals(BaseActivity.TESTING))
            apiURL = testURL;
        else if (BaseActivity.CURRENT_APPLICATION_STATUS.equals(BaseActivity.STAGING))
            apiURL = stagURL;
        else
            apiURL = prodURL;
    }

    public static void setContext(Context cxt){
        context = cxt;
    }

    /*
    * Plain service without any interceptor or error handler
    * */
    public static YammAPIService getService(){
        if (service == null){
            checkAPIURL();
            Log.i("YammAPIAdapter/getService", "Service initiated");

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(apiURL)
                    .setLog(setRestAdapterLog())
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            service = restAdapter.create(YammAPIService.class);
        }

        return service;
    }

    public static boolean isTokenEmpty(){
        return token == null;
    }

    public static void setToken(String s){
        token = s;
        Log.i("YammAPIAdapter/setToken","Token set " + token);
    }

    /*
    * Service that only needs token
    * */
    public static YammAPIService getTokenService(){
        if (tokenService == null){
            checkAPIURL();
            if (token==null){
                Log.e("YammAPIAdapter/getTokenService","Token should be set first!!");
                return null;
            }

            Log.i("YammAPIAdapter/getTokenService", "tokenService initiated with " + token);

            RequestInterceptor interceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", "Bearer " + token);
                }
            };

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(apiURL)
                    .setLog(setRestAdapterLog())
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setErrorHandler(new TokenErrorHandler())
                    .setRequestInterceptor(interceptor)
                    .build();

            tokenService = restAdapter.create(YammAPIService.class);
        }
        return tokenService;
    }

    /*
    * Service for Dislike API
    * */
    public static YammAPIService getDislikeService(){
        if (dislikeService == null){
            checkAPIURL();
            Log.i("YammAPIAdapter/getDislikeService", "dislikeService initiated with " + token);

            RequestInterceptor interceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", "Bearer " + token);
                }
            };

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(apiURL)
                    .setLog(setRestAdapterLog())
                    .setErrorHandler(new DislikeErrorHandler())
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(interceptor)
                    .build();

            dislikeService = restAdapter.create(YammAPIService.class);
        }

        return dislikeService;
    }

    /*
    * Service For Join API
    * */
    public static YammAPIService getJoinService(){
        if (joinService == null){
            checkAPIURL();
            Log.i("YammAPIAdapter/getJoinService", "JoinService initiated");

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(apiURL)
                    .setErrorHandler(new JoinErrorHandler())
                    .build();

            joinService = restAdapter.create(YammAPIService.class);
        }

        return joinService;
    }

    /*
    * Service For Login API
    * */
    public static YammAPIService getLoginService(String email, String pw){
        final String username = email;
        final String pwd = pw;

        checkAPIURL();

        Log.i("YammAPIAdapter/getLoginService", "LoginService initiated");
        RequestInterceptor interceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                Log.i("RequestInterceptor","username " + username + " pwd " + pwd);
                String cred = username + ":" + pwd;
                Log.i("RequestInterceptor","Basic " + Base64.encodeToString(cred.getBytes(), Base64.NO_WRAP));
                request.addHeader("Authorization", "Basic " + Base64.encodeToString(cred.getBytes(), Base64.NO_WRAP));
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiURL)
                .setRequestInterceptor(interceptor)
                .setErrorHandler(new LoginErrorHandler())
                .build();

        loginService = restAdapter.create(YammAPIService.class);


        return loginService;
    }

    /*
    * Service for FB Login
    * */
    public static YammAPIService getOAuthLoginService(){
        checkAPIURL();

        Log.i("YammAPIAdapter/getOAuthLoginService", "Oauth LoginService initiated");
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiURL)
                .setErrorHandler(new OAuthLoginErrorHandler())
                .setLog(setRestAdapterLog())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        oAuthLoginService = restAdapter.create(YammAPIService.class);

        return oAuthLoginService;
    }

    /*
   * Service for FB Connect/Disconnect
   * */
    public static YammAPIService getFBConnectService(){
        checkAPIURL();

        if (fbConnectService == null) {
            if (token==null){
                Log.e("YammAPIAdapter/getFBConnectService","Token should be set first!!");
                return null;
            }

            Log.i("YammAPIAdapter/getFBConnectService", "FB ConnectService initiated");

            RequestInterceptor interceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", "Bearer " + token);
                }
            };

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(apiURL)
                    .setErrorHandler(new FBConnectErrorHandler())
                    .setLog(setRestAdapterLog())
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(interceptor)
                    .build();

            fbConnectService = restAdapter.create(YammAPIService.class);
        }
        return fbConnectService;
    }


    private static RestAdapter.Log setRestAdapterLog(){
        return new RestAdapter.Log() {
            @Override
            public void log(String s) {
                Log.i("YammAPIServiceLog", s);
                if (context!=null) {
                    if (logger==null) {
                        logger = AndroidLogger.getLogger(context, context.getResources().getString(R.string.logentries_key), false);
                        Log.i("YammAPIAdapter/setRestAdapterLog","Logger is null. Init Logentries Logger");
                    }
                    logger.info(s);
                }
            }
        };
    }

    /*
    Error Handlers
    * */

    public static class FBConnectErrorHandler implements ErrorHandler{
        @Override
        public Throwable handleError(RetrofitError cause) {
            Response r = cause.getResponse();

            if (cause.isNetworkError()){
                Log.e("FBConnectErrorHandler/handleError","Handling Network Error");
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.NETWORK);
            }
            if (r != null && r.getStatus() == 400) {
                Log.e("FBConnectErrorHandler/handleError","Handling 400 Error");
                YammAPIService.YammRetrofitError error = new YammAPIService.YammRetrofitError();
                Gson gson = new Gson();
                try {
                    error = gson.fromJson(responseToString(r), error.getClass());
                }catch(JsonSyntaxException e){
                    Log.e("FBConnectErrorHandler/handleError","Json Syntax Exception Caught");
                    return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
                }catch(IllegalStateException e){
                    Log.e("FBConnectErrorHandler/handleError","Illegal State Exception Caught");
                    return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
                }catch(NullPointerException e){
                    Log.e("FBConnectErrorHandler/handleError","NullpointerException Caught");
                    return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
                }

                Log.e("FBConnectErrorHandler/handleError",error.getMessage());

                if (error.getCode().equals("NoOtherAuthenticationMethod")) {
                    return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.NO_OTHER_AUTHENTICATION);
                }
            }
            Log.e("FBConnectErrorHandler/handleError","Unidentified Error");
            return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
        }
    }

    public static class OAuthLoginErrorHandler implements ErrorHandler{
        @Override
        public Throwable handleError(RetrofitError cause) {
            Response r = cause.getResponse();

            if (cause.isNetworkError()){
                Log.e("OAuthLoginErrorHandler/handleError","Handling Network Error");
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.NETWORK);
            }
            if (r != null && r.getStatus() == 400) {
                Log.e("OAuthLoginErrorHandler/handleError","Handling 400 Error");
                YammAPIService.YammRetrofitError error = new YammAPIService.YammRetrofitError();
                Gson gson = new Gson();
                try {
                    error = gson.fromJson(responseToString(r), error.getClass());
                }catch(JsonSyntaxException e){
                    Log.e("OAuthLoginErrorHandler/handleError","Json Syntax Exception Caught");
                    return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
                }catch(IllegalStateException e){
                    Log.e("OAuthLoginErrorHandler/handleError","Illegal State Exception Caught");
                    return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
                }catch(NullPointerException e){
                    Log.e("OAuthLoginErrorHandler/handleError","NullpointerException Caught");
                    return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
                }

                Log.e("OAuthLoginErrorHandler/handleError",error.getMessage());

                if (error.getCode().equals("DuplicateEmail")) {
                    return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.DUPLICATE_ACCOUNT);
                }
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.AUTHENTICATION);
            }
            Log.e("OAuthLoginErrorHandler/handleError","Unidentified Error");
            return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
        }
    }

    public static class TokenErrorHandler implements ErrorHandler{
        @Override
        public Throwable handleError(RetrofitError cause) {
            Response r = cause.getResponse();

            if (cause.isNetworkError()){
                Log.e("TokenErrorHandler/handleError","Handling Network Error");
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.NETWORK);
            }
            if (r != null && r.getStatus() == 401) {
                Log.e("LoginErrorHandler/handleError","Handling 401 Error");
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.AUTHENTICATION);
            }
            Log.e("TokenErrorHandler/handleError","Unidentified Error");
            return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
        }
    }

    //  public static interface TokenCallback<T> extends Callback<T>

    public static class DislikeErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError cause) {
            Response r = cause.getResponse();

            if (cause.isNetworkError()){
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.NETWORK);
            }
            YammAPIService.YammRetrofitError error = new YammAPIService.YammRetrofitError();
            Gson gson = new Gson();

            error = gson.fromJson(responseToString(r), error.getClass());
            Log.e("DislikeErrorHandler/handleError",error.getMessage());

            if (r != null && r.getStatus() == 401) {
                Log.e("LoginErrorHandler/handleError","Handling 401 Error");
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.AUTHENTICATION);
            }

            if (error.getCode().equals("TooManyAttempts")) {
                return new YammAPIService.YammRetrofitException(cause, DishFragment.TOO_MANY_DISLIKE);
            }
            return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
        }
    }

    public static class JoinErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError cause) {
            Response r = cause.getResponse();

            if (cause.isNetworkError()){
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.NETWORK);
            }
            YammAPIService.YammRetrofitError error = new YammAPIService.YammRetrofitError();
            Gson gson = new Gson();
            try {
                error = gson.fromJson(responseToString(r), error.getClass());
            }catch(JsonSyntaxException e){
                Log.e("JoinErrorHandler/handleError","Json Syntax Exception Caught");
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
            }catch(IllegalStateException e){
                Log.e("JoinErrorHandler/handleError","Illegal State Exception Caught");
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
            }catch(NullPointerException e){
                Log.e("JoinErrorHandler/handleError","NullpointerException Caught");
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
            }

            if (error == null){
                Log.e("JoinErrorHandler/handleError","Error is Null");
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
            }


            Log.e("JoinErrorHandler/handleError",error.getMessage());


            if (error.getCode().equals("DuplicateAccount")) {
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.DUPLICATE_ACCOUNT);
            }
            else if (error.getCode().equals("IncorrectAuthCode"))
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.INCORRECT_AUTHCODE);
            else if (error.getCode().equals("InvalidParam:password:minlen"))
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.PASSWORD_MIN);
            else if (error.getCode().equals("InvalidParam:password:pattern"))
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.PASSWORD_FORMAT);
            else if (error.getCode().equals("InvalidParam:email:pattern"))
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.EMAIL_FORMAT);
            else if (error.getCode().equals("InvalidParam:phone:pattern"))
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.PHONE_FORMAT);

            Log.e("JoinErrorHandler/handleError", "Handling Unidentified Error");
            return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
        }
    }

    public static class LoginErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError cause) {
            Response r = cause.getResponse();

            if (cause.isNetworkError()){
                Log.e("LoginErrorHandler/handleError","Handling Network Error");
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.NETWORK);
            }
            if (r != null && r.getStatus() == 401) {
                Log.e("LoginErrorHandler/handleError","Handling 401 Error");
                return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.AUTHENTICATION);
            }
            Log.e("LoginErrorHandler/handleError","Unidentified Error");
            return new YammAPIService.YammRetrofitException(cause, YammAPIService.YammRetrofitException.UNIDENTIFIED);
        }
    }

    private static String responseToString(Response result){
        //Try to get response body
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {

            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
