package safisoft.gymnationmembers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;


public class WorkoutHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String ServerURL ;
    String WorkOut_Img_Folder_Name ;
    String WorkOut_Img_Type ;
    private Context mCtx;
    private List<Workout> workoutList;





    class WorkoutHistoryViewWorHolder extends RecyclerView.ViewHolder {
        ImageButton btn_share_facebook ;
        TextView txtv_workout_start_time,txtv_workout_rate,txtv_workout_duration,txtv_workout_sign_out,txtv_workout_end_time,txtv_workout_date,txtv_workout_one_history,txtv_workout_tow_history ;
        ImageView img_workout_rate,imgv_workout_one,imgv_workout_two;

        public WorkoutHistoryViewWorHolder(View itemView) {
            super(itemView);

            txtv_workout_start_time = itemView.findViewById(R.id.txtv_workout_start_time);
            txtv_workout_rate = itemView.findViewById(R.id.txtv_workout_rate);
            txtv_workout_duration = itemView.findViewById(R.id.txtv_workout_duration);
            txtv_workout_sign_out = itemView.findViewById(R.id.txtv_workout_sign_out);
            txtv_workout_end_time = itemView.findViewById(R.id.txtv_workout_end_time);
            txtv_workout_date = itemView.findViewById(R.id.txtv_workout_date);
            txtv_workout_one_history = itemView.findViewById(R.id.txtv_workout_one_history);
            txtv_workout_tow_history = itemView.findViewById(R.id.txtv_workout_tow_history);
            img_workout_rate = itemView.findViewById(R.id.img_workout_rate);
            imgv_workout_one = itemView.findViewById(R.id.imgv_workout_one_history);
            imgv_workout_two = itemView.findViewById(R.id.imgv_workout_tow_history);
            btn_share_facebook = itemView.findViewById(R.id.btn_share_facebook);


            // on item click
            btn_share_facebook.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // get position
                    int pos = getAdapterPosition();

                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        Workout clickedDataItem = workoutList.get(pos-1);


                        Intent intent = new Intent(mCtx, WaterMarkShareActivity.class);
                        intent.putExtra("WORKOUT_DATE", clickedDataItem.getstartWorkoutDate());
                        intent.putExtra("WORKOUT_DURATION", clickedDataItem.getWorkoutTimeDuration());
                        intent.putExtra("WORKOUT_ONE_NAME", clickedDataItem.getWorkoutOneName());
                        intent.putExtra("WORKOUT_TOW_NAME", clickedDataItem.getWorkoutTwoName());
                        mCtx.startActivity(intent);
                    }
                }
            });


        }
    }

    class HeaderViewWorkoutHistoryHolder extends RecyclerView.ViewHolder {
        TextView txtv_workout_days_count ;
        public HeaderViewWorkoutHistoryHolder(View itemView) {
            super(itemView);
            txtv_workout_days_count = itemView.findViewById(R.id.txtv_workout_days_count);
        }
    }

    public WorkoutHistoryAdapter(Context mCtx, List<Workout> workoutList) {
        this.mCtx = mCtx;
        this.workoutList = workoutList;
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



        View view;
        if(viewType == 0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_history_header, parent, false);
            return new HeaderViewWorkoutHistoryHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workouthistory_list, parent, false);
            return new WorkoutHistoryViewWorHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(position == 0){

            ((HeaderViewWorkoutHistoryHolder) holder).txtv_workout_days_count.setText(Integer.toString(workoutList.size()));
    }
        else

    {
        position = position - 1 ;
        Workout workout = workoutList.get(position);

        String workout_img_one_name = workout.getWorkoutOneName();
        String workout_img_two_name = workout.getWorkoutTwoName();

        if (workout_img_one_name.equals("")) {
            ((WorkoutHistoryViewWorHolder) holder).imgv_workout_one.setVisibility(View.GONE);
            ((WorkoutHistoryViewWorHolder) holder).txtv_workout_one_history.setVisibility(View.GONE);
        } else {
            Glide.with(mCtx)
                    .load(ServerURL + WorkOut_Img_Folder_Name + workout_img_one_name + WorkOut_Img_Type)
                    .into(((WorkoutHistoryViewWorHolder) holder).imgv_workout_one);
            ((WorkoutHistoryViewWorHolder) holder).imgv_workout_one.setVisibility(View.VISIBLE);
            ((WorkoutHistoryViewWorHolder) holder).txtv_workout_one_history.setVisibility(View.VISIBLE);
            ((WorkoutHistoryViewWorHolder) holder).txtv_workout_one_history.setText(workout_img_one_name);
        }




        if (workout_img_two_name.equals("")) {
            ((WorkoutHistoryViewWorHolder) holder).imgv_workout_two.setVisibility(View.GONE);
            ((WorkoutHistoryViewWorHolder) holder).txtv_workout_tow_history.setVisibility(View.GONE);
        } else {
            Glide.with(mCtx)
                    .load(ServerURL + WorkOut_Img_Folder_Name + workout_img_two_name + WorkOut_Img_Type)
                    .into(((WorkoutHistoryViewWorHolder) holder).imgv_workout_two);
            ((WorkoutHistoryViewWorHolder) holder).imgv_workout_two.setVisibility(View.VISIBLE);
            ((WorkoutHistoryViewWorHolder) holder).txtv_workout_tow_history.setVisibility(View.VISIBLE);
            ((WorkoutHistoryViewWorHolder) holder).txtv_workout_tow_history.setText(workout_img_two_name);
        }


        switch (workout.getWorkoutRate()) {

            case "0":
                ((WorkoutHistoryViewWorHolder) holder).img_workout_rate.setImageResource(R.drawable.rate_0);
                break;

            case "20":
                ((WorkoutHistoryViewWorHolder) holder).img_workout_rate.setImageResource(R.drawable.rate_20);
                break;

            case "50":
                ((WorkoutHistoryViewWorHolder) holder).img_workout_rate.setImageResource(R.drawable.rate_50);
                break;

            case "80":
                ((WorkoutHistoryViewWorHolder) holder).img_workout_rate.setImageResource(R.drawable.rate_80);
                break;

            case "100":
                ((WorkoutHistoryViewWorHolder) holder).img_workout_rate.setImageResource(R.drawable.rate_100);
                break;

        }

        ((WorkoutHistoryViewWorHolder) holder).txtv_workout_start_time.setText(workout.getstartWorkoutTime());
        ((WorkoutHistoryViewWorHolder) holder).txtv_workout_rate.setText(workout.getWorkoutRate() + "%");
        ((WorkoutHistoryViewWorHolder) holder).txtv_workout_duration.setText(String.valueOf(workout.getWorkoutTimeDuration()));
        ((WorkoutHistoryViewWorHolder) holder).txtv_workout_sign_out.setText(String.valueOf(workout.getWorkoutSignOutMode()));
        ((WorkoutHistoryViewWorHolder) holder).txtv_workout_end_time.setText(String.valueOf(workout.getendWorkoutTime()));
        ((WorkoutHistoryViewWorHolder) holder).txtv_workout_date.setText(String.valueOf(workout.getstartWorkoutDate()));
    }
    }

    @Override
    public int getItemCount() {
        return workoutList.size()+1;
    }


}
