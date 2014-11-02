package com.teamyamm.yamm.app.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.teamyamm.yamm.app.BaseActivity;
import com.teamyamm.yamm.app.GridActivity;
import com.teamyamm.yamm.app.IntroActivity;
import com.teamyamm.yamm.app.NewJoinActivity;
import com.teamyamm.yamm.app.R;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.network.YammAPIService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 10/9/14.
 */
public class PhoneAuthFragment extends Fragment {

    private int authType;
    private String phone, name, pw;
    private TextView title;
    private EditText auth;
    private Button confirm;
    private Dialog fullScreenDialog = null;

    public PhoneAuthFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_phone_auth, container, false);

        Log.i("PhoneAuthFragment/onCreateView", "Fragment Created");


        title = (TextView) rootView.findViewById(R.id.phone_auth_title);
        auth = (EditText) rootView.findViewById(R.id.phone_auth_field);
        confirm = (Button) rootView.findViewById(R.id.phone_auth_submit);

        initFragment();
        configConfirmButton();
        return rootView;
    }

    public EditText getEditText(){
        return auth;
    }

    private void initFragment(){
        Bundle bundle = this.getArguments();
        authType = bundle.getInt(IntroActivity.AUTH_TYPE);
        if (authType==IntroActivity.PW)
            pw = bundle.getString(NewJoinActivity.PW);
        phone = bundle.getString(NewJoinActivity.PHONE);
        name = bundle.getString(NewJoinActivity.NAME);

        getActivity().setTitle(R.string.title_fragment_phone_auth);

        //set up title
        if (getActivity() instanceof BaseActivity)
            title.setText(((BaseActivity)getActivity()).phoneNumberFormat(phone)+getString(R.string.phone_auth_title_message));
        if (getActivity() instanceof NewJoinActivity)
            ((NewJoinActivity) getActivity()).configSmsListener(auth);
    }

    private void configConfirmButton(){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = "";
                if (getActivity() instanceof NewJoinActivity) {
                    token = ((NewJoinActivity) getActivity()).getOAuthToken();
                    fullScreenDialog = ((NewJoinActivity) getActivity()).createFullScreenDialog(getActivity(), getString(R.string.join_progress_dialog_title));
                    fullScreenDialog.show();
                }

                if (authType == IntroActivity.KAKAO)
                    kakaoRegistration(token);
                else if (authType == IntroActivity.FB)
                    fbRegistration(token);
                else
                    pwRegistration(((NewJoinActivity)getActivity()).getPassword());
            }
        });
        auth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4){
                    enableConfirmButton(true);
                }
                else{
                    enableConfirmButton(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void enableConfirmButton(boolean b){
        if (b){
            confirm.setEnabled(true);
            confirm.setBackgroundResource(R.drawable.enabled_round_button);
            confirm.setTextColor(getResources().getColor(R.color.button_enabled_text));
        }
        else{
            confirm.setEnabled(false);
            confirm.setBackgroundResource(R.drawable.disabled_round_button);
            confirm.setTextColor(getResources().getColor(R.color.button_disabled_text));
        }
    }

    private void kakaoRegistration(String token){
        YammAPIAdapter.getJoinService().kakaoRegistration(name, token, phone, auth.getText().toString(), new Callback<YammAPIService.YammToken>() {
            @Override
            public void success(YammAPIService.YammToken yammToken, Response response) {
                Log.i("PhoneAuthFragment/fbRegistration", "KAKAO Registration Success");
                MixpanelController.setMixpanelAlias(yammToken.uid + "@kakao");
                MixpanelController.trackJoiningMixpanel("KAKAO");

                finishRegistration(yammToken.access_token);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                handleJoinError(retrofitError.getCause().getMessage());
            }
        });
    }

    private void fbRegistration(String token){
        YammAPIAdapter.getJoinService().facebookRegistration(name, token, phone, auth.getText().toString(), new Callback<YammAPIService.YammToken>() {
            @Override
            public void success(YammAPIService.YammToken yammToken, Response response) {
                Log.i("PhoneAuthFragment/fbRegistration","FB Registration Success");
                MixpanelController.setMixpanelAlias(yammToken.uid+"@facebook");
                MixpanelController.trackJoiningMixpanel("FB");

                finishRegistration(yammToken.access_token);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                handleJoinError(retrofitError.getCause().getMessage());
            }
        });
    }

    private void pwRegistration(String pw){
        YammAPIAdapter.getJoinService().pwRegistration(name, pw, phone, auth.getText().toString(), new Callback<YammAPIService.YammToken>() {
            @Override
            public void success(YammAPIService.YammToken yammToken, Response response) {
                Log.i("PhoneAuthFragment/pwRegistration", "Password Registration Success");
                MixpanelController.setMixpanelAlias(yammToken.uid + "@password");
                MixpanelController.trackJoiningMixpanel("PW");

                finishRegistration(yammToken.access_token);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("PhoneAuthFragment/pwRegistration", "Password Registration Failure " + retrofitError.getBody());
                handleJoinError(retrofitError.getCause().getMessage());

            }
        });
    }

    private void finishRegistration(String accessToken){
        if (fullScreenDialog!=null)
            fullScreenDialog.dismiss();

        BaseActivity act = (BaseActivity) getActivity();
        act.putInPref(act.prefs, getString(R.string.AUTH_TOKEN), accessToken);
        YammAPIAdapter.setToken(accessToken);
        act.goToActivity(GridActivity.class);

        act.makeYammToast(R.string.join_success, Toast.LENGTH_SHORT);
    }

    private void handleJoinError(String msg) {
        Log.e("PhoneAuthFragment/handleJoinError", "ERROR CODE " + msg);

        BaseActivity act = (BaseActivity) getActivity();

        if (fullScreenDialog!=null)
            fullScreenDialog.dismiss();

        if (msg.equals(YammAPIService.YammRetrofitException.NETWORK))
            act.makeYammToast(getString(R.string.network_error_message), Toast.LENGTH_SHORT);
        else if (msg.equals(YammAPIService.YammRetrofitException.DUPLICATE_ACCOUNT))
            act.makeYammToast(getString(R.string.duplicate_account_error_message), Toast.LENGTH_SHORT);
        else if (msg.equals(YammAPIService.YammRetrofitException.INVALID_TOKEN))
            act.makeYammToast(getString(R.string.invalid_token_error_message), Toast.LENGTH_SHORT);
        else if (msg.equals(YammAPIService.YammRetrofitException.INCORRECT_AUTHCODE))
            act.makeYammToast(getString(R.string.incorrect_authcode_error_message), Toast.LENGTH_SHORT);
        else if (msg.equals(YammAPIService.YammRetrofitException.PASSWORD_FORMAT))
            act.makeYammToast(getString(R.string.password_format_error_message), Toast.LENGTH_SHORT);
        else if (msg.equals(YammAPIService.YammRetrofitException.PASSWORD_MIN))
            act.makeYammToast(getString(R.string.password_min_error_message), Toast.LENGTH_SHORT);
        else if (msg.equals(YammAPIService.YammRetrofitException.DUPLICATE_OAUTH_ACCOUNT))
            act.makeYammToast(getString(R.string.duplicate_oauth_account_error_message),Toast.LENGTH_SHORT);
        else
            act.makeYammToast(getString(R.string.unidentified_error_message), Toast.LENGTH_SHORT);
    }
}
