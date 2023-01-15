package safisoft.gymnationmembers;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;


public class SettingsActivity extends AppCompatActivity {

    String Server_URL ;

    ImageView status ;
    ImageView settings ;
    ImageView home ;
    ImageView workout ;
    ImageView btn_pick_img ;
    ImageView member_and_plan ;
    ImageButton btn_sign_out ;
    TextView txtv_member_name ;
    CheckBox checkBox_newad , checkBox_member_atgym , checkBox_massage , checkBox_workout ;
    String URL ;
    String URL_update_profile_img ;
    String encodedImage ;


    DbConnction dbConnction;
    ImageView quick_start_cropped_image ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Server_URL = getResources().getString(R.string.Server_URL);

        URL = Server_URL+"members_profile_imgs/gym_nation_upload_member_profile_img_api.php";
        URL_update_profile_img = Server_URL+"gym_nation_update_member_profile_img_url.php";


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



        status = (ImageView)findViewById(R.id.btn_img_status);
        settings = (ImageView)findViewById(R.id.btn_img_settings);
        home = (ImageView)findViewById(R.id.btn_img_home);
        workout = (ImageView)findViewById(R.id.btn_img_workout_history);
        member_and_plan = (ImageView)findViewById(R.id.btn_img_member_and_plan);
        btn_pick_img = (ImageView)findViewById(R.id.btn_pick_img);
        quick_start_cropped_image = (ImageView)findViewById(R.id.quick_start_cropped_image);
        btn_sign_out = (ImageButton)findViewById(R.id.btn_sign_out);
        checkBox_newad = (CheckBox)findViewById(R.id.checkBox_newad);
        checkBox_member_atgym = (CheckBox)findViewById(R.id.checkBox_member_atgym);
        checkBox_massage = (CheckBox)findViewById(R.id.checkBox_massage);
        checkBox_workout = (CheckBox)findViewById(R.id.checkBox_workout);
        txtv_member_name = (TextView)findViewById(R.id.txtv_member_name);


        check_box_statu_at_Startactivity();

        show_profile_img_and_name_localdatabase();


        btn_pick_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SettingsActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("Crop Image")
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1,1)
                        .setCropMenuCropButtonTitle("Done")
                        .setRequestedSize(1000, 1000)
                        .start(SettingsActivity.this);
            }
        });


        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SettingsActivity.this, MemberAtGymActivity.class);
                startActivity(intent);
                finish();

            }
        });



        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SettingsActivity.this, AdsPostActivity.class);
                startActivity(intent);
                finish();

            }
        });

        workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SettingsActivity.this, WorkoutHistoryActivity.class);
                startActivity(intent);
                finish();

            }
        });

        member_and_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SettingsActivity.this, UserAndPlanDataActivity.class);
                startActivity(intent);
                finish();

            }
        });



        checkBox_newad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    dbConnction.update_notification_statu("ad_notification","true");
                }
                else {
                    dbConnction.update_notification_statu("ad_notification","false");
                }
            }
        });

        checkBox_member_atgym.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    dbConnction.update_notification_statu("enter_gym_notification","true");
                }
                else {
                    dbConnction.update_notification_statu("enter_gym_notification","false");
                }
            }
        });

        checkBox_massage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    dbConnction.update_notification_statu("massage_notification","true");
                }
                else {
                    dbConnction.update_notification_statu("massage_notification","false");
                }
            }
        });

        checkBox_workout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    dbConnction.update_notification_statu("workout_notification","true");
                }
                else {
                    dbConnction.update_notification_statu("workout_notification","false");
                }
            }
        });



        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearApplicationData();
                Intent intent = new Intent(SettingsActivity.this, AdsPostActivity.class);
                startActivity(intent);
                fileList();
                Toast.makeText(SettingsActivity.this, "logged out", Toast.LENGTH_SHORT).show();

            }
        });






    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                String img_path = result.getUri().getPath() ;

                Cursor c = dbConnction.query_user_data("member_data",null,null,null,null,null,null);
                c.moveToPosition(0);
                String user_name = c.getString(1);

                if (!user_name.equals("Mr. Demo")) {
                    upload(img_path);
                }
                else {
                    Toast.makeText(getApplicationContext(),"The user profile picture cannot be changed in the DEMO MODE.",Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void upload(String img_path) {
        // Image location URL
        Log.e("path", "----------------" + img_path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 0;
        options.inPurgeable = true;
        Bitmap bm = BitmapFactory.decodeFile(img_path,options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,30,baos);
        // bitmap object

       // bm = Bitmap.createScaledBitmap(bm, 1000, 1000, false);


        byte[] byteImage_photo = baos.toByteArray();
        //generate base64 string of image
        encodedImage = Base64.encodeToString(byteImage_photo,Base64.DEFAULT);
        // Upload image to server
        new uploadToServer().execute();
    }

    public class uploadToServer extends AsyncTask<Void, Void, String> {
        private ProgressDialog pd = new ProgressDialog(SettingsActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Wait image uploading!");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            String rfid = user_rfid_local_database();
            String gymname = gym_name().replaceAll("\\s+","");
            String img_name =gymname+rfid.replaceAll("\\s+","");
            img_name.replaceAll("\\s+","");


            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("base64", encodedImage));
            nameValuePairs.add(new BasicNameValuePair("ImageName", img_name + ".jpg"));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                String st = EntityUtils.toString(response.getEntity());
                Log.v("log_tag", "In the try Loop" + st);

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }


            String new_img_url = "members_profile_imgs/"+img_name + ".jpg";

            ArrayList<NameValuePair> update_memper_profile_img = new ArrayList<NameValuePair>();
            update_memper_profile_img.add(new BasicNameValuePair("gymname", gym_database_name()));
            update_memper_profile_img.add(new BasicNameValuePair("rfid",rfid ));
            update_memper_profile_img.add(new BasicNameValuePair("profile_img_new_url",new_img_url ));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL_update_profile_img);
                httppost.setEntity(new UrlEncodedFormEntity(update_memper_profile_img));
                HttpResponse response = httpclient.execute(httppost);
                String st = EntityUtils.toString(response.getEntity());
                Log.v("log_tag", "In the try Loop" + st);

                dbConnction.update_member_data(get_member_name_localdatabase(),new_img_url);

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return "Success";
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
        }
    }

    @Override
    public void onResume(){
        super.onResume();


    }

    public String gym_name(){
        Cursor c = dbConnction.query_user_data("gym_info_local_database",null,null,null,null,null,null);
        c.moveToPosition(0);
        String gym_url = c.getString(1);
        return gym_url ;
    }

    public String gym_database_name(){
        Cursor c = dbConnction.query_user_data("gym_info_local_database",null,null,null,null,null,null);
        c.moveToPosition(0);
        String gym_url = c.getString(3);
        return gym_url ;
    }

    public String user_rfid_local_database(){
        Cursor c = dbConnction.query_user_data("member_rfid_local_database",null,null,null,null,null,null);
        c.moveToPosition(0);
        String rfid = c.getString(1);
        return rfid ;
    }


    public void show_profile_img_and_name_localdatabase(){
        Cursor c = dbConnction.query_user_data("member_data",null,null,null,null,null,null);
        c.moveToPosition(0);
        txtv_member_name.setText(c.getString(1));


        Glide.with(getApplicationContext())

                .load(Server_URL+c.getString(2))
                .apply(centerCropTransform()
                        .placeholder(R.drawable.gymnatiionlogo_squr_defult_err)
                        .error(R.drawable.gymnatiionlogo_squr_defult_err)
                        .priority(Priority.HIGH))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(quick_start_cropped_image);
    }




    public String get_member_name_localdatabase(){
        Cursor c = dbConnction.query_user_data("member_data",null,null,null,null,null,null);
        c.moveToPosition(0);
        return c.getString(1);
    }






    public void check_box_statu_at_Startactivity(){
        Cursor c = dbConnction.query_user_data("notification_check",null,null,null,null,null,null);
        c.moveToPosition(0);
        if(c.getString(1).equals("true")){checkBox_newad.setChecked(true);}else{checkBox_newad.setChecked(false);}
        if(c.getString(2).equals("true")){checkBox_member_atgym.setChecked(true);}else{checkBox_member_atgym.setChecked(false);}
        if(c.getString(3).equals("true")){checkBox_massage.setChecked(true);}else{checkBox_massage.setChecked(false);}
        if(c.getString(4).equals("true")){checkBox_workout.setChecked(true);}else{checkBox_workout.setChecked(false);}
    }



    public void clearApplicationData() {
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }


    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }


    @Override
    public void onBackPressed()
    {

        Intent intent = new Intent(SettingsActivity.this, AdsPostActivity.class);
        startActivity(intent);
        finish();

    }








}
