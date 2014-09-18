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
import com.teamyamm.yamm.app.interfaces.FriendListInterface;
import com.teamyamm.yamm.app.FriendsFragment;
import com.teamyamm.yamm.app.R;
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
        YammItemView layout = (YammItemView) inflater.inflate(R.layout.yamm_item_view, this, true);

        itemNameText = (TextView) layout.findViewById(R.id.yamm_item_name_text);
        itemCheckbox = (CheckBox) layout.findViewById(R.id.yamm_item_check);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemCheckbox.getLayoutParams();

        params.setMargins(0, 0, (int) (40*context.getResources().getDisplayMetrics().density), 0);

        Log.d("YammItemView/constructor",getResources().getDimension(R.dimen.yamm_item_right_margin)+"");
        Log.d("YammItemView/constructor",30*context.getResources().getDisplayMetrics().density+"");


        itemCheckbox.setLayoutParams(params);

        fragment = (FriendsFragment) ((BaseActivity)context).getSupportFragmentManager().findFragmentByTag(((FriendListInterface)context).getFragmentTag(contentType));

        setItem(item);
        itemCheckbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("YammItemView/itemCheckBoxOnClickListener","Checkbox Clicked");
                toggle();
            }
        });
    }

    public void setItem(YammItem f){
        item = f;
        itemNameText.setText(item.getName());


        if (isSelected())
            itemCheckbox.setChecked(true);
        else
            itemCheckbox.setChecked(false);

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