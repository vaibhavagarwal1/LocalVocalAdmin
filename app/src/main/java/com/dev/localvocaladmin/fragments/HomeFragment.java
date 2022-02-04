package com.dev.localvocaladmin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dev.localvocaladmin.R;
import com.dev.localvocaladmin.adapters.AdapterRequestShop;
import com.dev.localvocaladmin.models.ModelShop;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    //context for fragments
    private Context context;
    //UI Views
    private TextView totalShopsTv, totalProductsTv, totalUsersTv, totalRequestsTv;
    private ShimmerFrameLayout totalShopsSfl, totalProductsSfl, totalUsersSfl, totalRequestsSfl;
    private RelativeLayout totalRequestsRl;

    //Shop List and Adapter
    private ArrayList<ModelShop> shopArrayList;
    private AdapterRequestShop adapterRequestShop;

    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //init UI View
        totalShopsTv = view.findViewById(R.id.totalShopsTv);
        totalUsersTv = view.findViewById(R.id.totalUsersTv);
        totalProductsTv = view.findViewById(R.id.totalProductsTv);
        totalRequestsTv = view.findViewById(R.id.totalRequestsTv);
        totalShopsSfl = view.findViewById(R.id.totalShopsSfl);
        totalProductsSfl = view.findViewById(R.id.totalProductsSfl);
        totalUsersSfl = view.findViewById(R.id.totalUsersSfl);
        totalRequestsSfl = view.findViewById(R.id.totalRequestsSfl);
        totalRequestsRl = view.findViewById(R.id.totalRequestsRl);

        onResume();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Starting all the shimmer Frame Layouts
        totalShopsSfl.startShimmer();
        totalProductsSfl.startShimmer();
        totalUsersSfl.startShimmer();
        totalRequestsSfl.startShimmer();

        //load TotalShop
        loadTotalShops();

        //load Total Products
        loadTotalProducts();

        //load Total Users
        loadTotalUsers();

        //load Total Requests
        loadTotalRequests();

    }

    private void loadTotalRequests() {
        //Initializing Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ShopsForApproval");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Changing TotalShops Text View Visibility
                totalRequestsTv.setVisibility(View.VISIBLE);
                //Getting and putting total counts
                totalRequestsTv.setText("" + snapshot.getChildrenCount());
                //shutting the shimmerLayout down and making its Visibility GONE
                totalRequestsSfl.stopShimmer();
                totalRequestsSfl.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadTotalUsers() {
        //Initializing Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Changing TotalShops Text View Visibility
                totalUsersTv.setVisibility(View.VISIBLE);
                //Getting and putting total counts
                totalUsersTv.setText("" + snapshot.getChildrenCount());
                //shutting the shimmerLayout down and making its Visibility GONE
                totalUsersSfl.stopShimmer();
                totalUsersSfl.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadTotalProducts() {
        //Initializing Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Changing TotalShops Text View Visibility
                totalProductsTv.setVisibility(View.VISIBLE);
                //Getting and putting total counts
                totalProductsTv.setText("" + snapshot.getChildrenCount());
                //shutting the shimmerLayout down and making its Visibility GONE
                totalProductsSfl.stopShimmer();
                totalProductsSfl.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadTotalShops() {
        //Initializing Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Shops");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Changing TotalShops Text View Visibility
                totalShopsTv.setVisibility(View.VISIBLE);
                //Getting and putting total counts
                totalShopsTv.setText("" + snapshot.getChildrenCount());
                //shutting the shimmerLayout down and making its Visibility GONE
                totalShopsSfl.stopShimmer();
                totalShopsSfl.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}