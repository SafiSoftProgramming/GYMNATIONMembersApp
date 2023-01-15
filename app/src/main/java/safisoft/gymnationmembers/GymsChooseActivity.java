package safisoft.gymnationmembers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifTextView;


public class GymsChooseActivity extends AppCompatActivity {


    List<Gyms> Adlist;
    RecyclerView recyclerView;
    ImageView btn_back ;
    String Server_URL ;
    LottieAnimationView gifTextView_conn_status ;
    SwipeRefreshLayout pullToRefresh ;

    TextView txtv_info ;
    RelativeLayout main_notification_layout ;

    boolean visb = true ;

    String URL_GYMLIST ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gymschoose);

        recyclerView = findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        gifTextView_conn_status = (LottieAnimationView) findViewById(R.id.gifTextView_conn_status);


        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setColorSchemeColors(Color.parseColor("#DFF0FE"));
        pullToRefresh.setProgressBackgroundColorSchemeColor(Color.parseColor("#98CCFD"));


        main_notification_layout = (RelativeLayout)findViewById(R.id.main_notification_layout);

        txtv_info = (TextView)findViewById(R.id.txtv_info);

        Server_URL = getResources().getString(R.string.Server_URL);
        URL_GYMLIST = getResources().getString(R.string.Gym_list_choose_php);
        //initializing the productlist
        Adlist = new ArrayList<>();

        loadgymlist();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }
        });




        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Adlist.clear();
                loadgymlist();
                pullToRefresh.setRefreshing(false);
            }
        });





    }



    private void loadgymlist() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Server_URL+ URL_GYMLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response != null) {
                            try {
                                JSONArray array = new JSONArray(response);

                                if (array.length() == 0) {

                                    recyclerView.setVisibility(View.GONE);
                                    gifTextView_conn_status.setVisibility(View.VISIBLE);
                                    gifTextView_conn_status.setAnimation(R.raw.ic_no_one_at_the_gym);
                                    gifTextView_conn_status.playAnimation();
                                    txtv_info.setVisibility(View.VISIBLE);
                                } else {
                                    //if there any data
                                    recyclerView.setVisibility(View.VISIBLE);
                                    gifTextView_conn_status.setVisibility(View.GONE);
                                    txtv_info.setVisibility(View.GONE);


                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject AdPostData = array.getJSONObject(array.length() - 1 - i);
                                        Adlist.add(new Gyms(
                                                AdPostData.getInt("_id"),
                                                AdPostData.getString("gym_name"),
                                                AdPostData.getString("gym_logo"),
                                                AdPostData.getString("gym_database_url"),
                                                AdPostData.getString("gym_address")

                                        ));
                                    }
                                    safisoft.gymnationmembers.GymsChooseAdapter GymsChooseAdapter = new GymsChooseAdapter(GymsChooseActivity.this, Adlist);
                                    recyclerView.setAdapter(GymsChooseAdapter);


                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {



                        if (error instanceof TimeoutError) {
                            gifTextView_conn_status.setVisibility(View.VISIBLE);
                            gifTextView_conn_status.setAnimation(R.raw.ic_no_one_at_the_gym);
                            gifTextView_conn_status.playAnimation();
                            txtv_info.setVisibility(View.VISIBLE);
                        }

                        else if (error instanceof ServerError) {
                            gifTextView_conn_status.setVisibility(View.VISIBLE);
                            gifTextView_conn_status.setAnimation(R.raw.ic_no_one_at_the_gym);
                            gifTextView_conn_status.playAnimation();
                            txtv_info.setVisibility(View.VISIBLE);
                        }

                        else if (error instanceof NetworkError) {
                            gifTextView_conn_status.setVisibility(View.VISIBLE);
                            gifTextView_conn_status.setAnimation(R.raw.ic_no_one_at_the_gym);
                            gifTextView_conn_status.playAnimation();
                            txtv_info.setVisibility(View.VISIBLE);
                        }

                        else if (error instanceof ParseError) {
                            gifTextView_conn_status.setVisibility(View.VISIBLE);
                            gifTextView_conn_status.setAnimation(R.raw.ic_no_one_at_the_gym);
                            gifTextView_conn_status.playAnimation();
                            txtv_info.setVisibility(View.VISIBLE);
                        }

                        else {
                            gifTextView_conn_status.setVisibility(View.VISIBLE);
                            gifTextView_conn_status.setAnimation(R.raw.ic_no_one_at_the_gym);
                            gifTextView_conn_status.playAnimation();
                            txtv_info.setVisibility(View.VISIBLE);
                        }



                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }





    /**
     * Back button listener.
     * Will close the application if the back button pressed twice.
     */
                @Override
                public void onBackPressed()
                {

                    Intent intent = new Intent(GymsChooseActivity.this, AdsPostActivity.class);
                    startActivity(intent);
                    finish();

                }







}
