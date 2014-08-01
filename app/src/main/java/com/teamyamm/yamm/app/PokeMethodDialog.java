package com.teamyamm.yamm.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;

/**
 * Created by parkjiho on 5/17/14.
 */
public class PokeMethodDialog extends DialogFragment{
    private String[] listContent = {"얌친", "카카오톡", "SMS"};
    private ListView listView;

    private final int YAMM = 0;
    private final int KAKAO = 1;
    private final int SMS = 2;

    public PokeMethodDialog(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poke_method, container);
        getDialog().setTitle(getString(R.string.poke_method_dialog_title));
        listView = (ListView) view.findViewById(R.id.poke_method_list);
        listView.setAdapter(new PokeMethodListAdapter(getActivity(), listContent));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("onItemClick", "onItemClick called");
                switch (position) {
                    case KAKAO:
                        sendKakaoLink();
                        break;
                    case YAMM:
                        break;
                    case SMS:
                        break;
                }

                dismiss();
            }
        });
        return view;
    }


    private void sendKakaoLink(){
        try {
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getActivity());
            final KakaoTalkLinkMessageBuilder msgBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            msgBuilder.addText(((DishFragment)getParentFragment()).getDishItem().getName() + " 같이 먹을래요?");

            final String linkContents = msgBuilder.build();
            kakaoLink.sendMessage(linkContents, getActivity());

        }catch(KakaoParameterException e){
            Log.e("NewMainFragment/sendKakaLink", "Kakao link init error");
            e.printStackTrace();
        }
    }

    public class PokeMethodListAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public PokeMethodListAdapter(Context context, String[] values) {
            super(context, R.layout.poke_method_list_item, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.poke_method_list_item, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.label);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

            textView.setText(values[position]);

            // Change the icon for Windows and iPhone
            switch (position) {
                case KAKAO:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.kakaolink_btn_medium));
                    break;
                case YAMM:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.yamm_launcher));
                    break;
                case SMS:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.hangout_sms));
                    break;
            }

            return rowView;
        }
    }
}
