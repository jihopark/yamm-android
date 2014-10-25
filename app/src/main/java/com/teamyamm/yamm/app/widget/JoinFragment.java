package com.teamyamm.yamm.app.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.widget.LoginButton;
import com.teamyamm.yamm.app.BaseActivity;
import com.teamyamm.yamm.app.LoginActivity;
import com.teamyamm.yamm.app.R;

/**
 * Created by parkjiho on 10/8/14.
 */
public class JoinFragment extends Fragment {

    private ViewGroup rootView;
    private ClickableSpan loginSpan;

    public JoinFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_join, container, false);

        return rootView;
    }

    public Button getEmailJoinButton(){
        return (Button) rootView.findViewById(R.id.join_button);
    }

    public com.kakao.widget.LoginButton getKakaoJoinButton(){
        return (com.kakao.widget.LoginButton) rootView.findViewById(R.id.kakao_login_button);
    }

    public LoginButton getFacebookJoinButton(){
        return (LoginButton) rootView.findViewById(R.id.fb_auth_button);
    }

    public void setLoginButton(){
        loginSpan = new NonUnderlinedClickableSpan();

        TextView tv = (TextView) rootView.findViewById(R.id.login_text);
        SpannableString s = new SpannableString(getString(R.string.intro_login_text));
        s.setSpan(loginSpan, s.length()-3, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(s);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public class NonUnderlinedClickableSpan extends ClickableSpan
    {
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.join_fragment_span_color));
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            if (getActivity() instanceof BaseActivity) {
                ((BaseActivity)getActivity()).goToActivity(LoginActivity.class);
            }
        }
    }

}