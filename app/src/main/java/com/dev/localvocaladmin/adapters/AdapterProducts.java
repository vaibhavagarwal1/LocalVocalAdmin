package com.dev.localvocaladmin.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.localvocaladmin.R;
import com.dev.localvocaladmin.activities.EditProductActivity;
import com.dev.localvocaladmin.models.ModelProducts;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.HolderProducts> {

    //Context and List
    private final Context context;
    private final ArrayList<ModelProducts> productsArrayList;
    //String Values
    private String productImage, productName, productCategory, productQuantity, productPrice, productId, productDiscount;
    private String productDiscountAvailable, productDiscountNote, uid, shopId, productDescription;
    //Firebase
    private FirebaseAuth firebaseAuth;
    //Alert Dialog
    private AlertDialog alertDialog;

    public AdapterProducts(Context context, ArrayList<ModelProducts> productsArrayList) {
        this.context = context;
        this.productsArrayList = productsArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public HolderProducts onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_products, parent, false);
        return new HolderProducts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HolderProducts holder, int position) {
        //get Data
        productImage = productsArrayList.get(position).getProductImage();
        productName = productsArrayList.get(position).getProductName();
        productCategory = productsArrayList.get(position).getProductCategory();
        productQuantity = productsArrayList.get(position).getProductQuantity();
        productPrice = productsArrayList.get(position).getProductPrice();
        productId = productsArrayList.get(position).getProductId();
        productDiscount = productsArrayList.get(position).getProductDiscount();
        productDiscountAvailable = productsArrayList.get(position).getDiscountAvailable();
        productDiscountNote = productsArrayList.get(position).getProductDiscountNote();
        uid = productsArrayList.get(position).getUid();
        shopId = productsArrayList.get(position).getShopId();
        productDescription = productsArrayList.get(position).getProductDesc();

        //init Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //set Data
        holder.productNameTv.setText(productName);
        holder.productQuantityTv.setText("(" + productQuantity + ")");
        holder.productDescriptionTv.setText(productDescription);
        holder.productPriceTv.setText("Rs. " + productPrice + "/-");
        holder.discountPriceTv.setText("Rs. " + productDiscount + "/-");
        holder.discountNoteTv.setText(productDiscountNote + "% Off");

        //Checking Discount Case
        if (productDiscountAvailable.equals("true")) {
            holder.discountPriceTv.setVisibility(View.VISIBLE);
            holder.discountNoteTv.setVisibility(View.VISIBLE);
            holder.productPriceTv.setPaintFlags(holder.productPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.discountPriceTv.setVisibility(View.GONE);
            holder.discountNoteTv.setVisibility(View.GONE);
        }

        //init Shimmer Layout
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        //set ProductImage in UI View
        try {
            Glide.with(context).load(productImage).fitCenter().centerCrop().placeholder(shimmerDrawable).into(holder.productImageIv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //ProductImage Click Handler
        holder.productImageIv.setOnClickListener(v -> {
            //Alert Dialog Builder
            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
            View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_image, null);

            //init PhotoView
            PhotoView productImageIv = view.findViewById(R.id.productImageIv);

            //setting it Into productImageIv
            try {
                Glide.with(context).load(productImage).fitCenter().centerCrop().placeholder(shimmerDrawable).into(productImageIv);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //setting the view To builder
            builder.setView(view);

            //Creating AlertDialog for the Builder
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //showing AlertDialog
            alertDialog.show();
        });

        //Getting the Holder Product and Shop IDs
        String pId = productsArrayList.get(position).getProductId();
        String sId = productsArrayList.get(position).getShopId();

        //edit Click Handler
        holder.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditProductActivity.class);
            intent.putExtra("productId", pId);
            intent.putExtra("shopId", sId);
            context.startActivity(intent);
        });

        //delete Click Handler
        holder.deleteBtn.setOnClickListener(v -> {
            final AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
            View viewm = LayoutInflater.from(context).inflate(R.layout.alert_dialog_delete, null);

            Button cancel = viewm.findViewById(R.id.cancel_button);
            Button logout = viewm.findViewById(R.id.logout_btn);
            ProgressBar logou = viewm.findViewById(R.id.logou);

            alert.setView(viewm);

            AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            cancel.setOnClickListener(view -> alertDialog.dismiss());

            logout.setOnClickListener(view -> {
                logou.setVisibility(View.VISIBLE);
                logout.setVisibility(View.INVISIBLE);
                removeItem(position);
                deleteWithImage();
            });

            alertDialog.show();
            alertDialog.getWindow().setLayout(800, 800);
        });
    }

    private void deleteWithImage() {
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(productImage);
        picRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Query fQuery = FirebaseDatabase.getInstance().getReference("Products").child(productId);
                    fQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                ds.getRef().removeValue();
                            }
                            Toast.makeText(context, "Product successfully deleted.", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(context, "" + databaseError.getDetails(), Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                });
    }

    public void removeItem(int position) {
        productsArrayList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    class HolderProducts extends RecyclerView.ViewHolder {

        //UI View
        private final ImageView productImageIv;
        private final TextView productNameTv;
        private final TextView productQuantityTv;
        private final TextView productDescriptionTv;
        private final TextView discountNoteTv;
        private final TextView discountPriceTv;
        private final TextView productPriceTv;
        private final Button editBtn;
        private final Button deleteBtn;
        private final ProgressBar deletePb;

        public HolderProducts(@NonNull @NotNull View itemView) {
            super(itemView);

            //init UI View
            productImageIv = itemView.findViewById(R.id.productImageIv);
            productNameTv = itemView.findViewById(R.id.productNameTv);
            productQuantityTv = itemView.findViewById(R.id.productQuantityTv);
            productDescriptionTv = itemView.findViewById(R.id.productDescriptionTv);
            discountNoteTv = itemView.findViewById(R.id.discountNoteTv);
            discountPriceTv = itemView.findViewById(R.id.discountPriceTv);
            productPriceTv = itemView.findViewById(R.id.productPriceTv);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            deletePb = itemView.findViewById(R.id.deletePb);
        }
    }

}
