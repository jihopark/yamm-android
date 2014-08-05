package com.teamyamm.yamm.app;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by parkjiho on 6/19/14.
 */
public class YammImageView extends FrameLayout {

    private final static String exampleURL = "http://t1.gstatic.com/images?q=tbn:ANd9GcTPwjgglBpW0--98JFE6PyoQt1YhSRUMzBEEsIQhJlLuQkkTlHwkQ";
    private final static String imageURL = "http://res.cloudinary.com/yamm/image/upload/";
    public final static String DISH = "dish";
    public final static String GRID = "grid";

    private final static float imageRatio = 1.5f;
    private final static float progressCircleRatio = 0.2f;

    private String path;
    private int width, height;
    private int id;

    private Context context;
    private ImageView image;
    private ProgressBar progressCircle;


    /*
    * When added in xml
    * */

    public YammImageView(Context context, AttributeSet attrs){
        super(context, attrs);

        Log.i("YammImageView/constructor", "Yamm ImageView XML Constructor");


        width = 100;
        height = 100;

        this.context = context;

        this.setBackgroundColor(Color.GRAY);
        image = new ImageView(context);
        image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(image);

        progressCircle = new ProgressBar(context);
        FrameLayout.LayoutParams params = new LayoutParams((int) (width*progressCircleRatio),(int) (height*progressCircleRatio));
        params.gravity = Gravity.CENTER;
        progressCircle.setLayoutParams(params);
        progressCircle.setVisibility(View.VISIBLE);
        addView(progressCircle);
    }

    /*
    * When added dynamically
    * */

    public YammImageView(Context context, String path, int width, int height, int id){
        super(context);

        Log.i("YammImageView/constructor", "Yamm ImageView Constructor Dynamic");

        this.context = context;
        this.path = path;
        this.id = id;
        this.width = width;
        this.height = height;

        this.setBackgroundColor(Color.GRAY);
        image = new ImageView(context);
        image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(image);

        progressCircle = new ProgressBar(context);
        FrameLayout.LayoutParams params = new LayoutParams((int) (width*progressCircleRatio),(int) (height*progressCircleRatio));
        params.gravity = Gravity.CENTER;
        progressCircle.setLayoutParams(params);
        progressCircle.setVisibility(View.VISIBLE);
        addView(progressCircle);

        if (width!=0 && height!=0)
            loadImage();
    }

    public void setDishImage(String name){
/*
        //should be deleted
        name = "샘플";

        //encode korean into url format
        try {
            name = URLEncoder.encode(name, "UTF-8");
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
            Log.e("YammImageView/setDishImage","URL Encoding Error " + name);
        }

        String url = imageURL + "dish/" + name + ".jpg";



        Log.i("YammImageView/loadImage", "Loading Image " + url);
        Picasso.with(context)
                .load(url)
                .error(R.drawable.error_placeholder)
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        setBackgroundColor(Color.TRANSPARENT);
                        progressCircle.setVisibility(GONE);
                    }

                    @Override
                    public void onError() {
                        Log.e("YammImageView/loadImage", "Image Loading Error ");
                    }
                });*/
        image.setImageDrawable(getResources().getDrawable(R.drawable.example));
    }

    public void loadImage(){
        Log.i("YammImageView/loadImage", "Loading Image");
        Picasso.with(context)
                .load(getURL())
                .error(R.drawable.error_placeholder)
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        setBackgroundColor(Color.TRANSPARENT);
                        Log.i("YammImageView/loadImage", "Successfully loaded " + path + " "  + id);
                        progressCircle.setVisibility(GONE);
                    }

                    @Override
                    public void onError() {
                        Log.e("YammImageView/loadImage", "Image Loading Error " + path + " " + id);
                    }
                });
    }

    public void setDimension(int w, int h){
        width = w;
        height = h;
    }

    public String getURL(){
        if (path == DISH){

        }
        return exampleURL;
        //return imageURL + "/dish/" + id + "/c" + (int)(width/imageRatio) + "x" + (int)(height/imageRatio);
    }

    public void setID(int id){
        this.id = id;
    }

    public ImageView getImageView(){
        return image;
    }

}
