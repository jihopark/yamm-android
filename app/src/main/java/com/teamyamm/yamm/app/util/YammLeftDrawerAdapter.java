package com.teamyamm.yamm.app.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamyamm.yamm.app.R;
import com.teamyamm.yamm.app.pojos.LeftDrawerItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by parkjiho on 9/3/14.
 */
public class YammLeftDrawerAdapter extends BaseAdapter {
    private final static int pushUsageIndex = 3;
    private List<LeftDrawerItem> items;
    private Context context;

    public YammLeftDrawerAdapter(Context context) {
        this.context = context;
        items = new LinkedList<LeftDrawerItem>();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("LeftDrawerSetting", "Get View at " +position);

        LeftDrawerItemView view = null;

        if (convertView == null)
            view = new LeftDrawerItemView(context, getItem(position));
        else{
            view = (LeftDrawerItemView) convertView;
            view.setItem(getItem(position));
        }

        return view;
    }

    public void setPushUsageMenu(boolean b){
        LeftDrawerItem item;
        if (b){
            item = new LeftDrawerItem(context.getString(R.string.left_drawer_alarm_title),
                    context.getString(R.string.left_drawer_alarm_status_positive), pushUsageIndex);
        }
        else{
            item = new LeftDrawerItem(context.getString(R.string.left_drawer_alarm_title),
                    context.getString(R.string.left_drawer_alarm_status_negative), pushUsageIndex);
        }
        if (items.size() > pushUsageIndex)
            items.set(pushUsageIndex, item);
        else
            items.add(pushUsageIndex, item);

        Log.i("YammLeftDrawerAdapter/setPushUsageMenu","Push Usage Menu set to " + b);
        notifyDataSetInvalidated();
    }

    public void addMenuItems(LeftDrawerItem item){
        items.add(item);
    }

    public int getCount(){return items.size(); }

    public LeftDrawerItem getItem(int p){ return items.get(p); }

    public long getItemId(int p){
        return items.get(p).position;
    }

    private class LeftDrawerItemView extends RelativeLayout{
        private final int[] iconRes = {R.drawable.sidemenu_icon_01, R.drawable.sidemenu_icon_02,
                                          R.drawable.sidemenu_icon_03, R.drawable.sidemenu_icon_04,
                                            R.drawable.sidemenu_icon_05};

        private LeftDrawerItem item;
        private Context context;
        private RelativeLayout layout;
        private TextView txt1, txt2;
        private ImageView icon;


        public LeftDrawerItemView(Context context) {
            super(context);
        }
        public LeftDrawerItemView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }


        public LeftDrawerItemView(Context context, LeftDrawerItem item){
            super(context);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (RelativeLayout) inflater.inflate(R.layout.left_drawer_item, this, true);

            this.context = context;
            setItem(item);
        }

        void setItem(LeftDrawerItem item){
            this.item = item;

            icon = (ImageView) layout.findViewById(R.id.left_drawer_item_icon);
            if (item.position < iconRes.length)
                icon.setImageResource(iconRes[item.position]);

            txt1 = (TextView) layout.findViewById(R.id.left_drawer_item_text1);
            txt2 = (TextView) layout.findViewById(R.id.left_drawer_item_text2);

            txt1.setText(item.firstText);
            txt2.setText(item.secondText);

            if (item.firstClick!=null)
                txt1.setOnClickListener(item.firstClick);
            if (item.secondClick!=null)
                txt2.setOnClickListener(item.secondClick);
            if (item.totalClick!=null)
                layout.setOnClickListener(item.totalClick);

            Log.d("LeftDrawerSetting", "Item Set for " + item.firstText);
        }
    }


}
