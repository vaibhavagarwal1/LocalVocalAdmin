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
import com.dev.localvocaladmin.adapters.AdapterRequestShop;
import com.dev.localvocaladmin.models.ModelShop;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestFragment extends Fragment {

    //context for fragments
    private Context context;
    //UI Views
    private RecyclerView shopsRv;
    private LinearLayout noShopsForApprovalLl;
    private ShimmerFrameLayout shimmerFl;
    //Shop List and Adapter
    private ArrayList<ModelShop> shopArrayList;
    private AdapterRequestShop adapterRequestShop;

    public RequestFragment() {
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
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        //init UI View
        shopsRv = view.findViewById(R.id.shopsRv);
        noShopsForApprovalLl = view.findViewById(R.id.noShopsForApprovalLl);
        shimmerFl = view.findViewById(R.id.shimmerFl);

        //init List
        shopArrayList = new ArrayList<>();

        shimmerFl.startShimmer();

        loadShopsForApproval();

        return view;
    }

    private void loadShopsForApproval() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ShopsForApproval");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shopArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()) {
                    shopsRv.setVisibility(View.VISIBLE);
                    //get Data
                    ModelShop modelShop = ds.getValue(ModelShop.class);
                    //add data to the array list
                    shopArrayList.add(modelShop);
                }
                //setup Adapter
                adapterRequestShop = new AdapterRequestShop(context, shopArrayList);
                if (adapterRequestShop.getItemCount() == 0) {
                    noShopsForApprovalLl.setVisibility(View.VISIBLE);
                } else {
                    noShopsForApprovalLl.setVisibility(View.GONE);
                }
                //setting it to RecyclerView
                shopsRv.setAdapter(adapterRequestShop);
                //shutting the shimmerLayout down and making its Visibility GONE
                shimmerFl.stopShimmer();
                shimmerFl.setVisibility(View.GONE);
                //update the adapter
                adapterRequestShop.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}