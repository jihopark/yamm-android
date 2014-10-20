package com.teamyamm.yamm.app.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.teamyamm.yamm.app.BaseActivity;
import com.teamyamm.yamm.app.R;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.pojos.DishItem;
import com.teamyamm.yamm.app.util.DishSearchListAdapter;
import com.teamyamm.yamm.app.util.LocationSearchHelper;

/**
 * Created by parkjiho on 10/16/14.
 */
public class SearchWidget {

    private Context context;
    private View mainView = null;
    private AutoCompleteTextView textView;
    private Button searchButton;
    private boolean isSearchEnabled = false;
    private DishSearchListAdapter adapter;

    public SearchWidget(Context context){
        this.context = context;
    }

    private void setAutoCompleteTextView(){
        textView.setThreshold(1);
        textView.setSelectAllOnFocus(true);
        textView.requestFocus();

        adapter =
                new DishSearchListAdapter(context);
        textView.setAdapter(adapter);
        BaseActivity.showSoftKeyboard(textView, (Activity)context);
    }

    public void showSearchDialog(){
        MixpanelController.trackEnteredSearchDish();

        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_search);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        textView = (AutoCompleteTextView) dialog.findViewById(R.id.dish_search_autocomplete_text);

        //ImageButton setMap = (ImageButton) dialog.findViewById(R.id.map_icon);
        ImageButton negative = (ImageButton) dialog.findViewById(R.id.dish_search_dialog_negative_button);
        Button positive = (Button) dialog.findViewById(R.id.dish_search_dialog_positive_button);

        setAutoCompleteTextView();

        /*setMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(mContext.getString(R.string.place_pick_edit_text));
            }
        });*/
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.hideSoftKeyboard((Activity)context);
                dialog.dismiss();
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DishItem item =null;
                if (textView.getText()!=null)
                    item = adapter.checkIfDishIsPresent(textView.getText().toString());
                if (item!=null){
                    BaseActivity.hideSoftKeyboard((Activity)context);
                    dialog.dismiss();
                    MixpanelController.trackSearchDishMixpanel(item);
                    LocationSearchHelper.startMapActivity(context, item);
                }
                else{
                    if (context instanceof BaseActivity){
                        ((BaseActivity) context).makeYammToast(R.string.no_matching_dish_error, Toast.LENGTH_SHORT);
                    }
                }


            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
