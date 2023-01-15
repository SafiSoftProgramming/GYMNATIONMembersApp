package safisoft.gymnationmembers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;


public class StartActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    DbConnction dbConnction;
    String Server_URL ;


    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    ImageButton btn_Flash, btn_CameraFlip;
    Barcode QR_CODE;
    int QR_FORMAT;
    boolean flashlight_state = true;
    LinearLayout lay_click_info;
    TextView txt_lay_info;
    SeekBar seekBar;
    ImageButton btn_demo_qrcode ;
    int Camera_Face = CameraSource.CAMERA_FACING_BACK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);





        surfaceView = findViewById(R.id.surfaceView);
        btn_Flash = findViewById(R.id.btn_Flash);
        btn_CameraFlip = findViewById(R.id.btn_CameraFlip);

        btn_demo_qrcode =findViewById(R.id.btn_demo_qrcode);



        seekBar = findViewById(R.id.seekBar);




        btn_Flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (flashlight_state) {
                        flashlight_state = false;
                        btn_Flash.setBackgroundResource(R.drawable.ic_fash_on);
                        cameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        flashlight_state = false;
                    } else {
                        flashlight_state = false;
                        btn_Flash.setBackgroundResource(R.drawable.ic_fash_off);
                        cameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        flashlight_state = true;
                    }
                } catch (Exception e) {
                //    btn_Flash.setBackgroundResource(R.drawable.btn_eff_flash_off);
                    Toast.makeText(StartActivity.this, "Flash is not supported on this device", Toast.LENGTH_SHORT).show();
                    flashlight_state = true;
                }


            }
        });

        btn_CameraFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cameraSource.stop();

                if(cameraSource.getCameraFacing()==CameraSource.CAMERA_FACING_BACK) {
                    cameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                            .setRequestedPreviewSize(1920, 1080)
                            .setFacing(CameraSource.CAMERA_FACING_FRONT)
                            .setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE)
                            .build();
                    btn_Flash.setBackgroundResource(R.drawable.ic_fash_off);
                    flashlight_state = true ;
                }
               else {
                    cameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                            .setRequestedPreviewSize(1920, 1080)
                            .setFacing(CameraSource.CAMERA_FACING_BACK)
                            .setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE)
                            .build();
                    btn_Flash.setBackgroundResource(R.drawable.ic_fash_off);
                    flashlight_state = true ;
                }


                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    Toast.makeText(StartActivity.this, "Not Supported", Toast.LENGTH_SHORT).show();
                }


            }
        });






        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    seekBar.setMax(cameraSource.Get_max_zoome()-1);
                    cameraSource.doZoom(progress);
                }
                catch (Exception e){
                    Toast.makeText(StartActivity.this, "Zoom is not supported on this device", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btn_demo_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendReceivedData(gym_database_url(),"00 00 00 00");

            }
        });

    }


    private void initialiseDetectorsAndSources() {


        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        if(!barcodeDetector.isOperational()){
            Toast.makeText(StartActivity.this, "Make sure Google Play Services is up to date", Toast.LENGTH_SHORT).show();
        }


            cameraSource = new CameraSource.Builder(this, barcodeDetector)
                    .setRequestedPreviewSize(1920, 1080)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE)
                    .build();


        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(StartActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(StartActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    QR_CODE = barcodes.valueAt(0);
                    QR_FORMAT = barcodes.valueAt(0).valueFormat ;

                  System.out.println(QR_CODE.rawValue);

                    SendReceivedData(gym_database_url(),QR_CODE.rawValue);

             //       Intent intent = new Intent(StartActivity.this, AdsPostActivity.class);
             //       intent.putExtra("QR_CODE",QR_CODE );
               //     intent.putExtra("QR_FORMAT",QR_FORMAT);
              //     intent.putExtra("RECORD_ID_FROM_ADAPTER","no");
             //       startActivity(intent);
             //       finish();
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.stop();
        btn_Flash.setBackgroundResource(R.drawable.ic_fash_off);
        flashlight_state = false ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    public String gym_database_url(){
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

        Cursor c = dbConnction.query_user_data("gym_info_local_database",null,null,null,null,null,null);
        c.moveToPosition(0);
        String gym_url = c.getString(3);
        return gym_url ;
    }


    public void SendReceivedData(final String gym_name,final String rfid) {

        Server_URL = getResources().getString(R.string.Server_URL);


        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

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



                String Server_link = Server_URL+"gym_nation_search_member_api.php";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("rfid", rfid));
                nameValuePairs.add(new BasicNameValuePair("gymname", gym_name));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(Server_link);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    BufferedReader rd = new BufferedReader(new InputStreamReader
                            (httpResponse.getEntity().getContent()));

                    String line = "";
                    line = rd.readLine();



                    JSONArray jsonarray = new JSONArray(line);
                    JSONObject obj = jsonarray.getJSONObject(0);
                    String rfid = obj.getString("RFID");

                    String member_name = obj.getString("full_name");
                    String profile_img = obj.getString("image");

                    dbConnction.update_member_data(member_name,profile_img);
                    dbConnction.update_member_rfid_data(rfid);

                    Intent intent = new Intent(StartActivity.this, UserAndPlanDataActivity.class);
                    startActivity(intent);
                    finish();

                } catch (ClientProtocolException e) {
                } catch (IOException e) {

                } catch (JSONException e) {
                    Intent intent = new Intent(StartActivity.this,ScannerNotAcceptedActivity.class);
                    startActivity(intent);
                    finish();
                }
                return "Data Inserted Successfully";
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);


            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(rfid);
    }






    @Override
    public void onBackPressed()
    {

        Intent intent = new Intent(StartActivity.this, GymsChooseActivity.class);
        startActivity(intent);
        finish();

    }







}