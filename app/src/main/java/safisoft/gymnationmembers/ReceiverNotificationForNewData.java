package safisoft.gymnationmembers;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

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


import static android.content.Context.NOTIFICATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by root on 6/1/18.
 */

public class ReceiverNotificationForNewData extends BroadcastReceiver {

    String Server_URL ;
    String URL_massage ;
    String URL_post  ;
    String URL_membersatgym ;
    String URL_workout  ;
    List<Member> productList;
    DbConnction dbConnction;



    boolean statu_newad_notification ;
    boolean statu_enter_gym_notification;
    boolean statu_newassage_notification ;
    boolean statu_workout_notification ;


    

   Context context1  ;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        context1 = context ;
        Server_URL = context.getResources().getString(R.string.Server_URL);
        URL_massage = context.getResources().getString(R.string.URL_massage);
        URL_post = context.getResources().getString(R.string.URL_post);
        URL_membersatgym = context.getResources().getString(R.string.URL_membersatgym);
        URL_workout = context.getResources().getString(R.string.URL_workout);



        dbConnction = new DbConnction(context);
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

        check_box_statu_at_Startactivity();
   //
   //     Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("safisoft.gymnationmembers");
   //     if (launchIntent != null) {
   //         context.startActivity(launchIntent);
   //     }

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            SetAlarmClass setAlarmClass = new SetAlarmClass();
            setAlarmClass.setalarm(context);



            fire_notification_post(context,intent);
            SendReceivedData_massage(Server_URL+URL_massage,gym_database_url());
            SendReceivedData_members_atgym(Server_URL+URL_membersatgym,gym_database_url());
            SendReceivedData_workout(Server_URL+URL_workout,gym_database_url(),user_rfid_local_database());
        }

        else {
            fire_notification_post(context,intent);
            SendReceivedData_massage(Server_URL+URL_massage,gym_database_url());
            SendReceivedData_members_atgym(Server_URL+URL_membersatgym,gym_database_url());
            SendReceivedData_workout(Server_URL+URL_workout,gym_database_url(),user_rfid_local_database());

        }
    }




    private void fire_notification_post(final Context context, final Intent intent) {
        if(statu_newad_notification) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Server_URL + URL_post,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                if (response.length() > 3) { //minimum response length it always return value "[]" check if there any response before convert
                                    JSONArray array = new JSONArray(response);
                                    JSONObject massage = array.getJSONObject(array.length() - 1);
                                    int id_post_from_server = massage.getInt("_id");
                                    String massage_head = massage.getString("ad_name");
                                    String massage_body = massage.getString("ad_desc");
                                    String ad_icon_img = Server_URL+massage.getString("ad_icon");

                                    if (id_post_from_server > get_id_post_count()) {
                                        start_notification_body_post(context, massage_head, massage_body,ad_icon_img, AdsPostActivity.class);
                                    }
                                    dbConnction.update_id_post_count(id_post_from_server);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
            Volley.newRequestQueue(context).add(stringRequest);
        }
    }




    public void SendReceivedData_members_atgym(final String server_URL,final String gym_name) {
        if(statu_enter_gym_notification && !user_rfid_local_database().equals("0")) {
            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                String line;

                @Override
                protected String doInBackground(String... params) {
                    String Server_URL = server_URL;
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
                            if (result.length() > 3) { //minimum response length it always return value "[]" check if there any response before convert
                                JSONArray array = new JSONArray(result);
                                JSONObject memberatgym = array.getJSONObject(array.length() - 1);
                                int id_member_from_server = memberatgym.getInt("_id");
                                String massage_head = memberatgym.getString("member_name");
                                String workout1 = memberatgym.getString("workout_one_name");
                                String workout2 = memberatgym.getString("workout_two_name");
                                String member_profile_img = Server_URL+memberatgym.getString("member_photo");

                                String massage_body = "Start Training  " + workout1 + " " + workout2;
                                if (id_member_from_server > get_id_memberatgym_count()) {
                                    start_notification_body_memberatgym(massage_head, massage_body, member_profile_img, MemberAtGymActivity.class);
                                }
                                dbConnction.update_id_member_count(id_member_from_server);
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
    }





    public void SendReceivedData_massage(final String server_URL,final String gym_name) {
        if(statu_newassage_notification && !user_rfid_local_database().equals("0")) {
            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                String line;

                @Override
                protected String doInBackground(String... params) {
                    String Server_URL = server_URL;
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
                            if (result.length() > 3) { //minimum response length it always return value "[]" check if there any response before convert
                                JSONArray array = new JSONArray(result);
                                JSONObject massage = array.getJSONObject(array.length() - 1);
                                int id_notification_from_server = massage.getInt("_id");
                                String massage_head = massage.getString("massage_head");
                                String massage_body = massage.getString("massage_body");

                                if (id_notification_from_server > get_id_notification_count()) {
                                    start_notification_body_massage(massage_head, massage_body, NotificationActivity.class);
                                }
                                dbConnction.update_id_notification_count(id_notification_from_server);
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
    }


    public void SendReceivedData_workout(final String server_URL,final String gym_name,final String rfid) {
        if(statu_workout_notification && !user_rfid_local_database().equals("0")) {
            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                String line;

                @Override
                protected String doInBackground(String... params) {
                    String Server_URL = server_URL;
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("gymname", gym_name));
                    nameValuePairs.add(new BasicNameValuePair("rfid", rfid));
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
                            if (result.length() > 3) { //minimum response length it always return value "[]" check if there any response before convert
                                JSONArray array = new JSONArray(result);
                                JSONObject massage = array.getJSONObject(array.length() - 1);
                                int id_workout_from_server = array.length();
                                String massage_head = "Mission Accomplished";
                                String workoutOneName = massage.getString("workout_one_name");
                                String workoutTwoName = massage.getString("workout_two_name");
                                String workoutTimeDuration = massage.getString("workout_time_duration");
                                if (id_workout_from_server > get_id_workout_count()) {
                                    end_body_massage_workout(massage_head, "Trained" + " " + workoutOneName + " " + workoutTwoName + " " + "in" + " " + workoutTimeDuration, WorkoutHistoryActivity.class);
                                }
                                dbConnction.update_id_workout_count(id_workout_from_server);
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
    }





    public void start_notification_body_massage(final String title, final String body, final Class aClass ){

                        Intent intent = new Intent(context1,aClass);

       // intent = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.safisoft.fakelocation_duckgps");
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context1.getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
            Uri uri = Uri.parse("android.resource://" +context1.getApplicationContext().getPackageName() + "/" + R.raw.dumbel_notification);
            NotificationManager notificationManager = (NotificationManager)context1.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("1", "Start Notification",NotificationManager.IMPORTANCE_HIGH);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context1, "1");
                 notificationChannel.setSound(uri, null);
                builder = builder.setContentIntent(pendingIntent)
                        .setSound(uri)
                        .setSmallIcon(R.drawable.ic_launcher1)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setContentTitle(title)
                        .setLargeIcon(BitmapFactory.decodeResource(context1.getResources(), R.drawable.notificationicon))
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setChannelId("1");
                notificationManager = context1.getSystemService(NotificationManager.class);
                notificationManager.notify(1, builder.build());
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder builder_repet = new NotificationCompat.Builder(context1.getApplicationContext())
                    .setContentIntent(pendingIntent)
                    .setSound(uri)
                    .setSmallIcon(R.drawable.ic_launcher1)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setContentText(body)
                    .setLargeIcon(BitmapFactory.decodeResource(context1.getResources(), R.drawable.notificationicon))
                    .setPriority(Notification.PRIORITY_MAX);
            notificationManager.notify(0, builder_repet.build());
        }


    }



    public void start_notification_body_post( final Context context, final String title, final String body,String ad_icon_img, final Class aClass){

        Glide.with(getApplicationContext()).asBitmap().
                load(ad_icon_img)
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


        Intent intent = new Intent(context,aClass);

        // intent = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.safisoft.fakelocation_duckgps");
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 2, intent, PendingIntent.FLAG_MUTABLE);
            Uri uri = Uri.parse("android.resource://" +context.getApplicationContext().getPackageName() + "/" + R.raw.dumbel_notification);
            NotificationManager notificationManager = (NotificationManager)context.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("2", "post Notification",NotificationManager.IMPORTANCE_HIGH);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "2");
                notificationChannel.setSound(uri, null);
                builder = builder.setContentIntent(pendingIntent)
                        .setSound(uri)
                        .setSmallIcon(R.drawable.ic_launcher1)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setContentTitle(title)
                        .setLargeIcon(resource)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setChannelId("2");
                notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.notify(2, builder.build());
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder builder_repet = new NotificationCompat.Builder(context.getApplicationContext())
                    .setContentIntent(pendingIntent)
                    .setSound(uri)
                    .setSmallIcon(R.drawable.ic_launcher1)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setContentText(body)
                    .setLargeIcon(resource)
                    .setPriority(Notification.PRIORITY_MAX);
            notificationManager.notify(2, builder_repet.build());
        }



                        return true;
                    }
                }).submit();


    }



    public void start_notification_body_memberatgym(final String title, final String body, String member_profile_img, final Class aClass){

        Glide.with(getApplicationContext()).asBitmap().
                load(member_profile_img)
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

        Intent intent = new Intent(context1,aClass);

        // intent = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.safisoft.fakelocation_duckgps");
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context1.getApplicationContext(), 3, intent, PendingIntent.FLAG_MUTABLE);
            Uri uri = Uri.parse("android.resource://" +context1.getApplicationContext().getPackageName() + "/" + R.raw.dumbel_notification);
            NotificationManager notificationManager = (NotificationManager)context1.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("3", "member Notification",NotificationManager.IMPORTANCE_HIGH);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context1, "");
                notificationChannel.setSound(uri, null);
                builder = builder.setContentIntent(pendingIntent)
                        .setSound(uri)
                        .setSmallIcon(R.drawable.ic_launcher1)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setContentTitle(title)
                        .setLargeIcon(resource)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setChannelId("3");
                notificationManager = context1.getSystemService(NotificationManager.class);
                notificationManager.notify(3, builder.build());
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder builder_repet = new NotificationCompat.Builder(context1.getApplicationContext())
                    .setContentIntent(pendingIntent)
                    .setSound(uri)
                    .setSmallIcon(R.drawable.ic_launcher1)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setContentText(body)
                    .setLargeIcon(resource)
                    .setPriority(Notification.PRIORITY_MAX);
            notificationManager.notify(3, builder_repet.build());
        }
                        return true;
                    }
                }).submit();

    }


    public void end_body_massage_workout(String title,String body,Class aClass){

        Intent intent = new Intent(context1,aClass);

        // intent = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.safisoft.fakelocation_duckgps");
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context1.getApplicationContext(), 4, intent, PendingIntent.FLAG_MUTABLE);
            Uri uri = Uri.parse("android.resource://" +context1.getApplicationContext().getPackageName() + "/" + R.raw.dumbel_notification);
            NotificationManager notificationManager = (NotificationManager)context1.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("4", "Start Notification",NotificationManager.IMPORTANCE_HIGH);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context1, "4");
                notificationChannel.setSound(uri, null);
                builder = builder.setContentIntent(pendingIntent)
                        .setSound(uri)
                        .setSmallIcon(R.drawable.ic_launcher1)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setContentTitle(title)
                        .setLargeIcon(BitmapFactory.decodeResource(context1.getResources(), R.drawable.fullbodymusc_fornotifacation))
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setChannelId("4");
                notificationManager = context1.getSystemService(NotificationManager.class);
                notificationManager.notify(4, builder.build());
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder builder_repet = new NotificationCompat.Builder(context1.getApplicationContext())
                    .setContentIntent(pendingIntent)
                    .setSound(uri)
                    .setSmallIcon(R.drawable.ic_launcher1)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setContentText(body)
                    .setLargeIcon(BitmapFactory.decodeResource(context1.getResources(), R.drawable.fullbodymusc_fornotifacation))
                    .setPriority(Notification.PRIORITY_MAX);
            notificationManager.notify(4, builder_repet.build());
        }

    }




    public int get_id_notification_count(){

        Cursor c = dbConnction.query_user_data("id_notification_check",null,null,null,null,null,null);
        c.moveToPosition(0);
        int count = c.getInt(1);
        return count ;
    }


    public int get_id_post_count(){

        Cursor c = dbConnction.query_user_data("id_post_check",null,null,null,null,null,null);
        c.moveToPosition(0);
        int count = c.getInt(1);
        return count ;
    }


    public int get_id_memberatgym_count(){

        Cursor c = dbConnction.query_user_data("id_member_check",null,null,null,null,null,null);
        c.moveToPosition(0);
        int count = c.getInt(1);
        return count ;
    }

    public int get_id_workout_count(){

        Cursor c = dbConnction.query_user_data("id_workout_check",null,null,null,null,null,null);
        c.moveToPosition(0);
        int count = c.getInt(1);
        return count ;
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


    public void check_box_statu_at_Startactivity(){
        Cursor c = dbConnction.query_user_data("notification_check",null,null,null,null,null,null);
        c.moveToPosition(0);
        if(c.getString(1).equals("true"))  {statu_newad_notification = true;}  else{statu_newad_notification = false;}
        if(c.getString(2).equals("true"))  {statu_enter_gym_notification = true;}  else{statu_enter_gym_notification = false;}
        if(c.getString(3).equals("true"))  {statu_newassage_notification = true;}  else{statu_newassage_notification = false;}
        if(c.getString(4).equals("true"))  {statu_workout_notification = true;}  else{statu_workout_notification = false;}
    }



}

