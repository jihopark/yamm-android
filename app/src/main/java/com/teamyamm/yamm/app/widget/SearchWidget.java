package com.teamyamm.yamm.app.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.teamyamm.yamm.app.BaseActivity;
import com.teamyamm.yamm.app.R;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.network.YammAPIService;
import com.teamyamm.yamm.app.pojos.DishItem;
import com.teamyamm.yamm.app.pojos.SearchCategory;
import com.teamyamm.yamm.app.util.DishSearchListAdapter;
import com.teamyamm.yamm.app.util.LocationSearchHelper;
import com.teamyamm.yamm.app.util.WTFExceptionHandler;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
    private List<DishItem> list;

    public SearchWidget(List<DishItem> list, Context context){
        this.list = list;
        this.context = context;
    }

    private void setAutoCompleteTextView(){
        textView.setThreshold(2);
        textView.setSelectAllOnFocus(true);
        textView.requestFocus();

        adapter =
                new DishSearchListAdapter(list, context);
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

        ImageButton negative = (ImageButton) dialog.findViewById(R.id.dish_search_dialog_negative_button);
        Button positive = (Button) dialog.findViewById(R.id.dish_search_dialog_positive_button);

        setAutoCompleteTextView();

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

                    if (item instanceof SearchCategory) {
                        MixpanelController.trackSearchCategoryMixpanel((SearchCategory)item);
                    }
                    else{
                        addDishToPositive(item);
                        MixpanelController.trackSearchDishMixpanel(item);
                    }
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

    public void addCategories(List<SearchCategory> searchCategories){
        adapter.addCategories(searchCategories);
    }

    private void addDishToPositive(DishItem item){
        final String category = "SEARCHDISH";

        YammAPIService service = YammAPIAdapter.getTokenService();

        Log.d("SearchWidget/addDishToPositive", "Like " + item.getName() + " SEARCHDISH");

        if (service==null) {
            if (context instanceof BaseActivity) {
                ((BaseActivity) context).invalidToken();
                WTFExceptionHandler.sendLogToServer(context, "WTF Invalid Token Error @DishFragment/addDishToPositive");
            }
            return ;
        }

        service.postLikeDish(new YammAPIService.RawLike(item.getId(), category, ""), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.i("SearchWidget/postLikeDish","Success " + s);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String msg = retrofitError.getCause().getMessage();
                if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION)) {
                    Log.e("SearchWidget/addDishToPositive", "Invalid Token, Logging out");
                    if (context instanceof BaseActivity) {
                        ((BaseActivity) context).invalidToken();
                        return ;
                    }
                }
            }
        });
    }
}
