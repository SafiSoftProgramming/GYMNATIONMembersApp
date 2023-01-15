package safisoft.gymnationmembers;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class GymsChooseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mCtx;
    private List<Gyms> gymsList;
    DbConnction dbConnction;
    String Server_URL ;



    class GymsViewHolder extends RecyclerView.ViewHolder {

        TextView txtv_gym_name;
        ImageView imgv_gym_logo;
        TextView txtv_gym_address ;

        public GymsViewHolder(View itemView) {
            super(itemView);
            txtv_gym_name = itemView.findViewById(R.id.txtv_gym_name);
            imgv_gym_logo = itemView.findViewById(R.id.imgv_gym_logo);
            txtv_gym_address = itemView.findViewById(R.id.txtv_gym_address);

            // on item click
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // get position
                    int pos = getAdapterPosition();
                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        Gyms clickedDataItem = gymsList.get(pos-1);

                        //  dataBaseConnction = new DataBaseConnction(mCtx);
                        dbConnction.update_gym_info_data(clickedDataItem.getGym_nameg(),clickedDataItem.getGym_logo(),clickedDataItem.getGym_database_url());

                        //  Intent intent = new Intent(mCtx, ScannerActivity.class);
                        Intent intent = new Intent(mCtx, safisoft.gymnationmembers.StartActivity.class);
                        mCtx.startActivity(intent);
                        ((GymsChooseActivity)mCtx).finish();

                    }
                }
            });
        }
    }

    class HeaderGymsHolder extends RecyclerView.ViewHolder {
        TextView txtv_gyms_count ;
        ImageButton btn_demo_gym ;
        public HeaderGymsHolder(View itemView) {
            super(itemView);
            txtv_gyms_count = itemView.findViewById(R.id.txtv_gyms_count);
            btn_demo_gym = itemView.findViewById(R.id.btn_demo_gym);

            btn_demo_gym.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  dataBaseConnction = new DataBaseConnction(mCtx);
                    dbConnction.update_gym_info_data("Demo GYM","gyms_logo/demo_gym.jpg","Social_Gym");

                    //  Intent intent = new Intent(mCtx, ScannerActivity.class);
                    Intent intent = new Intent(mCtx, safisoft.gymnationmembers.StartActivity.class);
                    mCtx.startActivity(intent);
                    ((GymsChooseActivity)mCtx).finish();
                }
            });

        }
    }


    public GymsChooseAdapter(Context mCtx, List<Gyms> gymsList) {
        this.mCtx = mCtx;
        this.gymsList = gymsList;
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
        Server_URL = mCtx.getResources().getString(R.string.Server_URL);
        View view;
        if(viewType == 0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_gym_header, parent, false);
            return new HeaderGymsHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gyms_list, parent, false);
            return new GymsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {

        dbConnction = new DbConnction(mCtx);
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

        if(position == 0){

            ((HeaderGymsHolder) holder).txtv_gyms_count.setText(Integer.toString(gymsList.size()));
        }
        else {
            position = position - 1 ;
            Gyms gyms = gymsList.get(position);

            Glide.with(mCtx)
                    .load(Server_URL + gyms.getGym_logo())
                    .apply(centerCropTransform()
                            .placeholder(R.drawable.ic_defult_user_circ)
                            .error(R.drawable.ic_defult_user_circ)
                            .priority(Priority.HIGH))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(((GymsViewHolder) holder).imgv_gym_logo);

            ((GymsViewHolder) holder).txtv_gym_name.setText(gyms.getGym_nameg());

            ((GymsViewHolder) holder).txtv_gym_address.setText(gyms.getGym_address());
        }

    }

    @Override
    public int getItemCount() {
        return gymsList.size()+1;
    }


}
