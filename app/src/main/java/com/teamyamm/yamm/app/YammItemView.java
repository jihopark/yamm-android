package com.teamyamm.yamm.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by parkjiho on 5/19/14.
 */
public class YammItemView extends LinearLayout {
    YammItem item;
    TextView yammItemNameText;

    public YammItemView(Context context) {
        super(context);
    }

    public YammItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YammItemView(Context context, YammItem i) {
        super(context);
        item = i;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.yamm_item_view, this, true);

        yammItemNameText = (TextView) layout.findViewById(R.id.yamm_item_name_text);

        yammItemNameText.setText(i.getName());
    }

    public void setItem(YammItem f){
        item = f;
        yammItemNameText.setText(item.getName());
    }
}