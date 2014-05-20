package com.teamyamm.yamm.app;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
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
    private TextView pickText;
    private FriendsFragment fragment;
    private Context activity;

    public YammItemView(Context context) {
        super(context);
    }

    public YammItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YammItemView(Context context, YammItem i) {
        super(context);

        activity = context;

        item = i;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        YammItemView layout = (YammItemView) inflater.inflate(R.layout.yamm_item_view, this, true);

        itemNameText = (TextView) layout.findViewById(R.id.yamm_item_name_text);
        itemSelectedText = (TextView) layout.findViewById(R.id.yamm_item_selected_text);

        //If item is selected
        if (isSelected())
            itemSelectedText.setVisibility(TextView.VISIBLE);
        else
            itemSelectedText.setVisibility(TextView.GONE);

        pickText = (TextView) ((Activity)context).findViewById(R.id.friend_pick_edit_text);

        itemNameText.setText(i.getName());
        Log.i("YammItemView",i.getName() + "created");
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
        if (isSelected())
            itemSelectedText.setVisibility(TextView.VISIBLE);
        else
            itemSelectedText.setVisibility(TextView.GONE);
    }

    public boolean isSelected(){
        return item.getSelected();
    }

    public void toggle(){
        item.toggle();

        //Change Visibility of Selected Text
        if (item.getSelected())
            itemSelectedText.setVisibility(TextView.VISIBLE);
        else
            itemSelectedText.setVisibility(TextView.GONE);

        //Change SearchText
        if (pickText !=null && activity instanceof MainActivity){
            MainActivity act = ((MainActivity)activity);
            if (item.getSelected())
                act.addItemToSelectedList(item);
            else
                act.removeItemToSelectedList(item);

            String s = "";
            for (YammItem i : act.getYammItemSelectedList()){
                s = s + getResources().getString(R.string.selected_item_html_tag_start) + i.getName()
                        + getResources().getString(R.string.selected_item_html_tag_end) + " ";
            }
            pickText.setText(Html.fromHtml(s));
        }
        else{
            Log.e("YammItemView","cannot find yamm item search text");
        }
    }
}