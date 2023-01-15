package safisoft.gymnationmembers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

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

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;
import static safisoft.gymnationmembers.SettingsActivity.deleteFile;

/**
 * Created by Belal on 10/18/2017.
 */

public class UserAndplanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String ServerURL ;
    String WorkOut_Img_Folder_Name ;
    String WorkOut_Img_Type ;
    private Context mCtx;
    private List<UserAndPlans> plansList;
    DbConnction dbConnction;


    String Server_URL ;
    String URL_MEMBER ;
    String URL_PLANS ;
    String URL_SEARCH_MEMBER ;





    class PlansViewHolder extends RecyclerView.ViewHolder {


        ImageView imgv_plan_lock ;
        TextView txtv_plan_name ;
        TextView txtv_plan_desc ;
        TextView txtv_plan_month ;
        TextView txtv_plan_days ;
        TextView txtv_plan_price ;

        public PlansViewHolder(View itemView) {
            super(itemView);

            imgv_plan_lock = itemView.findViewById(R.id.imgv_plan_lock);
            txtv_plan_name = itemView.findViewById(R.id.txtv_plan_name);
            txtv_plan_desc = itemView.findViewById(R.id.txtv_plan_desc);
            txtv_plan_month = itemView.findViewById(R.id.txtv_plan_month);
            txtv_plan_days = itemView.findViewById(R.id.txtv_plan_days);
            txtv_plan_price = itemView.findViewById(R.id.txtv_plan_price);

        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        ImageView imgv_member_profile_img,imgv_gym_logo ;
        TextView txtv_member_name;
        TextView txtv_gym_name ;
        TextView txtv_member_plane;
        TextView txtv_member_startdate ;
        TextView txtv_member_enddate ;
        LinearLayout lay_member_name,lay_member_plane,lay_member_startdate,lay_member_enddate,lay_gym_name ;


        public HeaderViewHolder(View itemView) {
            super(itemView);

             imgv_member_profile_img = itemView.findViewById(R.id.imgv_member_profile_img);
             txtv_member_name = itemView.findViewById(R.id.txtv_member_name);
             txtv_gym_name = itemView.findViewById(R.id.txtv_gym_name);
             txtv_member_plane = itemView.findViewById(R.id.txtv_member_plane);
             txtv_member_startdate = itemView.findViewById(R.id.txtv_member_startdate);
             txtv_member_enddate = itemView.findViewById(R.id.txtv_member_enddate);
            lay_member_name = itemView.findViewById(R.id.lay_member_name);
            lay_gym_name= itemView.findViewById(R.id.lay_gym_name);
            lay_member_plane = itemView.findViewById(R.id.lay_member_plane);
            lay_member_startdate = itemView.findViewById(R.id.lay_member_startdate);
            lay_member_enddate = itemView.findViewById(R.id.lay_member_enddate);
            imgv_gym_logo = itemView.findViewById(R.id.imgv_gym_logo);


        }
    }

    public UserAndplanAdapter(Context mCtx, List<UserAndPlans> plansList) {
        this.mCtx = mCtx;
        this.plansList = plansList;
    }



    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return 0;
            case 1:
                return 1;
            default:
                return -1;
        }
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ServerURL = mCtx.getResources().getString(R.string.Server_URL);
        WorkOut_Img_Folder_Name = mCtx.getResources().getString(R.string.WorkOut_Img_Folder_Name);
        WorkOut_Img_Type =mCtx.getResources().getString(R.string.WorkOut_Img_Type);

        Server_URL = mCtx.getResources().getString(R.string.Server_URL);
        URL_MEMBER = mCtx.getResources().getString(R.string.Member_conn_php);
        URL_PLANS = "gym_nation_plans_api.php";
        URL_SEARCH_MEMBER = mCtx.getResources().getString(R.string.Member_search_php);


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




        View view;
        if(viewType == 0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_and_plan_header, parent, false);
            return new HeaderViewHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_list, parent, false);
            return new PlansViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {





        if(position == 0){


            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                String line ;
                @Override
                protected String doInBackground(String... params) {
                    String Server_URL_link = Server_URL+URL_SEARCH_MEMBER;
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("rfid", user_rfid_local_database()));
                    nameValuePairs.add(new BasicNameValuePair("gymname", gym_database_url()));
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(Server_URL_link);
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse httpResponse = httpClient.execute(httpPost);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        BufferedReader rd = new BufferedReader(new InputStreamReader
                                (httpResponse.getEntity().getContent()));
                        line = "";
                        line = rd.readLine();


                    } catch (ClientProtocolException e) {
                    } catch (IOException e) {
                    }
                    return line;
                }
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
///////////////////
                    if(result.equals("[]")){
                        try {
                            clearApplicationData();
                            Intent intent = new Intent(mCtx, AdsPostActivity.class);
                            mCtx.startActivity(intent);
                            mCtx.fileList();
                        }
                        catch (Exception e){
                        }
                    }

                    if (result != null) {

                        JSONArray jsonarray = null;
                        try {
                            jsonarray = new JSONArray(result);
                            JSONObject obj = jsonarray.getJSONObject(0);
                            ((HeaderViewHolder) holder).txtv_member_name.setText(obj.getString("full_name"));
                            ((HeaderViewHolder) holder).txtv_gym_name.setText(gym_database_name());
                            ((HeaderViewHolder) holder).txtv_member_plane.setText(obj.getString("plane"));
                            ((HeaderViewHolder) holder).txtv_member_startdate.setText(obj.getString("start_date"));
                            ((HeaderViewHolder) holder).txtv_member_enddate.setText(obj.getString("end_date"));
                            Glide.with(getApplicationContext())
                                    .load(Server_URL+obj.get("image"))
                                    .apply(centerCropTransform()
                                            .placeholder(R.drawable.gymnatiionlogo_squr_defult_err)
                                            .error(R.drawable.gymnatiionlogo_squr_defult_err)
                                            .priority(Priority.HIGH))
                                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                    .into(((HeaderViewHolder) holder).imgv_member_profile_img);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
            sendPostReqAsyncTask.execute(user_rfid_local_database());


            Glide.with(mCtx)
                    .load(ServerURL+gym_database_logo())
                    .into(((HeaderViewHolder) holder).imgv_gym_logo);

        }
        else{


            position = position - 1 ;
            UserAndPlans userAndPlans = plansList.get(position);



            String plan_available = userAndPlans.getPlanAvailable();
            if(plan_available.equalsIgnoreCase("yes")){
                ((PlansViewHolder) holder).imgv_plan_lock.setImageResource(R.drawable.ic_member_plane);
            }
            else {
                ((PlansViewHolder) holder).imgv_plan_lock.setImageResource(R.drawable.plan_lock);
            }

            ((PlansViewHolder) holder).txtv_plan_name.setText(userAndPlans.getPlanName());
            ((PlansViewHolder) holder).txtv_plan_desc.setText(String.valueOf(userAndPlans.getPlanDescription()));
            ((PlansViewHolder) holder).txtv_plan_month.setText(String.valueOf(userAndPlans.getPlanDuration()));
            int duration = Integer.parseInt(userAndPlans.getPlanDuration());
            duration = duration * 30 ;
            ((PlansViewHolder) holder).txtv_plan_days.setText(String.valueOf(duration));
            ((PlansViewHolder) holder).txtv_plan_price.setText(String.valueOf(userAndPlans.getPlanPrice()));

            }

    }



    @Override
    public int getItemCount() {
        return plansList.size()+1;
    }

    public String gym_database_name(){
        Cursor c = dbConnction.query_user_data("gym_info_local_database",null,null,null,null,null,null);
        c.moveToPosition(0);
        String gym_url = c.getString(1);
        return gym_url ;
    }

    public String gym_database_logo(){
        Cursor c = dbConnction.query_user_data("gym_info_local_database",null,null,null,null,null,null);
        c.moveToPosition(0);
        String gym_url = c.getString(2);
        return gym_url ;
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






}
