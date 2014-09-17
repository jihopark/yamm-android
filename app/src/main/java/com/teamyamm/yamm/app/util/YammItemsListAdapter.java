package com.teamyamm.yamm.app.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

import com.teamyamm.yamm.app.pojos.YammItem;
import com.teamyamm.yamm.app.widget.YammItemView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by parkjiho on 5/19/14.
 */
public class YammItemsListAdapter extends BaseAdapter implements SectionIndexer {
    private List<YammItem> items;
    private Context context;
    private int contentType;

    private String mSections = "";


    public YammItemsListAdapter(Context context){
        this.context = context;
        items = new ArrayList<YammItem>();
    }

    public YammItemsListAdapter(Context context, List<YammItem> list, int contentType){
        this.context = context;
        items = list;
        Collections.sort(items);
        this.contentType = contentType;
        createSectionsForIndex();
    }


    public View getView(int position, View convertView, ViewGroup parent){
        YammItemView view = null;

        if (convertView == null)
            view = new YammItemView(context, getItem(position), contentType);
        else{
            view = (YammItemView) convertView;
            view.setItem(getItem(position));
        }

        return view;
    }

    private void createSectionsForIndex(){
        char current = '-';

        for (YammItem item : items){
            char c = item.getName().charAt(0);
            if (StringMatcher.isKorean(c))
                c = StringMatcher.getInitialSound(c);

            if (current=='-' || c!=current) {
                mSections += c;
                current = c;
            }
        }
        Log.i("YammItemsListAdatper/createSectionsForIndex","Index Created " + mSections);
    }

    @Override
    public int getPositionForSection(int section) {
        // If there is no item for current section, previous section will be selected
        for (int i = section; i >= 0; i--) {
            for (int j = 0; j < getCount(); j++) {
                if (i == 0) {
                    // For numeric section
                    for (int k = 0; k <= 9; k++) {
                        if (StringMatcher.match(String.valueOf(getItem(j).getName().charAt(0)), String.valueOf(k)))
                            return j;
                    }
                } else {
                    if (StringMatcher.match(String.valueOf(getItem(j).getName().charAt(0)), String.valueOf(mSections.charAt(i))))
                        return j;
                }
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        String[] sections = new String[mSections.length()];
        for (int i = 0; i < mSections.length(); i++)
            sections[i] = String.valueOf(mSections.charAt(i));
        return sections;
    }


    public int getCount(){
        return items.size();
    }

    public void addItem(YammItem item){
        items.add(item);
    }

    public YammItem getItem(int p){
        return items.get(p);
    }

    public long getItemId(int p){
        return items.get(p).getID();
    }
}
