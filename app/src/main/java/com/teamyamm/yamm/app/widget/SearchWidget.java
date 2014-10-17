package com.teamyamm.yamm.app.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.teamyamm.yamm.app.BaseActivity;
import com.teamyamm.yamm.app.R;
import com.teamyamm.yamm.app.util.DishSearchListAdapter;

/**
 * Created by parkjiho on 10/16/14.
 */
public class SearchWidget {

    private Context context;
    private View mainView = null;
    private AutoCompleteTextView textView;
    private Button searchButton;
    private boolean isSearchEnabled = false;

    public SearchWidget(Context context){
        this.context = context;
    }

    public View getCustomView(){
        if (mainView!=null)
            return mainView;

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(R.layout.main_activity_custom_bar, null);

        searchButton = (Button) mainView.findViewById(R.id.search_button);
        textView = (AutoCompleteTextView) mainView.findViewById(R.id.search_text_view);

        setAutoCompleteTextView();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(true);
            }
        });

        return mainView;
    }

    public void toggle(boolean b){
        isSearchEnabled = b;
        if (isSearchEnabled) {
            textView.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.GONE);
        }
        else{
            textView.setVisibility(View.GONE);
            searchButton.setVisibility(View.VISIBLE);
        }
        if (b && context instanceof BaseActivity){
            BaseActivity.showSoftKeyboard(textView, (Activity)context);
        }
    }

    private void setAutoCompleteTextView(){
        textView.setThreshold(1);
        textView.setSelectAllOnFocus(true);

        DishSearchListAdapter place_adapter =
                new DishSearchListAdapter(context);
        textView.setAdapter(place_adapter);
    }
}
