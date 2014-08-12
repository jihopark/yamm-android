package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.kakao.AppActionBuilder;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;

/**
 * Created by parkjiho on 7/15/14.
 */
public class KakaoFragment extends Fragment {
    public final static String TYPE = "ty";
    public final static String DISH = "ds";
    public final static String TIME = "tm";


    public final static int INVITE = 1;
    public final static int POKE = 2;


    private Button kakaoButton;
    private int type;
    private DishItem currentItem;
    private String time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_kakao, container, false);

        getBundle();

        kakaoButton = (Button) layout.findViewById(R.id.kakao_button);
        kakaoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == INVITE) {
                    sendInviteKakaoLink();
                    if (getActivity() instanceof InviteActivity) {
                        ((InviteActivity)getActivity()).trackSendInviteMixpanel("KAKAO", 0);
                    }
                }
                else if (type == POKE){
                    sendPokeKakaoLink();
                    if (getActivity() instanceof PokeActivity) {
                        ((PokeActivity)getActivity()).trackPokeFriendMixpanel("KAKAO", 0, time, currentItem.getName());
                    }
                }
            }
        });


        return layout;
    }

    private void getBundle(){
        if (this.getArguments() != null) {
            type = this.getArguments().getInt(TYPE);
            if (type == POKE) {
                currentItem = new Gson().fromJson(getArguments().getString(DISH), DishItem.class);
                time = getArguments().getString(TIME);
            }
            Log.i("KakaoFragment/getBundle", "Type " + type + " Dish " + currentItem + " Time " + time);
        }
    }


    private void sendPokeKakaoLink(){
        try {
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getActivity());
            final KakaoTalkLinkMessageBuilder msgBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            msgBuilder.addText(time + "에 " + currentItem.getName() + " 같이 먹을래요?");

            final String linkContents = msgBuilder.build();
            kakaoLink.sendMessage(linkContents, getActivity());

        }catch(KakaoParameterException e){
            Log.e("NewMainFragment/sendKakaLink", "Kakao link init error");
            e.printStackTrace();
        }
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

        }catch(KakaoParameterException e){
            Log.e("KaKaoFragment/sendKakaoLink", "Kakao link init error");
            e.printStackTrace();
        }
    }

}
