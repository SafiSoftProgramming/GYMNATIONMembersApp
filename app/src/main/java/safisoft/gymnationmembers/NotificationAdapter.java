package safisoft.gymnationmembers;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;
import static com.facebook.FacebookSdk.getApplicationContext;


public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    String ServerURL ;
    String Notification_Icon_Folder_Name ;
    String Notification_Icon_Type ;
    private Context mCtx;
    private List<Notification> notificationList;
    DbConnction dbConnction;



    class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView txtv_massage_head, txtv_massage_body, txtv_massage_time_date;
        ImageView imgv_massage_icon,imgv_show_more_text;

        public NotificationViewHolder(View itemView) {
            super(itemView);

            txtv_massage_head = itemView.findViewById(R.id.txtv_massage_head);
            txtv_massage_body = itemView.findViewById(R.id.txtv_massage_body);
            txtv_massage_time_date = itemView.findViewById(R.id.txtv_massage_time_date);
            imgv_massage_icon = itemView.findViewById(R.id.imgv_massage_icon);
            imgv_show_more_text = itemView.findViewById(R.id.imgv_show_more_text);

            // on item click
            imgv_show_more_text.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // get position
                    int pos = getAdapterPosition();
                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        ViewGroup.LayoutParams params = txtv_massage_body.getLayoutParams();
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        txtv_massage_body.setLayoutParams(params);
                    }
                }
            });


        }
    }



    class HeaderViewMessagesHolder extends RecyclerView.ViewHolder {
        TextView txtv_messages_count ;
        ImageView imgv_gym_logo ;
        TextView txtv_gym_name ;
        public HeaderViewMessagesHolder(View itemView) {
            super(itemView);
            txtv_messages_count = itemView.findViewById(R.id.txtv_messages_count);
            imgv_gym_logo = itemView.findViewById(R.id.imgv_gym_logo);
            txtv_gym_name = itemView.findViewById(R.id.txtv_gym_name);
        }
    }


    public NotificationAdapter(Context mCtx, List<Notification> notificationList) {
        this.mCtx = mCtx;
        this.notificationList = notificationList;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_header, parent, false);
            return new HeaderViewMessagesHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list, parent, false);
            return new NotificationViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {
            ServerURL = mCtx.getResources().getString(R.string.Server_URL);
            Notification_Icon_Folder_Name = mCtx.getResources().getString(R.string.Notification_Icons_Folder_Name);
            Notification_Icon_Type = mCtx.getResources().getString(R.string.Notification_Icon_Type);

        if(position == 0){

            ((HeaderViewMessagesHolder) holder).txtv_messages_count.setText(Integer.toString(notificationList.size()));

            Glide.with(mCtx)
                    .load(ServerURL+gym_database_logo())
                    .into(((HeaderViewMessagesHolder) holder).imgv_gym_logo);

            ((HeaderViewMessagesHolder) holder).txtv_gym_name.setText(gym_database_name());
        }
        else {

            position = position - 1;
            Notification notification = notificationList.get(position);

            Glide.with(mCtx)
                    .load(ServerURL + Notification_Icon_Folder_Name + notification.getMassageIcon() + Notification_Icon_Type)
                    .apply(centerCropTransform()
                            .placeholder(R.drawable.gymnatiionlogo_squr_defult_err)
                            .error(R.drawable.gymnatiionlogo_squr_defult_err)
                            .priority(Priority.HIGH))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(((NotificationViewHolder) holder).imgv_massage_icon);


            ((NotificationViewHolder) holder).txtv_massage_head.setText(notification.getMassageHead());
            ((NotificationViewHolder) holder).txtv_massage_body.setText(notification.getMassageBody());
            ((NotificationViewHolder) holder).txtv_massage_time_date.setText(String.valueOf(notification.getMassageTimeDate()));


            if (notification.getMassageBody().length() >= 150) {
                ((NotificationViewHolder) holder).imgv_show_more_text.setVisibility(View.VISIBLE);
            } else {
                ((NotificationViewHolder) holder).imgv_show_more_text.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemCount() {
        return notificationList.size()+1;
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



}
