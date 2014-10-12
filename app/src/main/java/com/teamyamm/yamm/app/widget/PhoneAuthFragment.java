package com.teamyamm.yamm.app.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    }

    private void configConfirmButton(){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = "";
                if (getActivity() instanceof NewJoinActivity) {
                    token = ((NewJoinActivity) getActivity()).getOAuthToken();
                    fullScreenDialog = ((NewJoinActivity) getActivity()).createFullScreenDialog(getActivity(), getString(R.string.join_progress_dialog_title));
                }

                if (authType == IntroActivity.KAKAO)
                    kakaoRegistration(token);
                else if (authType == IntroActivity.FB)
                    fbRegistration(token);
                else
                    pwRegistration(((NewJoinActivity)getActivity()).getPassword());
            }
        });
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

            }
        });
    }

    private void pwRegistration(String pw){
        YammAPIAdapter.getJoinService().pwRegistration(name, pw, phone, auth.getText().toString(), new Callback<YammAPIService.YammToken>() {
            @Override
            public void success(YammAPIService.YammToken yammToken, Response response) {
                Log.i("PhoneAuthFragment/fbRegistration", "Password Registration Success");
                MixpanelController.setMixpanelAlias(yammToken.uid + "@password");
                MixpanelController.trackJoiningMixpanel("PW");

                finishRegistration(yammToken.access_token);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

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
}
