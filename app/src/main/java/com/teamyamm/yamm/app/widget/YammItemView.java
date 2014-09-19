package com.teamyamm.yamm.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamyamm.yamm.app.BaseActivity;
import com.teamyamm.yamm.app.FriendsFragment;
import com.teamyamm.yamm.app.R;
import com.teamyamm.yamm.app.interfaces.FriendListInterface;
import com.teamyamm.yamm.app.pojos.YammItem;


/**
 * Created by parkjiho on 5/19/14.
 */
public class YammItemView extends LinearLayout {
    private YammItem item;
    private TextView itemNameText;
    private CheckBox itemCheckbox;
    private FriendsFragment fragment;
    private Context context;
    private int contentType;
    private YammItemView layout;

    public YammItemView(Context context) {
        super(context);
    }

    public YammItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YammItemView(Context context, YammItem i, int type) {
        super(context);

        this.context = context;
        this.item = i;
        contentType = type;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (YammItemView) inflater.inflate(R.layout.yamm_item_view, this, true);

        itemNameText = (TextView) layout.findViewById(R.id.yamm_item_name_text);
        itemCheckbox = (CheckBox) layout.findViewById(R.id.yamm_item_check);

        fragment = (FriendsFragment) ((BaseActivity)context).getSupportFragmentManager().findFragmentByTag(((FriendListInterface)context).getFragmentTag(contentType));

        setItemCheckBoxMargin(IndexableListView.currentMarginState);

        setItem(item);


        itemCheckbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("YammItemView/itemCheckBoxOnClickListener","Checkbox Clicked");
                toggle();
            }
        });

    }
    /*
    * False = default, True = when IndexScroller is up
    * */
    public void setItemCheckBoxMargin(boolean b) {

        final int margin;
        if (b)
            margin = (int) (40 * context.getResources().getDisplayMetrics().density);
        else
            margin = (int) context.getResources().getDimension(R.dimen.yamm_item_right_margin);


        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(itemCheckbox.getLayoutParams());
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        params.setMargins(0, 0, margin, 0);

        itemCheckbox.setLayoutParams(params);

    }

    public void setItem(YammItem f){
        item = f;
        itemNameText.setText(item.getName());


        if (isSelected())
            itemCheckbox.setChecked(true);
        else
            itemCheckbox.setChecked(false);

        if (item.getID() == -1){
            Log.d("YammItemView/setItem","Found Dummy");
            layout.setVisibility(INVISIBLE);
        }
        else
            layout.setVisibility(VISIBLE);
    }

    public boolean isSelected(){
        return item.getSelected();
    }

    public YammItem getItem(){
        return item;
    }

    public void toggle(){
        item.toggle();


        //Change Visibility of Selected Text
        if (item.getSelected())
            itemCheckbox.setChecked(true);
        else
            itemCheckbox.setChecked(false);


        //Put Selected Item to List
        if (fragment == null){
            Log.e("YammItemView/constructor","Cannot find friendfragment");
        }
        else{
            if (item.getSelected())
                fragment.addSelectedItem(item);
            else
                fragment.removeSelectedItem(item);

            //Change Enabled of Confirm Button in FriendActivity
            int selectedItemSize = fragment.getSelectedItems().size();
            if (selectedItemSize == 0){
                fragment.setConfirmButtonEnabled(false);
            }
            else if (selectedItemSize == 1){
                fragment.setConfirmButtonEnabled(true);
            }
        }
    }
}