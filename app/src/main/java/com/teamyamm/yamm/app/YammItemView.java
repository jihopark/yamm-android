package com.teamyamm.yamm.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by parkjiho on 5/19/14.
 */
public class YammItemView extends LinearLayout {
    private YammItem item;
    private TextView itemNameText, itemSelectedText;
    private boolean selected = false;

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
        YammItemView layout = (YammItemView) inflater.inflate(R.layout.yamm_item_view, this, true);

        itemNameText = (TextView) layout.findViewById(R.id.yamm_item_name_text);
        itemSelectedText = (TextView) layout.findViewById(R.id.yamm_item_selected_text);

        itemNameText.setText(i.getName());

        //On Touch Listener that toggles view
        layout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                toggle();
                return false;
            }
        });
    }

    public void setItem(YammItem f){
        item = f;
        itemNameText.setText(item.getName());
    }

    public boolean isSelected(){
        return selected;
    }

    public void toggle(){
        selected = !selected;
        if (selected)
            itemSelectedText.setVisibility(TextView.VISIBLE);
        else
            itemSelectedText.setVisibility(TextView.GONE);
    }
}