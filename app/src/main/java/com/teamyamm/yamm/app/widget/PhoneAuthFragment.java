package com.teamyamm.yamm.app.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.teamyamm.yamm.app.BaseActivity;
import com.teamyamm.yamm.app.IntroActivity;
import com.teamyamm.yamm.app.NewJoinActivity;
import com.teamyamm.yamm.app.R;

/**
 * Created by parkjiho on 10/9/14.
 */
public class PhoneAuthFragment extends Fragment {

    private int authType;
    private String phone, name, pw;
    private TextView title;
    private EditText auth;
    private Button confirm;

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
        if (authType == IntroActivity.KAKAO){

        }
        else if (authType == IntroActivity.FB){

        }
        else{

        }
    }
}
