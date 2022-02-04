package com.dev.localvocaladmin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.localvocaladmin.R;
import com.dev.localvocaladmin.adapters.AdapterShops;
import com.dev.localvocaladmin.models.ModelShop;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class ShopsFragment extends Fragment {

    //context for fragments
    private Context context;
    //UI Views
    private RecyclerView shopsRv;
    private LinearLayout noShopsLl;
    private ShimmerFrameLayout shimmerFl;
    private RelativeLayout searchRl;
    private EditText searchShopsEt;
    private ImageButton filterBtn;
    private TextView filterShopsTv;
    //Shop List and Adapter
    private ArrayList<ModelShop> shopArrayList;
    private AdapterShops adapterShops;
    //City List
    private Set<String> citySet;
    private ArrayList<String> cityArrayList;

    public ShopsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_shops, container, false);

        //init UI View
        shopsRv = view.findViewById(R.id.shopsRv);
        noShopsLl = view.findViewById(R.id.noShopsLl);
        shimmerFl = view.findViewById(R.id.shimmerFl);
        searchRl = view.findViewById(R.id.searchRl);
        searchShopsEt = view.findViewById(R.id.searchShopsEt);
        filterBtn = view.findViewById(R.id.filterBtn);
        filterShopsTv = view.findViewById(R.id.filterShopsTv);

        //init List
        shopArrayList = new ArrayList<>();
        cityArrayList = new ArrayList<>();

        //start shimmering
        shimmerFl.startShimmer();

        //load All Shops
        loadShops();

        //filterBtn Click Handler
        filterBtn.setOnClickListener(v -> {
            //show alertDialog
            loadAllCitiesList();
        });

        //search Handler
        searchShopsEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterShops.getFilter().filter(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void loadAllCitiesList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose City")
                .setItems(cityArrayList.toArray(new String[0]), (dialogInterface, i) -> {
                    String city = cityArrayList.get(i);
                    if (city.equals("All")) {
                        shimmerFl.setVisibility(View.VISIBLE);
                        shimmerFl.startShimmer();
                        filterShopsTv.setText("Showing All");
                        loadShops();
                    } else {
                        shimmerFl.setVisibility(View.VISIBLE);
                        shimmerFl.startShimmer();
                        loadFilteredShops(city);
                    }
                }).show();
    }

    private void loadFilteredShops(String city) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Shops");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shopArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    shopsRv.setVisibility(View.VISIBLE);
                    //get Data
                    ModelShop modelShop = ds.getValue(ModelShop.class);
                    if (modelShop.getShopcity().equals(city)) {
                        //add data to the array list
                        shopArrayList.add(modelShop);
                    }
                    filterShopsTv.setText(city);
                }
                //setup Adapter
                adapterShops = new AdapterShops(context, shopArrayList);
                if (adapterShops.getItemCount() == 0) {
                    noShopsLl.setVisibility(View.VISIBLE);
                    searchRl.setVisibility(View.GONE);
                } else {
                    noShopsLl.setVisibility(View.GONE);
                    searchRl.setVisibility(View.VISIBLE);
                }
                //setting it to RecyclerView
                shopsRv.setAdapter(adapterShops);
                //shutting the shimmerLayout down and making its Visibility GONE
                shimmerFl.stopShimmer();
                shimmerFl.setVisibility(View.GONE);
                //update the adapter
                adapterShops.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadShops() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Shops");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shopArrayList.clear();
                cityArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    shopsRv.setVisibility(View.VISIBLE);
                    //get Data
                    ModelShop modelShop = ds.getValue(ModelShop.class);
                    //add data to the array list
                    shopArrayList.add(modelShop);
                    cityArrayList.add(modelShop.getShopcity());
                    filterShopsTv.setText("Showing All");
                }
                citySet = new TreeSet<>(cityArrayList);
                cityArrayList.clear();
                cityArrayList.addAll(citySet);
                cityArrayList.add(0, "All");
                //setup Adapter
                adapterShops = new AdapterShops(context, shopArrayList);
                if (adapterShops.getItemCount() == 0) {
                    noShopsLl.setVisibility(View.VISIBLE);
                    searchRl.setVisibility(View.GONE);
                } else {
                    noShopsLl.setVisibility(View.GONE);
                    searchRl.setVisibility(View.VISIBLE);
                }
                //setting it to RecyclerView
                shopsRv.setAdapter(adapterShops);
                //shutting the shimmerLayout down and making its Visibility GONE
                shimmerFl.stopShimmer();
                shimmerFl.setVisibility(View.GONE);
                //update the adapter
                adapterShops.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}