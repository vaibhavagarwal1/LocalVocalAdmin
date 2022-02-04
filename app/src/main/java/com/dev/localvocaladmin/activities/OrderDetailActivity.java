package com.dev.localvocaladmin.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.localvocaladmin.R;
import com.dev.localvocaladmin.adapters.AdapterDetails;
import com.dev.localvocaladmin.models.ModelDetail;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private String orderId;
    private TextView oId, shopName, minOrder, deliveryFee, cartPrice, deliveryTo, orderStatus, orderTypePayment, noItems, cartValue, deliveryFeeShop, total;
    private RecyclerView cartRecycler;
    private ShimmerFrameLayout shimmerLayout2;
    private ImageView shopImage;
    private BottomSheetDialog bottomSheetDialog;

    private TextView cancel;

    private ArrayList<ModelDetail> cartList;
    private AdapterDetails adapterCart;

    private String shopId, oUid, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        setContentView(R.layout.activity_order_detail);

        //get String Data
        orderId = getIntent().getStringExtra("orderId");

        //init UI View
        oId = findViewById(R.id.orderId);
        shopName = findViewById(R.id.shopName);
        shopImage = findViewById(R.id.shopimageIv);
        minOrder = findViewById(R.id.minOrder);
        deliveryFee = findViewById(R.id.deliveryFee);
        cartRecycler = findViewById(R.id.cartRecycler);
        cartRecycler = findViewById(R.id.cartRecycler);
        shimmerLayout2 = findViewById(R.id.shimmerLayout2);
        cartPrice = findViewById(R.id.cartPrice);
        deliveryTo = findViewById(R.id.textView35);
        orderStatus = findViewById(R.id.textView29);
        orderTypePayment = findViewById(R.id.textView);
        noItems = findViewById(R.id.noItems);
        cartValue = findViewById(R.id.cartValue);
        deliveryFeeShop = findViewById(R.id.deliveryFeeShop);
        total = findViewById(R.id.total);
        cancel = findViewById(R.id.button);

        //cancel click handler
        cancel.setOnClickListener(v -> {
            final AlertDialog.Builder alert = new AlertDialog.Builder(OrderDetailActivity.this, R.style.AlertDialogTheme);
            View viewm = getLayoutInflater().inflate(R.layout.alert_dialog_cancel_order, null);

            Button cancel = viewm.findViewById(R.id.cancel_button);
            Button logout = viewm.findViewById(R.id.logout_btn);

            alert.setView(viewm);

            AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            cancel.setOnClickListener(view -> alertDialog.dismiss());

            logout.setOnClickListener(view -> {
                alertDialog.dismiss();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("orderStatus", "Cancelled");

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                reference.child(orderId).updateChildren(hashMap)
                        .addOnSuccessListener(aVoid -> Toast.makeText(OrderDetailActivity.this, "Status changed", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(OrderDetailActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());
            });
            alertDialog.show();
            alertDialog.getWindow().setLayout(700, 700);
        });

        findViewById(R.id.relativeLayout6).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ShopDetailsActivity.class);
            intent.putExtra("shopId", shopId);
            intent.putExtra("type", "Details");
            startActivity(intent);
        });

        cartList = new ArrayList<>();

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, true);
        layoutManager1.setStackFromEnd(true);
        cartRecycler.setLayoutManager(layoutManager1);

        shimmerLayout2.startShimmer();
        loadOrderDetails();

    }

    private void loadOrderDetails() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            cartRecycler.setVisibility(View.VISIBLE);
                            ModelDetail modelCart = ds.getValue(ModelDetail.class);
                            cartList.add(modelCart);
                        }
                        adapterCart = new AdapterDetails(getApplicationContext(), cartList);
                        cartRecycler.setAdapter(adapterCart);
                        shimmerLayout2.stopShimmer();
                        shimmerLayout2.setVisibility(View.GONE);
                        adapterCart.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Orders");
        reference1.orderByChild("orderId").equalTo(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String dFee = "" + ds.child("deliveryFee").getValue();
                            String dType = "" + ds.child("deliveryType").getValue();
                            String iCount = "" + ds.child("itemCount").getValue();
                            String oStatus = "" + ds.child("orderStatus").getValue();
                            oUid = "" + ds.child("ownerUid").getValue();
                            String pMethod = "" + ds.child("paymentMethod").getValue();
                            String pCost = "" + ds.child("productsCost").getValue();
                            String sAddress = "" + ds.child("shippingAddress").getValue();
                            shopId = "" + ds.child("shopId").getValue();
                            uid = "" + ds.child("uid").getValue();
                            String tPrice = "" + ds.child("totalPrice").getValue();

                            if (oUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                findViewById(R.id.changeStatus).setVisibility(View.VISIBLE);
                            } else {
                                if (oStatus.equals("Out for delivery") || oStatus.equals("Cancelled") || oStatus.equals("Delivered")) {
                                    cancel.setVisibility(View.GONE);
                                } else {
                                    cancel.setVisibility(View.VISIBLE);
                                }
                            }

                            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                            calendar.setTimeInMillis(Long.parseLong(orderId));
                            String dateTime = DateFormat.format("dd/MM/yyyy (hh:mm aa)", calendar).toString();

                            oId.setText("Order ID : #" + orderId + "\n" + dateTime);
                            cartPrice.setText("Rs. " + pCost);
                            deliveryTo.setText(sAddress);
                            orderStatus.setText(oStatus);
                            cartValue.setText("Rs. " + pCost);
                            deliveryFeeShop.setText("Rs. " + dFee + ".0");
                            total.setText("Rs. " + tPrice);
                            noItems.setText("(" + iCount + ")");

                            if (oStatus.equals("Received") || oStatus.equals("In Process") || oStatus.equals("Packing") || oStatus.equals("Packed") || oStatus.equals("Out for delivery") || oStatus.equals("Delivered")) {
                                orderStatus.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.light_green));
                                orderStatus.setTextColor(getApplicationContext().getResources().getColor(R.color.colorGreen));
                            }
                            if (oStatus.equals("Cancelled")) {
                                orderStatus.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.light_yellow));
                                orderStatus.setTextColor(getApplicationContext().getResources().getColor(R.color.dark_yellow));
                            }
                            if (oStatus.equals("Failed")) {
                                orderStatus.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.light_red));
                                orderStatus.setTextColor(getApplicationContext().getResources().getColor(R.color.dark_red));
                            }

                            if (dType.equals("Pick Up")) {
                                orderTypePayment.setText("[" + dType + "]    [Cash on Pick Up (COP)]");
                                findViewById(R.id.textView33).setVisibility(View.GONE);
                                findViewById(R.id.textView35).setVisibility(View.GONE);
                                findViewById(R.id.view1).setVisibility(View.GONE);
                            } else {
                                orderTypePayment.setText("[" + dType + "]    [" + pMethod + "]");
                            }

                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Shops");
                            reference2.orderByChild("shopid").equalTo(shopId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        String sImage = "" + ds.child("shopimage").getValue();
                                        String sName = "" + ds.child("shopname").getValue();
                                        String minO = "" + ds.child("cartValue").getValue();
                                        String dFee = "" + ds.child("deliveryfee").getValue();

                                        shopName.setText(sName);
                                        minOrder.setText("Minimum order value : Rs. " + minO);
                                        deliveryFee.setText("Delivery fee : Rs. " + dFee);

                                        try {
                                            Glide.with(getApplicationContext()).load(sImage).centerCrop().into(shopImage);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void back(View view) {
        OrderDetailActivity.super.onBackPressed();
    }
}