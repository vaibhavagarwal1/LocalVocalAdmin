package com.dev.localvocaladmin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.localvocaladmin.R;
import com.dev.localvocaladmin.adapters.AdapterOrders;
import com.dev.localvocaladmin.models.ModelOrders;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrdersFragment extends Fragment {

    //context for fragments
    private Context context;

    //UI Views
    private RecyclerView ordersRv;
    private ShimmerFrameLayout ordersSfl;
    private LinearLayout noOrdersLl;

    //Orders List and Adapter
    private ArrayList<ModelOrders> ordersList;
    private AdapterOrders adapterOrders;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        //init UI Views
        ordersRv = view.findViewById(R.id.ordersRv);
        ordersSfl = view.findViewById(R.id.ordersSfl);
        noOrdersLl = view.findViewById(R.id.noOrdersLl);

        //init Orders ArrayList
        ordersList = new ArrayList<>();

        //start shimmer Layout
        ordersSfl.startShimmer();

        //load Orders
        loadOrders();

        return view;
    }

    private void loadOrders() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ordersRv.setVisibility(View.VISIBLE);
                    //Toast.makeText(context, "" + ds.getValue(ModelOrders.class), Toast.LENGTH_SHORT).show();
                    ModelOrders modelOrders = ds.getValue(ModelOrders.class);
                    ordersList.add(modelOrders);
                }
                adapterOrders = new AdapterOrders(context, ordersList);
                if (adapterOrders.getItemCount() == 0) {
                    noOrdersLl.setVisibility(View.VISIBLE);
                } else {
                    noOrdersLl.setVisibility(View.GONE);
                }
                ordersRv.setAdapter(adapterOrders);
                ordersSfl.setVisibility(View.GONE);
                ordersSfl.stopShimmer();
                adapterOrders.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}