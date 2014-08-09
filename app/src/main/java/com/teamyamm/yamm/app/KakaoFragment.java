package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kakao.AppActionBuilder;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;

/**
 * Created by parkjiho on 7/15/14.
 */
public class KakaoFragment extends Fragment {
    Button kakaoButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_kakao, container, false);

        kakaoButton = (Button) layout.findViewById(R.id.kakao_button);
        kakaoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInviteKakaoLink();
            }
        });


        return layout;
    }


    private void sendInviteKakaoLink(){
        try {
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getActivity());
            final KakaoTalkLinkMessageBuilder msgBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            String imageURL = "https://parkjiho.files.wordpress.com/2014/07/yamm_google_play.png";
            msgBuilder.addText("Yamm 다운받기");
            msgBuilder.addImage(imageURL, 80, 80);

            AppActionBuilder app = new AppActionBuilder();

            app.setAndroidExecuteURLParam("market://details?id=com.google.android.youtube");
            app.setIOSExecuteURLParam("https://itunes.apple.com/us/app/secret-speak-freely/id775307543");

            msgBuilder.addAppButton("앱으로 이동", app.build());

            final String linkContents = msgBuilder.build();
            kakaoLink.sendMessage(linkContents, getActivity());

            if (getActivity() instanceof InviteActivity) {
                ((InviteActivity)getActivity()).trackInviteMixpanel("KAKAO", 0);
            }

        }catch(KakaoParameterException e){
            Log.e("KaKaoFragment/sendKakaLink", "Kakao link init error");
            e.printStackTrace();
        }
    }

}
