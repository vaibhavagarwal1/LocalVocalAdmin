package com.dev.localvocaladmin.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.localvocaladmin.R;
import com.dev.localvocaladmin.activities.ShopDetailsActivity;
import com.dev.localvocaladmin.filters.FilterShops;
import com.dev.localvocaladmin.models.ModelShop;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterShops extends RecyclerView.Adapter<AdapterShops.HolderShops> implements Filterable {

    //context and lists
    public ArrayList<ModelShop> shopArrayList, filterList;
    private final Context context;
    private FilterShops filter;
    //Firebase
    private FirebaseAuth firebaseAuth;

    public AdapterShops(Context context, ArrayList<ModelShop> shopArrayList) {
        this.context = context;
        this.shopArrayList = shopArrayList;
        this.filterList = shopArrayList;
    }

    @NonNull
    @Override
    public HolderShops onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate layout row_shops.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_shops, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        return new HolderShops(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterShops.HolderShops holder, int position) {
        //get Data
        String shopImage = shopArrayList.get(position).getShopimage();
        String shopName = shopArrayList.get(position).getShopname();
        String shopCategory = shopArrayList.get(position).getShopcategory();
        String shopFee = shopArrayList.get(position).getDeliveryfee();
        String uid = shopArrayList.get(position).getUid();
        String shopId = shopArrayList.get(position).getShopid();

        //get shop status
        getShopStatus(holder, shopId);

        //set Data
        holder.shopNameTv.setText(shopName);
        holder.shopCategoryTv.setText(shopCategory);
        holder.feeTv.setText("Delivery Fee : Rs " + shopFee);

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
            intent.putExtra("type", "Details");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return shopArrayList.size();
    }

    private void getShopStatus(HolderShops holder, String shopId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ShopStatus").child(shopId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.shopStatusTv.setText("Open");
                    holder.shopStatusTv.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
                } else {
                    holder.shopStatusTv.setText("Closed");
                    holder.shopStatusTv.setTextColor(Color.RED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterShops(this, filterList);
        }
        return filter;
    }

    class HolderShops extends RecyclerView.ViewHolder {

        //UI Views
        private final CircleImageView shopImageIv;
        private final TextView shopNameTv;
        private final TextView shopCategoryTv;
        private final TextView shopStatusTv;
        private final TextView feeTv;

        public HolderShops(@NonNull View itemView) {
            super(itemView);

            //init UI Views
            shopImageIv = itemView.findViewById(R.id.productImageIv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            shopCategoryTv = itemView.findViewById(R.id.shopCategoryTv);
            shopStatusTv = itemView.findViewById(R.id.shopStatusTv);
            feeTv = itemView.findViewById(R.id.feeTv);

        }
    }
}
