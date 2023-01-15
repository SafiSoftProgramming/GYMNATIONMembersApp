package safisoft.gymnationmembers;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

import pl.droidsonroids.gif.GifTextView;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;


/**
 * Created by Belal on 10/18/2017.
 */

public class AdsPostAdapter extends RecyclerView.Adapter<AdsPostAdapter.AdPostViewHolder> {


    private Context mCtx;
    private List<AdsPost> adList;
    String Server_URL ;


    public AdsPostAdapter(Context mCtx, List<AdsPost> adList) {
        this.mCtx = mCtx;
        this.adList = adList;
    }

    @Override
    public AdPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.home_list, null);
        Server_URL = mCtx.getResources().getString(R.string.Server_URL);
        return new AdPostViewHolder(view);

    }

    @Override
    public void onBindViewHolder(AdPostViewHolder holder, int position) {
        AdsPost adsPost = adList.get(position);



        Glide.with(mCtx)
                .load(Server_URL+ adsPost.getAd_img())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(holder.gif_postview);



        Glide.with(mCtx)
                .load(Server_URL+ adsPost.getad_icon())
                .apply(centerCropTransform()
                        .placeholder(R.drawable.ic_defult_user_circ)
                        .error(R.drawable.ic_defult_user_circ)
                        .priority(Priority.HIGH))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(holder.img_post_icon);


        holder.txtv_post_desc.setText(adsPost.getAd_desc());

        holder.txtv_post_name.setText(adsPost.getad_name());

        holder.txtv_post_time_date.setText(adsPost.getad_time_date());



        if (adsPost.getAd_desc().length() >= 150){
            holder.imgv_show_more_text.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgv_show_more_text.setVisibility(View.GONE);
        }


        if (adsPost.getpromo_code().equals("")){
            holder.gifTextView_promocode.setVisibility(View.GONE);
        }
        else {
            holder.gifTextView_promocode.setVisibility(View.VISIBLE);
        }




/*
        try {
            String link=adsPost.getAd_desc();

            MediaController mediaController = new MediaController(mCtx);
            mediaController.setAnchorView(vid_v);
            Uri video = Uri.parse(link);
            vid_v.setMediaController(mediaController);
            vid_v.setVideoURI(video);
            vid_v.start();
        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText(mCtx, "Error connecting", Toast.LENGTH_SHORT).show();
        }
*/





    }

  //  VideoView vid_v ;

    @Override
    public int getItemCount() {
        return adList.size();
    }

    class AdPostViewHolder extends RecyclerView.ViewHolder {

        ImageView gif_postview ,img_post_icon , imgv_show_more_text;

        TextView txtv_post_desc , txtv_post_name,txtv_post_time_date ;

        GifTextView gifTextView_promocode ;

        LinearLayout btn_share_facebook ;




        public AdPostViewHolder(View itemView) {
            super(itemView);
            txtv_post_time_date = itemView.findViewById(R.id.txtv_post_time_date);
            txtv_post_desc = itemView.findViewById(R.id.txtv_post_desc);
            gif_postview = itemView.findViewById(R.id.gif_postview);
            txtv_post_name = itemView.findViewById(R.id.txtv_post_name);
            img_post_icon = itemView.findViewById(R.id.img_post_icon);
            imgv_show_more_text = itemView.findViewById(R.id.imgv_show_more_text);
            btn_share_facebook = itemView.findViewById(R.id.btn_share_facebook);
            gifTextView_promocode = itemView.findViewById(R.id.gifTextView_promocode);
           // vid_v = itemView.findViewById(R.id.vid_v);





            // on item click
            imgv_show_more_text.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // get position
                    int pos = getAdapterPosition();
                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        ViewGroup.LayoutParams params = txtv_post_desc.getLayoutParams();
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        txtv_post_desc.setLayoutParams(params);
                    }
                }
            });


            // on item click
            btn_share_facebook.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // get position
                    int pos = getAdapterPosition();

                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        AdsPost clickedDataItem = adList.get(pos);

                        Intent intent = new Intent(mCtx, safisoft.gymnationmembers.WaterMarkSharePostAdActivity.class);
                        intent.putExtra("ADPOST_NAME", clickedDataItem.getad_name());
                        intent.putExtra("ADPOST_ICON", clickedDataItem.getad_icon());
                        intent.putExtra("ADPOST_IMG", clickedDataItem.getAd_img());
                        intent.putExtra("ADPOST_DESC", clickedDataItem.getAd_desc());
                        intent.putExtra("ADPOST_TIME_DATE", clickedDataItem.getad_time_date());
                        intent.putExtra("ADPOST_PROMO_CODE", clickedDataItem.getpromo_code());
                        intent.putExtra("ADPOST_PROMO_CODE_EXPIRY_DATE", clickedDataItem.getpromo_code_expiry_date());
                        intent.putExtra("ADPOST_CONTACT_DETAILS", clickedDataItem.getcontact_details());
                        mCtx.startActivity(intent);
                    }
                }
            });





        }
    }
}
