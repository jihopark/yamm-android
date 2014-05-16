package com.teamyamm.yamm.app;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 5/15/14.
 */
public class StreamListAdapter extends BaseAdapter {
    private List<DishItem> items;
    private Context mContext;
    private int screenWidth;

    static class ViewHolder{
        public TextView text;
        public ImageView image;
    }

    public StreamListAdapter(Context context){
        mContext = context;
    }
    public StreamListAdapter(Context context, ArrayList<DishItem> list, int w){
        this(context);
        items = list;
        screenWidth = w;
    }

    @Override
    public long getItemId(int p){
        return items.get(p).getId();
    }

    @Override
    public DishItem getItem(int p){ return items.get(p); }

    @Override
    public int getCount(){ return items.size(); }

    public View getView(int position, View convertView, ViewGroup parent){
        DishStreamView view = (DishStreamView) convertView;
        Log.v("StreamListAdapter/getView", "getView Started");

        if (convertView == null) {
            Log.v("StreamListAdapter/getView", "Make New DishStreamView");
            view = new DishStreamView(mContext, getItem(position), parent);

            //Configure View Holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.dish_stream_text);
            viewHolder.image = (ImageView) view.findViewById(R.id.dish_stream_image);
            view.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        //Set TextView
        holder.text.setText(getItem(position).getName());

        //Set ImageView
        int height = (int)(DishStreamView.RATIO*screenWidth);
        Picasso.with(mContext).load(BaseActivity.getDishImageURL(getItem(position).getId()
                ,screenWidth,height))
                .placeholder(R.drawable.image_placeholer)
                .into(holder.image);


        Log.v("StreamListAdapter/getView", "imageurl " +BaseActivity.getDishImageURL(getItem(position).getId()
                ,screenWidth,height));
        Log.v("StreamListAdapter/getView", "set dish item to view - " + getItem(position).getName());
        return view;
    }

    public void addDishItem(DishItem item){
        items.add(item);
    }
}
