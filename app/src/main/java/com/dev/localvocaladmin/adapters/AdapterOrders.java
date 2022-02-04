package com.dev.localvocaladmin.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.localvocaladmin.R;
import com.dev.localvocaladmin.activities.OrderDetailActivity;
import com.dev.localvocaladmin.models.ModelOrders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterOrders extends RecyclerView.Adapter<AdapterOrders.HolderOrders> {

    private final Context context;
    public ArrayList<ModelOrders> ordersList;
    //String
    private String orderBy, orderTo;

    public AdapterOrders(Context context, ArrayList<ModelOrders> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public HolderOrders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_orders, parent, false);
        return new HolderOrders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrders holder, int position) {
        String orderStatus = ordersList.get(position).getOrderStatus();
        String orderId = ordersList.get(position).getOrderId();
        String shippingAddress = ordersList.get(position).getShippingAddress();
        String orderDate = ordersList.get(position).getOrderId();
        String totalPrice = ordersList.get(position).getTotalPrice();
        orderTo = ordersList.get(position).getShopId();
        orderBy = ordersList.get(position).getUid();

        holder.orderStatusTv.setText(orderStatus);
        holder.orderIdTv.setText("#" + orderId);
        holder.deliveryToTv.setText(shippingAddress);
        holder.totalCostTv.setText("Rs. " + totalPrice);

        if (orderStatus.equals("Received") ||
                orderStatus.equals("In Process") ||
                orderStatus.equals("Packing") ||
                orderStatus.equals("Packed") ||
                orderStatus.equals("Out for delivery") ||
                orderStatus.equals("Delivered")) {
            holder.orderStatusTv.setBackgroundColor(context.getResources().getColor(R.color.light_green));
            holder.orderStatusTv.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }
        if (orderStatus.equals("Cancelled")) {
            holder.orderStatusTv.setBackgroundColor(context.getResources().getColor(R.color.light_yellow));
            holder.orderStatusTv.setTextColor(context.getResources().getColor(R.color.dark_yellow));
        }
        if (orderStatus.equals("Failed")) {
            holder.orderStatusTv.setBackgroundColor(context.getResources().getColor(R.color.light_red));
            holder.orderStatusTv.setTextColor(context.getResources().getColor(R.color.dark_red));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", orderId);
            context.startActivity(intent);
        });

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(orderDate));
        String dateTime = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.dateTv.setText(dateTime);

        //load Shop Name
        loadShopName(holder);
        //load User Name
        loadUserName(holder);
    }

    private void loadShopName(HolderOrders holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Shops");
        reference.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        holder.orderToTv.setText("" + snapshot.child("shopname").getValue());
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void loadUserName(HolderOrders holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        holder.orderByTv.setText("" + snapshot.child("name").getValue());
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    class HolderOrders extends RecyclerView.ViewHolder {

        //UI Views
        private final TextView orderStatusTv;
        private final TextView orderIdTv;
        private final TextView deliveryToTv;
        private final TextView dateTv;
        private final TextView totalCostTv;
        private final TextView orderToTv;
        private final TextView orderByTv;

        public HolderOrders(@NonNull View itemView) {
            super(itemView);

            orderStatusTv = itemView.findViewById(R.id.textView29);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            deliveryToTv = itemView.findViewById(R.id.deliveryTo);
            dateTv = itemView.findViewById(R.id.time);
            totalCostTv = itemView.findViewById(R.id.totalCost);
            orderByTv = itemView.findViewById(R.id.orderByTv);
            orderToTv = itemView.findViewById(R.id.orderToTv);

        }
    }
}
