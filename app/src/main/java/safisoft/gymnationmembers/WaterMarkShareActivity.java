package safisoft.gymnationmembers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WaterMarkShareActivity extends AppCompatActivity {


    ImageView quick_start_cropped_image , btn_pick_img ;
    DbConnction dbConnction;
    ImageButton btn_share_facebook ;
    Bitmap workout_duration ;

    String WORKOUT_DATE ;
    String WORKOUT_DURATION ;
    String WORKOUT_ONE_NAME ;
    String WORKOUT_TWO_NAME ;

    Bitmap GYM_LOGO ;
    String Server_URL ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_mark_share);


        WORKOUT_DATE = getIntent().getStringExtra("WORKOUT_DATE");
        WORKOUT_DURATION = getIntent().getStringExtra("WORKOUT_DURATION");
        WORKOUT_ONE_NAME = getIntent().getStringExtra("WORKOUT_ONE_NAME");
        WORKOUT_TWO_NAME = getIntent().getStringExtra("WORKOUT_TOW_NAME");
        Server_URL = getResources().getString(R.string.Server_URL);



        if (WORKOUT_ONE_NAME.equals("")){
            WORKOUT_ONE_NAME = WORKOUT_TWO_NAME ;
            WORKOUT_TWO_NAME = "";
        }



        btn_pick_img = (ImageView)findViewById(R.id.btn_pick_img);
        quick_start_cropped_image = (ImageView)findViewById(R.id.quick_start_cropped_image);
        btn_share_facebook= (ImageButton) findViewById(R.id.btn_share_facebook);


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


        btn_pick_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(WaterMarkShareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(WaterMarkShareActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(WaterMarkShareActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("Crop Image")
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(1,1)
                        .setCropMenuCropButtonTitle("Done")
                        .setRequestedSize(1000, 1000)
                        .start(WaterMarkShareActivity.this);
            }
        });




        btn_share_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

     SharePhoto photo = new SharePhoto.Builder()
             .setBitmap(workout_duration)
             .build();

     SharePhotoContent content = new SharePhotoContent.Builder()
             .addPhoto(photo)
             .build();


     ShareDialog shareDialog = new ShareDialog(WaterMarkShareActivity.this);
     shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);






            }
        });




    }










    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
             //   ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                String img_path = result.getUri().getPath() ;
                upload(img_path);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }



    private void upload(String img_path) {
        // Image location URL
        Log.e("path", "----------------" + img_path);
     //   BitmapFactory.Options options = new BitmapFactory.Options();
     //   options.inSampleSize = 0;
      //  options.inPurgeable = true;
        Bitmap bm = BitmapFactory.decodeFile(img_path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        // bitmap object

        bm = Bitmap.createScaledBitmap(bm, 1000, 1000, false); // important  to change any image size to w1000 h1000 after crop .

        Bitmap green_back_line = addWatermark(getApplicationContext(),1000,300,getResources(),bm,0.30,R.drawable.water_mark_down);

        Bitmap bitmapdown = addWatermark(getApplicationContext(),1000,1000,getResources(),green_back_line,0.30,R.drawable.trans_line);

        Bitmap workout_tow = addWatermark(getApplicationContext(),330,180,getResources(),bitmapdown,0.15,WorkOutTwoImg());

        Bitmap workout_one = addWatermark(getApplicationContext(),170,180,getResources(),workout_tow,0.15 ,WorkOutOneImg());

        Bitmap gymnation_logo = addWatermark(getApplicationContext(),998,985,getResources(),workout_one,0.12 ,R.drawable.gymnatiionlogo_squr);

        //   Bitmap gym_logo = addWatermark_gymlogo(getApplicationContext(),150,980,gymnation_logo,0.12 ,GYM_LOGO);

        Bitmap gym_name = text_watermark(gymnation_logo,gym_name(),20,875,40);

        Bitmap workout_date = text_watermark(gym_name,WORKOUT_DATE,20,920,30);

        workout_duration = text_watermark(workout_date,"Duration "+WORKOUT_DURATION,20,960,30);


        ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageBitmap(workout_duration);



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
                load(Server_URL+c.getString(2))
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

                        Bitmap workout_tow = addWatermark(getApplicationContext(),330,180,getResources(),bitmapdown,0.15,WorkOutTwoImg());

                        Bitmap workout_one = addWatermark(getApplicationContext(),170,180,getResources(),workout_tow,0.15 ,WorkOutOneImg());

                        Bitmap gymnation_logo = addWatermark(getApplicationContext(),998,985,getResources(),workout_one,0.12 ,R.drawable.gymnatiionlogo_squr);

                     //   Bitmap gym_logo = addWatermark_gymlogo(getApplicationContext(),150,980,gymnation_logo,0.12 ,GYM_LOGO);

                        Bitmap gym_name = text_watermark(gymnation_logo,gym_name(),20,875,40);

                        Bitmap workout_date = text_watermark(gym_name,WORKOUT_DATE,20,920,30);

                        workout_duration = text_watermark(workout_date,"Duration "+WORKOUT_DURATION,20,960,30);

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


    public int WorkOutOneImg(){

        int Workout = 0 ;

        switch (WORKOUT_ONE_NAME) {

            case "Abs" :
                Workout = R.drawable.w_abs ;
                break;

            case "Arm" :
                Workout = R.drawable.w_arm ;
                break;

            case "Back" :
                Workout = R.drawable.w_back ;
                break;

            case "Cardio" :
                Workout = R.drawable.w_cardio ;
                break;

            case "Chest" :
                Workout = R.drawable.w_chest ;
                break;

            case "Crossfit" :
                Workout = R.drawable.w_crossfit ;
                break;

            case "Leg" :
                Workout = R.drawable.w_leg ;
                break;

            case "Shoulders" :
                Workout = R.drawable.w_shoulders ;
                break;

            case "" :
                Workout = R.drawable.w_null ;
                break;

        }
        return Workout ;
    }


    public int WorkOutTwoImg(){

        int Workout = 0 ;

        switch (WORKOUT_TWO_NAME) {

            case "Abs" :
                Workout = R.drawable.w_abs ;
                break;

            case "Arm" :
                Workout = R.drawable.w_arm ;
                break;

            case "Back" :
                Workout = R.drawable.w_back ;
                break;

            case "Cardio" :
                Workout = R.drawable.w_cardio ;
                break;

            case "Chest" :
                Workout = R.drawable.w_chest ;
                break;

            case "Crossfit" :
                Workout = R.drawable.w_crossfit ;
                break;

            case "Leg" :
                Workout = R.drawable.w_leg ;
                break;

            case "Shoulders" :
                Workout = R.drawable.w_shoulders ;
                break;

            case "" :
                Workout = R.drawable.w_null ;
                break;

        }
        return Workout ;
    }





}
