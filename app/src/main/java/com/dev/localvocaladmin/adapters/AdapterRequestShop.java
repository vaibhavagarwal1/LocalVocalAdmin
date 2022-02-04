package com.dev.localvocaladmin.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.localvocaladmin.R;
import com.dev.localvocaladmin.activities.ShopDetailsActivity;
import com.dev.localvocaladmin.models.ModelShop;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRequestShop extends RecyclerView.Adapter<AdapterRequestShop.HolderShops> {

    private final Context context;
    private final ArrayList<ModelShop> shopArrayList;
    //Firebase
    private FirebaseAuth firebaseAuth;
    //Strings for getting data
    private String shopId, shopStatus;

    public AdapterRequestShop(Context context, ArrayList<ModelShop> shopArrayList) {
        this.context = context;
        this.shopArrayList = shopArrayList;
    }

    @NonNull
    @Override
    public HolderShops onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate layout row_shops.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_shops_approval, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        return new HolderShops(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShops holder, int position) {
        //get Data
        String shopImage = shopArrayList.get(position).getShopimage();
        String shopName = shopArrayList.get(position).getShopname();
        String shopCategory = shopArrayList.get(position).getShopcategory();
        String shopFee = shopArrayList.get(position).getDeliveryfee();
        String uid = shopArrayList.get(position).getUid();
        shopId = shopArrayList.get(position).getShopid();
        String timestamp = shopId;

        //Converting timestamp into dd/MM/yyyy format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String formattedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        //get shop status
        holder.shopStatusTv.setText("Waiting For Approval");
        holder.shopStatusTv.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));

        //set Data
        holder.shopNameTv.setText(shopName);
        holder.shopCategoryTv.setText(shopCategory);
        holder.feeTv.setText("Delivery Fee : Rs " + shopFee);
        holder.dateTv.setText(formattedDate);

        //Creating Shimmer Drawable
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        try {
            Glide.with(context).load(shopImage).fitCenter().centerCrop().placeholder(shimmerDrawable).into(holder.shopImageIv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(v -> {
            //go to ShopDetailsActivity
            Intent intent = new Intent(context, ShopDetailsActivity.class);
            intent.putExtra("shopId", shopId);
            intent.putExtra("type", "Approval");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return shopArrayList.size();
    }

    class HolderShops extends RecyclerView.ViewHolder {

        //UI Views
        private final CircleImageView shopImageIv;
        private final TextView shopNameTv;
        private final TextView shopCategoryTv;
        private final TextView shopStatusTv;
        private final TextView feeTv;
        private final TextView dateTv;

        public HolderShops(@NonNull View itemView) {
            super(itemView);

            shopImageIv = itemView.findViewById(R.id.productImageIv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            shopCategoryTv = itemView.findViewById(R.id.shopCategoryTv);
            shopStatusTv = itemView.findViewById(R.id.shopStatusTv);
            feeTv = itemView.findViewById(R.id.feeTv);
            dateTv = itemView.findViewById(R.id.dateTv);

        }
    }

}
