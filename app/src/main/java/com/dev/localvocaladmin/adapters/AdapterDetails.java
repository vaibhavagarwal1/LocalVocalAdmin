package com.dev.localvocaladmin.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.localvocaladmin.R;
import com.dev.localvocaladmin.models.ModelDetail;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AdapterDetails extends RecyclerView.Adapter<AdapterDetails.HolderDetails> {
    private final ArrayList<ModelDetail> cartList;
    String pP;
    float f;
    private final Context context;
    private FirebaseUser user;

    public AdapterDetails(Context context, ArrayList<ModelDetail> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public HolderDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_details, parent, false);
        return new HolderDetails(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderDetails holder, int position) {
        String productName = cartList.get(position).getpName();
        String productImage = cartList.get(position).getProductImage();
        String productId = cartList.get(position).getProductId();
        String shopId = cartList.get(position).getShopId();
        String timeStamp = cartList.get(position).getTimeStamp();
        String originalPrice = cartList.get(position).getProductPrice();
        String finalPrice = cartList.get(position).getFinalPrice();
        String finalQuantity = cartList.get(position).getFinalQuantity();

        user = FirebaseAuth.getInstance().getCurrentUser();

        try {
            f = Float.parseFloat(originalPrice);
            pP = String.valueOf(f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.productNameTv.setText(productName);
        holder.productPriceTv.setText("Rs. " + pP);
        holder.finalPriceTv.setText("= Rs. " + finalPrice);
        holder.finalQuantityTv.setText("x (" + finalQuantity + ")");

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
            Glide.with(context).load(productImage).fitCenter().centerCrop().placeholder(shimmerDrawable).into(holder.productImageIv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    class HolderDetails extends RecyclerView.ViewHolder {

        //UI Views
        private final ImageView productImageIv;
        private final TextView productNameTv;
        private final TextView productPriceTv;
        private final TextView finalQuantityTv;
        private final TextView finalPriceTv;

        public HolderDetails(@NonNull View itemView) {
            super(itemView);
            //init UI Views
            productImageIv = itemView.findViewById(R.id.imageView5);
            productNameTv = itemView.findViewById(R.id.productName);
            productPriceTv = itemView.findViewById(R.id.originalPriceTv);
            finalQuantityTv = itemView.findViewById(R.id.quantity);
            finalPriceTv = itemView.findViewById(R.id.finalPrice);
        }
    }
}
