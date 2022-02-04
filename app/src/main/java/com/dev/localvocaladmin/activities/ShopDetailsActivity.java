package com.dev.localvocaladmin.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.localvocaladmin.R;
import com.dev.localvocaladmin.adapters.AdapterProducts;
import com.dev.localvocaladmin.models.ModelProducts;
import com.dev.localvocaladmin.models.ModelShop;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShopDetailsActivity extends AppCompatActivity {

    //UI Views
    private CardView backCv;
    private CircleImageView profileIv;
    private ImageView mapIv, shopImageIv;
    private TextView nameTv, addressTv, statusTv, shopNameTv, phoneTv, shopCityTv;
    private TextView feeTv, locationTv, emailTv, minOrderValueTv, filterProductsTv;
    private Button acceptBtn, declineBtn, editBtn, deleteBtn;
    private LinearLayout approvalButtonsLl, editButtonsLl, callSellerLl, noProductsLl;
    private EditText emailSubjectTv, emailMessageTv, searchProductsEt;
    private RecyclerView productsRv;
    private ShimmerFrameLayout productsSfl;
    private ImageButton filterBtn;

    //Firebase
    private FirebaseAuth firebaseAuth;

    //Strings
    private String latitude, longitude, shopName, phone;

    //Intent Data
    private String shopId, type;

    //Products List and Adapter
    private ArrayList<ModelProducts> productsArrayList;
    private AdapterProducts adapterProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        setContentView(R.layout.activity_shop_details);

        //Getting data from Intent
        shopId = getIntent().getStringExtra("shopId");
        type = getIntent().getStringExtra("type");

        //init UI Views
        backCv = findViewById(R.id.backCv);
        profileIv = findViewById(R.id.profileIv);
        shopImageIv = findViewById(R.id.productImageIv);
        mapIv = findViewById(R.id.mapIv);
        callSellerLl = findViewById(R.id.callSellerLl);
        nameTv = findViewById(R.id.nameTv);
        addressTv = findViewById(R.id.addressTv);
        statusTv = findViewById(R.id.statusTv);
        shopNameTv = findViewById(R.id.shopNameTv);
        phoneTv = findViewById(R.id.phoneTv);
        shopCityTv = findViewById(R.id.shopCityTv);
        feeTv = findViewById(R.id.feeTv);
        locationTv = findViewById(R.id.locationTv);
        emailTv = findViewById(R.id.emailTv);
        minOrderValueTv = findViewById(R.id.minOrderValueTv);
        acceptBtn = findViewById(R.id.acceptBtn);
        declineBtn = findViewById(R.id.declineBtn);
        editBtn = findViewById(R.id.editBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        approvalButtonsLl = findViewById(R.id.approvalButtonsLl);
        editButtonsLl = findViewById(R.id.editButtonsLl);
        filterProductsTv = findViewById(R.id.filterProductsTv);
        noProductsLl = findViewById(R.id.noProductsLl);
        productsRv = findViewById(R.id.productsRv);
        productsSfl = findViewById(R.id.productsSfl);
        searchProductsEt = findViewById(R.id.searchShopsEt);
        filterBtn = findViewById(R.id.filterBtn);

        //init Products ArrayList
        productsArrayList = new ArrayList<>();

        if (type.equals("Approval")) {
            approvalButtonsLl.setVisibility(View.VISIBLE);
            editButtonsLl.setVisibility(View.GONE);
            //load shop
            loadShopForApprovalDetails();
        } else {
            approvalButtonsLl.setVisibility(View.GONE);
            editButtonsLl.setVisibility(View.VISIBLE);
            //load shop
            loadShopDetails();
        }

        //load Products
        loadProducts();

        //init Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //Back Click Handler
        backCv.setOnClickListener(v -> ShopDetailsActivity.super.onBackPressed());

        //mapIv click handler
        mapIv.setOnClickListener(v -> openGoogleMaps());

        //accept Btn handler
        acceptBtn.setOnClickListener(v -> approveShop());

        //decline Btn handler
        declineBtn.setOnClickListener(v -> deleteFromShopApproval());

        //edit Btn handler
        editBtn.setOnClickListener(v -> Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show());

        //delete Btn handler
        deleteBtn.setOnClickListener(v -> deleteFromShops());

        //callSellerBtn
        callSellerLl.setOnClickListener(v -> callSeller());
    }

    private void loadProducts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
        reference.orderByChild("shopId").equalTo(shopId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    productsRv.setVisibility(View.VISIBLE);
                    ModelProducts modelProducts = ds.getValue(ModelProducts.class);
                    productsArrayList.add(modelProducts);
                }
                //Setup Adapter for Products
                adapterProducts = new AdapterProducts(ShopDetailsActivity.this, productsArrayList);
                //Checking total no. of Products
                if (adapterProducts.getItemCount() == 0) {
                    //if 0, then No Products LinearLayout, True
                    noProductsLl.setVisibility(View.VISIBLE);
                } else {
                    //else, then No Products LinearLayout, False
                    noProductsLl.setVisibility(View.GONE);
                }
                //setting Adapter to RecyclerView
                productsRv.setAdapter(adapterProducts);
                //Stopping shimmerLayout and setting it's visibility GONE
                productsSfl.stopShimmer();
                productsSfl.setVisibility(View.GONE);
                //Notify Data Change
                adapterProducts.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteFromShops() {
        FirebaseDatabase.getInstance().getReference("Shops")
                .child(shopId).removeValue()
                .addOnSuccessListener(aVoid -> showBottomSheetDialogForEmail())
                .addOnFailureListener(e -> Toast.makeText(ShopDetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadShopDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Shops");
        reference.orderByChild("shopid").equalTo(shopId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //get data
                            ModelShop modelShop = ds.getValue(ModelShop.class);

                            //set data
                            latitude = modelShop.getLatitude();
                            longitude = modelShop.getLongitude();
                            shopName = modelShop.getShopname();
                            phone = modelShop.getShopphone();
                            nameTv.setText(modelShop.getYourname());
                            addressTv.setText(modelShop.getShopaddress());
                            shopNameTv.setText(shopName);
                            shopCityTv.setText(modelShop.getShopcity());
                            emailTv.setText(modelShop.getShopemail());
                            phoneTv.setText("+91 " + phone);
                            feeTv.setText(modelShop.getDeliveryfee() + "/-");
                            minOrderValueTv.setText(modelShop.getCartValue() + "/-");
                            locationTv.setText(modelShop.getDeliveryLocation());
                            checkStatus();
                            statusTv.setText("Waiting For Approval");

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
                                Glide.with(getApplicationContext())
                                        .load(modelShop.getShopimage())
                                        .placeholder(shimmerDrawable)
                                        .into(shopImageIv);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            loadUsersProfileImage(modelShop);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
    }

    private void checkStatus() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ShopStatus");
        reference.child(shopId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    statusTv.setText("O P E N");
                    statusTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                } else {
                    statusTv.setText("C L O S E D");
                    statusTv.setTextColor(Color.RED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openGoogleMaps() {
        String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + shopName + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));

        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

        startActivity(intent);
    }

    private void callSeller() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void approveShop() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ShopsForApproval");
        reference.orderByChild("shopid").equalTo(shopId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelShop modelShop = ds.getValue(ModelShop.class);

                            if (modelShop.getShopid().equals(shopId)) {
                                Toast.makeText(ShopDetailsActivity.this, "Devesh", Toast.LENGTH_SHORT).show();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Shops");
                                ref.child(shopId).setValue(modelShop)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(ShopDetailsActivity.this, "Shop has been approved", Toast.LENGTH_SHORT).show();
                                            ShopDetailsActivity.super.onBackPressed();
                                            deleteFromShopApproval();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(ShopDetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deleteFromShopApproval() {
        FirebaseDatabase.getInstance().getReference("ShopsForApproval")
                .child(shopId).removeValue()
                .addOnSuccessListener(aVoid -> showBottomSheetDialogForEmail())
                .addOnFailureListener(e -> Toast.makeText(ShopDetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showBottomSheetDialogForEmail() {
        Toast.makeText(this, "Not fully Operational", Toast.LENGTH_SHORT).show();
        //Alert Dialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.alert_dialog_email, null);

        //UI view declaration and initialization.
        emailSubjectTv = view.findViewById(R.id.emailSubjectTv);
        emailMessageTv = view.findViewById(R.id.emailMessageTv);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);
        Button emailSendBtn = view.findViewById(R.id.emailSendBtn);
        ProgressBar emailSendPb = view.findViewById(R.id.emailSendPb);

        //setting the alert_dialog_logout.xml to alertDialog Builder
        builder.setView(view);

        //Setting up Alert Dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Cancel Btn Click Handler
        cancelBtn.setOnClickListener(v -> alertDialog.dismiss());

        //Email Send Btn Click Handler
        emailSendBtn.setOnClickListener(v -> {
            emailSendPb.setVisibility(View.VISIBLE);
            emailSendBtn.setVisibility(View.INVISIBLE);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                emailSendPb.setVisibility(View.GONE);
                emailSendBtn.setVisibility(View.VISIBLE);

                alertDialog.dismiss();
            }, 4000);
        });
        alertDialog.show();
    }

    private void loadShopForApprovalDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ShopsForApproval");
        reference.orderByChild("shopid").equalTo(shopId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //get data
                            ModelShop modelShop = ds.getValue(ModelShop.class);

                            //set data
                            latitude = modelShop.getLatitude();
                            longitude = modelShop.getLongitude();
                            shopName = modelShop.getShopname();
                            phone = modelShop.getShopphone();
                            nameTv.setText(modelShop.getYourname());
                            addressTv.setText(modelShop.getShopaddress());
                            shopNameTv.setText(shopName);
                            shopCityTv.setText(modelShop.getShopcity());
                            emailTv.setText(modelShop.getShopemail());
                            phoneTv.setText("+91 " + phone);
                            feeTv.setText(modelShop.getDeliveryfee() + "/-");
                            minOrderValueTv.setText(modelShop.getCartValue() + "/-");
                            locationTv.setText(modelShop.getDeliveryLocation());
                            statusTv.setText("Waiting For Approval");

                            Toast.makeText(ShopDetailsActivity.this, "" + modelShop.getShopname(), Toast.LENGTH_SHORT).show();

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
                                Glide.with(getApplicationContext())
                                        .load(modelShop.getShopimage())
                                        .placeholder(shimmerDrawable)
                                        .into(shopImageIv);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            shimmerDrawable.startShimmer();

                            loadUsersProfileImage(modelShop);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
    }

    private void loadUsersProfileImage(ModelShop modelShop) {
        //Shimmer Drawable
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        //Load users profile image
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .orderByChild("uid")
                .equalTo(modelShop.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String myDp = "" + ds.child("image").getValue();
                            try {
                                Glide.with(getApplicationContext()).load(myDp).placeholder(shimmerDrawable).into(profileIv);
                                if (myDp.equals("")) {
                                    Glide.with(getApplicationContext()).load(R.drawable.user).placeholder(shimmerDrawable).into(profileIv);
                                }
                            } catch (Exception e) {
                                Glide.with(getApplicationContext()).load(R.drawable.user).placeholder(shimmerDrawable).into(profileIv);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        shimmerDrawable.startShimmer();
    }
}