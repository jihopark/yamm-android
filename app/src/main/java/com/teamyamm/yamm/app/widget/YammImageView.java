package com.teamyamm.yamm.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;
import com.teamyamm.yamm.app.R;
import com.teamyamm.yamm.app.network.VolleyController;

/**
 * Created by parkjiho on 6/19/14.
 */
public class YammImageView extends FrameLayout {
    private static Picasso picasso = null;

    private final static String imageURL = "http://res.cloudinary.com/yamm-img/image/upload/";
    public final static String DISH = "dish";
    public final static String MAIN = "main";
    public final static String BATTLE = "battle";
    public final static String GROUP = "group";

    public final static String GRID = "grid";

    private final static float imageRatio = 1.5f;
    private final static float progressCircleRatio = 0.2f;
    private static boolean skipCache = false;

    public String path = "";
    private int width = 0, height = 0;
    private long id = 0;

    private Context context;
    private YammNetworkImageView image;
    private ProgressBar progressCircle;

    private ImageLoader imageLoader;


    /*
    * When added in xml
    * */

    public YammImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.i("YammImageView/constructor", "Yamm ImageView XML Constructor");


        this.context = context;

        this.setBackgroundColor(Color.GRAY);

        measureDynamicDimension();

        progressCircle = new ProgressBar(context);
        FrameLayout.LayoutParams params = new LayoutParams((int) getResources().getDimension(R.dimen.image_progress_circle_radius),
                (int) getResources().getDimension(R.dimen.image_progress_circle_radius));
        params.gravity = Gravity.CENTER;
        progressCircle.setLayoutParams(params);
        progressCircle.setVisibility(View.VISIBLE);
        addView(progressCircle);
    }

    /*
    * When added dynamically
    * */

    public YammImageView(Context context, String path, int width, int height, int id) {
        super(context);

        Log.i("YammImageView/constructor", "Yamm ImageView Constructor Dynamic");

        this.context = context;
        this.path = path;
        this.id = id;
        this.width = width;
        this.height = height;

        this.setBackgroundColor(Color.GRAY);

        progressCircle = new ProgressBar(context);
        FrameLayout.LayoutParams params = new LayoutParams((int) getResources().getDimension(R.dimen.image_progress_circle_radius),
                (int) getResources().getDimension(R.dimen.image_progress_circle_radius));
        params.gravity = Gravity.CENTER;
        progressCircle.setLayoutParams(params);
        progressCircle.setVisibility(View.VISIBLE);
        addView(progressCircle);

        if (width != 0 && height != 0)
            setID(id);
    }

    private void setVolley(){
        VolleyController.setVolleyController(context);
        imageLoader = VolleyController.getImageLoader();
    }

    private void setImageView(YammNetworkImageView v){
        setVolley();
        image = v;
        image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        image.setScaleType(ImageView.ScaleType.FIT_XY);

        addView(image);
    }


    public ImageView getImageView(){
        return image;
    }


    public void setID(long id){
        this.id = id;
    }

    public void setPath(String path){
        this.path = path;
    }

    public void setDimension(int w, int h){
        width = w;
        height = h;
    }

    public int getImageHeight(){return height; }

    public int getImageWidth(){return width;}

    public static String getURL(String path, int width, int height, long id){
        if (path.equals(BATTLE))
            return imageURL + "dish/battle/" + getNumberFormat(id) + ".jpg";
        if (path.equals(DISH))
            return imageURL + "w_" + width +",h_" + height + ",c_crop,g_center/dish/" + getNumberFormat(id) + ".jpg";
        if (path.equals(MAIN) || path.equals(GROUP))
            return imageURL + "w_" + width +",h_" + height + ",c_crop,g_south/dish/" + getNumberFormat(id) + ".jpg";
        return "";
        //return imageURL + "/dish/" + id + "/c" + (int)(width/imageRatio) + "x" + (int)(height/imageRatio);
    }

    private static String getNumberFormat(long n){
        if (n < 10)
            return "00"+n;
        if (n < 100 && n>=10)
            return "0"+n;
        return n+"";
    }

    public void loadImage(){
        if (width!=0 && height!=0 && id!=0 && !path.isEmpty() || (path.equals(BATTLE) && id!=0)) {
            final String url = getURL(path, width, height, id);
           /* if (path.equals(BATTLE)) {
                try {
                    setImageView(new ImageView(context));

                    Log.d("YammImageView/loadImage","Image Loading from Picasso " + getURL(path, width, height, id));

                    if (picasso == null)
                        picasso = Picasso.with(context);
                    if (BaseActivity.CURRENT_APPLICATION_STATUS.equals(BaseActivity.TESTING)) {
                        picasso.setLoggingEnabled(true);
                        picasso.setIndicatorsEnabled(true);
                    }

                    RequestCreator creator = picasso.load(url);
                    creator.skipMemoryCache();
                    creator.error(new ColorDrawable(getResources().getColor(R.color.brown_color)));
                    creator.fit();
                    creator.into(image,new Callback() {
                        @Override
                        public void onSuccess() {
                            setBackgroundColor(Color.TRANSPARENT);
                            Log.i("YammImageView/loadImage", "Successfully loaded " + url);
                            Log.d("YammImageView/loadImage", picasso.getSnapshot().toString());

                        }

                        @Override
                        public void onError() {
                            Log.e("YammImageView/loadImage", "Image Loading Error " + url);
                            Log.e("YammImageView/loadImage", picasso.getSnapshot().toString());
                            WTFExceptionHandler.sendLogToServer(context, "**Image Error Log**\n" + picasso.getSnapshot().toString());
                        }x
                    });
                } catch (OutOfMemoryError e) {
                    Log.e("YammImageView/loadImage", "Out of Memory Error Caught. Skipping Cache");
                    e.printStackTrace();
                    skipCache = true;
                    loadImage();
                }
                return ;
            }*/
            setImageView(new YammNetworkImageView(context, progressCircle));

            if (path.equals(GROUP) || path.equals(MAIN)){
                image.setErrorImageResId(R.drawable.image_notfound);
                image.setDefaultImageResId(R.drawable.image_loading);
            }
            image.setImageUrl(url, imageLoader);


            Log.d("YammImageView/loadImage","Image Loading from Volley " + getURL(path, width, height, id));

        }
        else{
            Log.e("YammImageView/loadImage","Image not Ready");
        }
    }

    public String getImageURL(){
        return getURL(path, width, height, id);
    }

    private void measureDynamicDimension(){
        final YammImageView div = this;
        ViewTreeObserver vto = YammImageView.this.getViewTreeObserver();
        try {
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (div.width == 0 && div.height == 0) {
                        div.setDimension(YammImageView.this.getMeasuredWidth(), YammImageView.this.getMeasuredHeight());
                        if (!div.path.equals(BATTLE))
                            div.loadImage();

                        Log.i("YammImageView/onPreDraw", "Width " + div.width + " Height " + div.height);


                        YammImageView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;

                }
            });
        }catch (NullPointerException e){
            Log.e("YammImageView/onPreDrawListener","Nullpointer in PreDraw");
            e.printStackTrace();
        }
    }
}
