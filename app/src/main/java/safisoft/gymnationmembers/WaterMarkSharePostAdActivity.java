package safisoft.gymnationmembers;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.IOException;

public class WaterMarkSharePostAdActivity extends AppCompatActivity {


    ImageView quick_start_cropped_image , btn_pick_img ;
    DbConnction dbConnction;
    ImageButton btn_share_facebook ;
    Bitmap workout_duration ;

    String ADPOST_NAME ;
    String ADPOST_ICON ;
    String ADPOST_IMG ;
    String ADPOST_DESC ;
    String ADPOST_TIME_DATE ;
    String ADPOST_PROMO_CODE ;
    String ADPOST_PROMO_CODE_EXPIRY_DATE ;
    String ADPOST_CONTACT_DETAILS ;
    String Server_URL ;


    Bitmap GYM_LOGO ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_mark_share_post_ad);


        ADPOST_NAME = getIntent().getStringExtra("ADPOST_NAME");
        ADPOST_ICON = getIntent().getStringExtra("ADPOST_ICON");
        ADPOST_IMG = getIntent().getStringExtra("ADPOST_IMG");
        ADPOST_DESC = getIntent().getStringExtra("ADPOST_DESC");
        ADPOST_TIME_DATE = getIntent().getStringExtra("ADPOST_TIME_DATE");
        ADPOST_PROMO_CODE = getIntent().getStringExtra("ADPOST_PROMO_CODE");
        ADPOST_PROMO_CODE_EXPIRY_DATE = getIntent().getStringExtra("ADPOST_PROMO_CODE_EXPIRY_DATE");
        ADPOST_CONTACT_DETAILS = getIntent().getStringExtra("ADPOST_CONTACT_DETAILS");


        Server_URL = getResources().getString(R.string.Server_URL);



        //     if (WORKOUT_ONE_NAME.equals("")){
   //         WORKOUT_ONE_NAME = WORKOUT_TWO_NAME ;
   //         WORKOUT_TWO_NAME = "";
   //     }



        btn_pick_img = (ImageView)findViewById(R.id.btn_pick_img);
        quick_start_cropped_image = (ImageView)findViewById(R.id.quick_start_cropped_image);
        btn_share_facebook= (ImageButton)findViewById(R.id.btn_share_facebook);


        dbConnction = new DbConnction(getApplicationContext());
        try {
            dbConnction.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            dbConnction.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }


        gymlogo_url_to_bitmap(getApplicationContext(),gym_logo());




        show_profile_img_and_name_localdatabase();



        btn_share_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

     SharePhoto photo = new SharePhoto.Builder()
             .setBitmap(workout_duration)
             .build();

     SharePhotoContent content = new SharePhotoContent.Builder()
             .addPhoto(photo)
             .build();


     ShareDialog shareDialog = new ShareDialog(WaterMarkSharePostAdActivity.this);
     shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);


            }
        });




    }


    public static Bitmap addWatermark(Context context, int rw, int rh , Resources res, Bitmap source, double v,int icon) {
        int w , h ;
        Canvas c;
        Paint paint;
        Bitmap bmp, watermark;
        Matrix matrix;
        float scale;
        RectF r;
        w = source.getWidth();
        h = source.getHeight();
        // Create the new bitmap
        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        // Copy the original bitmap into the new one
        c = new Canvas(bmp);
        c.drawBitmap(source, 0, 0, paint);
        // Load the watermark
        watermark = BitmapFactory.decodeResource(res, icon);
        // Scale the watermark to be approximately 40% of the source image height
        scale = (float) (((float) h * v) / (float) watermark.getHeight());
        // Create the matrix
        matrix = new Matrix();
        matrix.postScale(scale, scale);
        // Determine the post-scaled size of the watermark
        r = new RectF(0, 0, watermark.getWidth(), watermark.getHeight());
        matrix.mapRect(r);
        // Move the watermark to the bottom right corner
        //  matrix.postTranslate(w - r.width(), h - r.height());
        matrix.postTranslate(w - rw, h - rh);
        // Draw the watermark
        c.drawBitmap(watermark, matrix, paint);
        // Free up the bitmap memory
        watermark.recycle();
        return bmp;
    }


    public static Bitmap addWatermark_gymlogo(Context context, int rw, int rh , Bitmap source, double v,Bitmap gym_logo) {
        int w , h ;
        Canvas c;
        Paint paint;
        Bitmap bmp, watermark;
        Matrix matrix;
        float scale;
        RectF r;
        w = source.getWidth();
        h = source.getHeight();
        // Create the new bitmap
        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        // Copy the original bitmap into the new one
        c = new Canvas(bmp);
        c.drawBitmap(source, 0, 0, paint);
        // Load the watermark
        watermark = gym_logo;
        // Scale the watermark to be approximately 40% of the source image height
        scale = (float) (((float) h * v) / (float) watermark.getHeight());
        // Create the matrix
        matrix = new Matrix();
        matrix.postScale(scale, scale);
        // Determine the post-scaled size of the watermark
        r = new RectF(0, 0, watermark.getWidth(), watermark.getHeight());
        matrix.mapRect(r);
        // Move the watermark to the bottom right corner
        //  matrix.postTranslate(w - r.width(), h - r.height());
        matrix.postTranslate(w - rw, h - rh);
        // Draw the watermark
        c.drawBitmap(watermark, matrix, paint);
        // Free up the bitmap memory
        watermark.recycle();
        return bmp;
    }



    public static Bitmap text_watermark(Bitmap src, String watermark,int x,int y , int size) {
        int w = 1000;
        int h = 1000;
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFFFFFFF"));
        paint.setAlpha(500);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        canvas.drawText(watermark, x, y, paint);

        return result;
    }



    public void gymlogo_url_to_bitmap(Context context,String gymlogo_url){


        Glide.with(context).asBitmap().
                load(gymlogo_url)
              //  .apply(new RequestOptions().override(1000, 1000))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                        GYM_LOGO = resource ;

                        return true;
                    }
                }).submit();



    }




    public void show_profile_img_and_name_localdatabase(){
        Cursor c = dbConnction.query_user_data("member_data",null,null,null,null,null,null);
        c.moveToPosition(0);

        Glide.with(getApplicationContext()).asBitmap().
                load(Server_URL+ADPOST_IMG)
                .apply(new RequestOptions().override(1000, 1000))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                        resource = Bitmap.createScaledBitmap(resource, 1000, 1000, false); // important  to change any image size to w1000 h1000 after crop .

                        Bitmap green_back_line = addWatermark(getApplicationContext(),1000,300,getResources(),resource,0.30,R.drawable.water_mark_down);

                        Bitmap bitmapdown = addWatermark(getApplicationContext(),1000,1000,getResources(),green_back_line,0.30,R.drawable.trans_line);

                        Bitmap gymnation_logo = addWatermark(getApplicationContext(),998,985,getResources(),bitmapdown,0.12 ,R.drawable.gymnatiionlogo_squr);

                        Bitmap ad_name = text_watermark(gymnation_logo,ADPOST_NAME,20,835,40);

                        Bitmap promo_code = text_watermark(ad_name,"Promo Code: "+ADPOST_PROMO_CODE,20,880,30);

                        Bitmap promo_code_EX_date = text_watermark(promo_code,"Promo Code Expire Date: "+ADPOST_PROMO_CODE_EXPIRY_DATE,20,918,30);

                        workout_duration = text_watermark(promo_code_EX_date,ADPOST_CONTACT_DETAILS,20,960,30);

                        quick_start_cropped_image.setImageBitmap(workout_duration);
                        return true;
                    }
                }).submit();


    }



    public String gym_name(){
        Cursor c = dbConnction.query_user_data("gym_info_local_database",null,null,null,null,null,null);
        c.moveToPosition(0);
        String gym_name = c.getString(1);
        return gym_name ;
    }

    public String gym_logo(){
        Cursor c = dbConnction.query_user_data("gym_info_local_database",null,null,null,null,null,null);
        c.moveToPosition(0);
        String gym_logo = c.getString(2);
        return gym_logo ;
    }







}
