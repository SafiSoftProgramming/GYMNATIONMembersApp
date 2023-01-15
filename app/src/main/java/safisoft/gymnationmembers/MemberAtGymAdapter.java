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

/**
 * Created by Belal on 10/18/2017.
 */

public class MemberAtGymAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String ServerURL ;
    String WorkOut_Img_Folder_Name ;
    String WorkOut_Img_Type ;
    private Context mCtx;
    private List<Member> memberList;
    DbConnction dbConnction;



    class MemberAtGymViewHolder extends RecyclerView.ViewHolder {

        TextView txtv_members_name, txtv_member_enter_time, txtv_workout_one_name, txtv_workout_two_name;
        ImageView imgv_members_profile,imgv_workout_one,imgv_workout_two;

        public MemberAtGymViewHolder(View itemView) {
            super(itemView);

            txtv_members_name = itemView.findViewById(R.id.txtv_members_name);
            txtv_member_enter_time = itemView.findViewById(R.id.txtv_member_enter_time);
            txtv_workout_one_name = itemView.findViewById(R.id.txtv_workout_one_name);
            txtv_workout_two_name = itemView.findViewById(R.id.txtv_workout_two_name);
            imgv_members_profile = itemView.findViewById(R.id.imgv_members_profile);
            imgv_workout_one = itemView.findViewById(R.id.imgv_workout_one);
            imgv_workout_two = itemView.findViewById(R.id.imgv_workout_two);
        }
    }

    class HeaderViewMemberAtGymHolder extends RecyclerView.ViewHolder {

        ImageView imgv_gym_logo ;
        TextView txtv_gym_name;
        TextView txtv_num_of_member_atgym ;


        public HeaderViewMemberAtGymHolder(View itemView) {
            super(itemView);

            imgv_gym_logo = itemView.findViewById(R.id.imgv_gym_logo);
            txtv_gym_name = itemView.findViewById(R.id.txtv_gym_name);
            txtv_num_of_member_atgym = itemView.findViewById(R.id.txtv_num_of_member_atgym);

        }
    }

    public MemberAtGymAdapter(Context mCtx, List<Member> memberList) {
        this.mCtx = mCtx;
        this.memberList = memberList;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_at_gym_header, parent, false);
            return new HeaderViewMemberAtGymHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.members_list, parent, false);
            return new MemberAtGymViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {



        if(position == 0){

            Glide.with(mCtx)
                    .load(ServerURL+gym_database_logo())
                    .into(((HeaderViewMemberAtGymHolder) holder).imgv_gym_logo);

            ((HeaderViewMemberAtGymHolder) holder).txtv_gym_name.setText(gym_database_name());
            ((HeaderViewMemberAtGymHolder) holder).txtv_num_of_member_atgym.setText(Integer.toString(memberList.size()));


        }
        else{


                position = position - 1 ;
                Member member = memberList.get(position);


                String workout_img_one_name = member.getWorkOutOneName();
                String workout_img_two_name = member.getWorkOutTwoName();

                if (workout_img_one_name.equals("")) {
                    ((MemberAtGymViewHolder) holder).txtv_workout_one_name.setVisibility(View.GONE);
                    ((MemberAtGymViewHolder) holder).imgv_workout_one.setVisibility(View.GONE);
                } else {
                    Glide.with(mCtx)
                            .load(ServerURL + WorkOut_Img_Folder_Name + workout_img_one_name + WorkOut_Img_Type)
                            .into(((MemberAtGymViewHolder) holder).imgv_workout_one);
                    ((MemberAtGymViewHolder) holder).txtv_workout_one_name.setVisibility(View.VISIBLE);
                    ((MemberAtGymViewHolder) holder).imgv_workout_one.setVisibility(View.VISIBLE);
                }

                if (workout_img_two_name.equals("")) {
                    ((MemberAtGymViewHolder) holder).txtv_workout_two_name.setVisibility(View.GONE);
                    ((MemberAtGymViewHolder) holder).imgv_workout_two.setVisibility(View.GONE);
                } else {
                    Glide.with(mCtx)
                            .load(ServerURL + WorkOut_Img_Folder_Name + workout_img_two_name + WorkOut_Img_Type)
                            .into(((MemberAtGymViewHolder) holder).imgv_workout_two);
                    ((MemberAtGymViewHolder) holder).txtv_workout_two_name.setVisibility(View.VISIBLE);
                    ((MemberAtGymViewHolder) holder).imgv_workout_two.setVisibility(View.VISIBLE);
                }

                Glide.with(mCtx)
                        .load(ServerURL + member.getMembersPhoto())
                        .apply(centerCropTransform()
                                .placeholder(R.drawable.gymnatiionlogo_squr_defult_err)
                                .error(R.drawable.gymnatiionlogo_squr_defult_err)
                                .priority(Priority.HIGH))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(((MemberAtGymViewHolder) holder).imgv_members_profile);

                ((MemberAtGymViewHolder) holder).txtv_members_name.setText(member.getMembersNmae());
                ((MemberAtGymViewHolder) holder).txtv_member_enter_time.setText(member.getEnterMemberTimeDate());
                ((MemberAtGymViewHolder) holder).txtv_workout_one_name.setText(String.valueOf(member.getWorkOutOneName()));
                ((MemberAtGymViewHolder) holder).txtv_workout_two_name.setText(String.valueOf(member.getWorkOutTwoName()));

            }

    }



    @Override
    public int getItemCount() {
        return memberList.size()+1;
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
