package safisoft.gymnationmembers;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class UserAndPlanDataActivity extends AppCompatActivity {

    TextView  txtv_member_name,txtv_member_plane,txtv_member_startdate,txtv_member_enddate ;
    ImageView imgv_member_profile_img ;

    LinearLayout  lay_member_name,lay_member_plane,lay_member_startdate,lay_member_enddate ;
    String Server_URL ;
    String URL_MEMBER ;
    String URL_PLANS ;
    String URL_SEARCH_MEMBER ;
    DbConnction dbConnction;

    ImageView status ;
    ImageView settings ;
    ImageView home ;
    ImageView workout ;

    boolean response = false ;

    LottieAnimationView gifTextView_conn_status ;

    List<UserAndPlans> PlanList;

    RecyclerView recyclerView;

    TextView txtv_info ;

    LinearLayout linearLayout_activity_control ;
    RelativeLayout main_notification_layout ;
    boolean visb = true ;

    SwipeRefreshLayout pullToRefresh ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_and_plan_data);


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



        Server_URL = getResources().getString(R.string.Server_URL);
        URL_MEMBER = getResources().getString(R.string.Member_conn_php);
        URL_PLANS = "gym_nation_plans_api.php";
        URL_SEARCH_MEMBER = getResources().getString(R.string.Member_search_php);

        recyclerView = findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        txtv_member_name = (TextView)findViewById(R.id.txtv_member_name);
        txtv_member_plane = (TextView)findViewById(R.id.txtv_member_plane);
        txtv_member_startdate = (TextView)findViewById(R.id.txtv_member_startdate);
        txtv_member_enddate = (TextView)findViewById(R.id.txtv_member_enddate);

        imgv_member_profile_img = (ImageView)findViewById(R.id.imgv_member_profile_img);

        txtv_info = (TextView)findViewById(R.id.txtv_info);

        lay_member_name = (LinearLayout)findViewById(R.id.lay_member_name);
        lay_member_plane = (LinearLayout)findViewById(R.id.lay_member_plane);
        lay_member_startdate = (LinearLayout)findViewById(R.id.lay_member_startdate);
        lay_member_enddate = (LinearLayout)findViewById(R.id.lay_member_enddate);

        status = (ImageView)findViewById(R.id.btn_img_status);
        settings = (ImageView)findViewById(R.id.btn_img_settings);
        home = (ImageView)findViewById(R.id.btn_img_home);
        workout = (ImageView)findViewById(R.id.btn_img_workout_history);

        gifTextView_conn_status = (LottieAnimationView) findViewById(R.id.gifTextView_conn_status);

        PlanList = new ArrayList<>();

        linearLayout_activity_control = (LinearLayout)findViewById(R.id.linearLayout_activity_control);
        main_notification_layout = (RelativeLayout)findViewById(R.id.main_notification_layout);

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setColorSchemeColors(Color.parseColor("#DFF0FE"));
        pullToRefresh.setProgressBackgroundColorSchemeColor(Color.parseColor("#98CCFD"));






        SendReceivedData_gym_plans(Server_URL,URL_PLANS,gym_database_url());




        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(UserAndPlanDataActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(UserAndPlanDataActivity.this, MemberAtGymActivity.class);
                    startActivity(intent);
                    finish();

            }
        });



        workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(UserAndPlanDataActivity.this, WorkoutHistoryActivity.class);
                    startActivity(intent);
                    finish();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(UserAndPlanDataActivity.this, AdsPostActivity.class);
                    startActivity(intent);
                    finish();
            }
        });



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




        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PlanList.clear();
                SendReceivedData_gym_plans(Server_URL,URL_PLANS,gym_database_url());
                pullToRefresh.setRefreshing(false);
            }
        });




        no_internet();


 

 
 }




    public void SendReceivedData_gym_plans(final String Server_url,final String php_Search_member_url,final String gym_name) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            String line ;
            @Override
            protected String doInBackground(String... params) {
                String Server_URL = Server_url+php_Search_member_url;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("gymname", gym_name));
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(Server_URL);
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
                if (result != null) {

                    try {
                        JSONArray array = new JSONArray(result);

                        if (array.length() == 0) {
                            recyclerView.setVisibility(View.GONE);
                            gifTextView_conn_status.setVisibility(View.VISIBLE);
                            gifTextView_conn_status.setAnimation(R.raw.ic_no_one_at_the_gym);
                            gifTextView_conn_status.playAnimation();
                            txtv_info.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);

                            gifTextView_conn_status.setVisibility(View.GONE);
                            txtv_info.setVisibility(View.GONE);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject PlanData = array.getJSONObject(array.length() - 1 - i);
                                PlanList.add(new UserAndPlans(
                                        PlanData.getInt("_id"),
                                        PlanData.getString("plan_name"),
                                        PlanData.getString("plan_description"),
                                        PlanData.getString("plan_duration"),
                                        PlanData.getString("plan_price"),
                                        PlanData.getString("plan_available")
                                ));
                            }
                            UserAndplanAdapter adapter = new UserAndplanAdapter(UserAndPlanDataActivity.this, PlanList);
                            recyclerView.setAdapter(adapter);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(gym_name);
    }



    public String gym_database_url(){
        Cursor c = dbConnction.query_user_data("gym_info_local_database",null,null,null,null,null,null);
        c.moveToPosition(0);
        String gym_url = c.getString(3);
        return gym_url ;
    }


    @Override
    public void onBackPressed()
    {

        Intent intent = new Intent(UserAndPlanDataActivity.this, AdsPostActivity.class);
        startActivity(intent);
        finish();

    }


    public void no_internet(){
        int min = 2*6000;
        new CountDownTimer(min, 1000
        ) {
            public void onTick(long millisUntilFinished) {
            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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















}
