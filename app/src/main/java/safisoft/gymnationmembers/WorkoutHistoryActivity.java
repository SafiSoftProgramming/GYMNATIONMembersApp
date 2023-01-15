package safisoft.gymnationmembers;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;

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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WorkoutHistoryActivity extends AppCompatActivity {


    ImageView status ;
    ImageView settings ;
    ImageView home ;
    ImageView workout ;
    ImageView member_and_plan ;
    DbConnction dbConnction;

    String line ;
    RecyclerView recyclerView;
    LottieAnimationView gifTextView_conn_status ;

    List<Workout> workoutList;

    TextView txtv_info ;

    LinearLayout linearLayout_activity_control ;
    RelativeLayout main_notification_layout ;

    SwipeRefreshLayout pullToRefresh ;

    boolean visb = true ;

    String Server_URL ;
    boolean response = false ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);


        Server_URL = getResources().getString(R.string.Server_URL);


        status = (ImageView)findViewById(R.id.btn_img_status);
        settings = (ImageView)findViewById(R.id.btn_img_settings);
        home = (ImageView)findViewById(R.id.btn_img_home);
        workout = (ImageView)findViewById(R.id.btn_img_workout_history);
        member_and_plan = (ImageView)findViewById(R.id.btn_img_member_and_plan);

        txtv_info = (TextView)findViewById(R.id.txtv_info);

        linearLayout_activity_control = (LinearLayout)findViewById(R.id.linearLayout_activity_control);
        main_notification_layout = (RelativeLayout)findViewById(R.id.main_notification_layout);

        recyclerView = findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gifTextView_conn_status = (LottieAnimationView) findViewById(R.id.gifTextView_conn_status);

        workoutList = new ArrayList<>();

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setColorSchemeColors(Color.parseColor("#DFF0FE"));
        pullToRefresh.setProgressBackgroundColorSchemeColor(Color.parseColor("#98CCFD"));


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





        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && visb == true) {
                    main_notification_layout.removeView(linearLayout_activity_control);
                    visb = false ;
                } else if (dy < 0 && visb == false) {
                    if(linearLayout_activity_control.getParent() != null) {
                        ((ViewGroup)linearLayout_activity_control.getParent()).removeView(linearLayout_activity_control); // <- fix
                    }
                    main_notification_layout.addView(linearLayout_activity_control);
                    visb = true ;
                }
            }
        });



        SendReceivedData(gym_database_url(),user_rfid_local_database());











        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(WorkoutHistoryActivity.this,SettingsActivity.class);
                startActivity(intent);
                finish();

            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(WorkoutHistoryActivity.this, AdsPostActivity.class);
                startActivity(intent);
                finish();

            }
        });


        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(WorkoutHistoryActivity.this, MemberAtGymActivity.class);
                startActivity(intent);
                finish();

            }
        });

        member_and_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(WorkoutHistoryActivity.this, UserAndPlanDataActivity.class);
                startActivity(intent);
                finish();

            }
        });




        no_internet();


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                workoutList.clear();
                SendReceivedData(gym_database_url(),user_rfid_local_database());
                pullToRefresh.setRefreshing(false);
            }
        });


    }



    public String gym_database_url(){
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



    public void SendReceivedData(final String gym_name,final String rfid) {

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



                String URL =  Server_URL+"gym_nation_workout_history_api.php";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("rfid", rfid));
                nameValuePairs.add(new BasicNameValuePair("gymname", gym_name));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    BufferedReader rd = new BufferedReader(new InputStreamReader
                            (httpResponse.getEntity().getContent()));

                    line = "";
                    line = rd.readLine();
                    if(!line.equals("")) {
                        response = true;
                    }

                } catch (ClientProtocolException e) {
                } catch (IOException e) {

                }
                return line;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if(result != null) {
                if(result.equals("[]")){
                    try {
                        clearApplicationData();
                        Intent intent = new Intent(getApplicationContext(), AdsPostActivity.class);
                        getApplicationContext().startActivity(intent);
                        getApplicationContext().fileList();
                    }
                    catch (Exception e){
                    }
                }


                    try {
                        JSONArray array = new JSONArray(result);
                        if (array.length() == 0) {
                            recyclerView.setVisibility(View.GONE);
                            gifTextView_conn_status.setVisibility(View.VISIBLE);
                            gifTextView_conn_status.setBackgroundResource(R.drawable.loding_nodata_gif);
                        } else {
                            //if there any data
                            recyclerView.setVisibility(View.VISIBLE);
                            gifTextView_conn_status.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject workout = array.getJSONObject(array.length() - 1 - i);
                                workoutList.add(new Workout(
                                        workout.getInt("_id"),
                                        workout.getString("member_name"),
                                        workout.getString("member_photo"),
                                        workout.getString("workout_one_name"),
                                        workout.getString("workout_two_name"),
                                        workout.getString("start_workout_date"),
                                        workout.getString("start_workout_time"),
                                        workout.getString("end_workout_time"),
                                        workout.getString("workout_time_duration"),
                                        workout.getString("workout_rate"),
                                        workout.getString("workout_sign_out_mode"),
                                        workout.getString("rfid")
                                ));
                            }
                            WorkoutHistoryAdapter workoutHistoryAdapter = new WorkoutHistoryAdapter(WorkoutHistoryActivity.this, workoutList);
                            recyclerView.setAdapter(workoutHistoryAdapter);
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(rfid);
    }


    public void no_internet(){
        int min = 2*6000;
        new CountDownTimer(min, 1000
        ) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                if(!response){
                    recyclerView.setVisibility(View.GONE);
                    gifTextView_conn_status.setVisibility(View.VISIBLE);
                    gifTextView_conn_status.setAnimation(R.raw.ic_no_one_at_the_gym);
                    gifTextView_conn_status.playAnimation();
                    txtv_info.setVisibility(View.VISIBLE);
                }
            }
        }.start();
    }


    @Override
    public void onBackPressed()
    {

        Intent intent = new Intent(WorkoutHistoryActivity.this, AdsPostActivity.class);
        startActivity(intent);
        finish();

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









}
