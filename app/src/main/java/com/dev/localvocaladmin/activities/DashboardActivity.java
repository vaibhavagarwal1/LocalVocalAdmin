package com.dev.localvocaladmin.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dev.localvocaladmin.R;
import com.dev.localvocaladmin.fragments.HomeFragment;
import com.dev.localvocaladmin.fragments.OrdersFragment;
import com.dev.localvocaladmin.fragments.RequestFragment;
import com.dev.localvocaladmin.fragments.ShopsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //UI Views
    private TextView titleTv;
    private ImageButton logoutBtn;
    private FrameLayout frameFl;
    private BottomNavigationView bottomNv;

    //Fragments
    private Fragment homeFragment, requestFragment, shopsFragment, ordersFragment;
    private Fragment activeFragment;
    private FragmentManager fragmentManager;

    //FirebaseAuth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //init UI Views
        titleTv = findViewById(R.id.titleTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        frameFl = findViewById(R.id.frameFl);
        bottomNv = findViewById(R.id.bottomNv);

        //init Fragments
        initFragments();

        //init FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //LogoutBtn Click Handler
        logoutBtn.setOnClickListener(v -> logout());

        //bottom Navigation Item Select Handler
        bottomNv.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

    }

    private void logout() {
        //Alert Dialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.alert_dialog_logout, null);

        //UI view declaration and initialization.
        Button cancelBtn = view.findViewById(R.id.cancelBtn);
        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        ProgressBar logoutPb = view.findViewById(R.id.logoutPb);

        //setting the alert_dialog_logout.xml to alertDialog Builder
        builder.setView(view);

        //Setting up Alert Dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Cancel Btn Click Handler
        cancelBtn.setOnClickListener(v -> alertDialog.dismiss());

        //Logout Btn Click Handler
        logoutBtn.setOnClickListener(v -> {
            logoutPb.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.INVISIBLE);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                logoutPb.setVisibility(View.GONE);
                logoutBtn.setVisibility(View.VISIBLE);
                firebaseAuth.signOut();
                checkUserStatus();
                alertDialog.dismiss();
            }, 4000);
        });
        alertDialog.show();
        alertDialog.getWindow().setLayout(800, 800);
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void initFragments() {
        //init Fragments
        homeFragment = new HomeFragment();
        requestFragment = new RequestFragment();
        shopsFragment = new ShopsFragment();
        ordersFragment = new OrdersFragment();

        //init FragmentManger
        fragmentManager = getSupportFragmentManager();
        //setting up Active Fragment
        activeFragment = homeFragment;

        //Default Fragment Status
        fragmentManager.beginTransaction()
                .add(R.id.frameFl, homeFragment, "Home Fragment")
                .commit();

        fragmentManager.beginTransaction()
                .add(R.id.frameFl, shopsFragment, "Shops Fragment")
                .hide(shopsFragment)
                .commit();

        fragmentManager.beginTransaction()
                .add(R.id.frameFl, ordersFragment, "Order Fragment")
                .hide(ordersFragment)
                .commit();

        fragmentManager.beginTransaction()
                .add(R.id.frameFl, requestFragment, "Request Fragment")
                .hide(requestFragment)
                .commit();
    }

    private void loadHomeFragment() {
        titleTv.setText("Home");
        fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
        activeFragment = homeFragment;
    }

    private void loadShopsFragment() {
        titleTv.setText("Shops List");
        fragmentManager.beginTransaction().hide(activeFragment).show(shopsFragment).commit();
        activeFragment = shopsFragment;
    }

    private void loadOrderFragment() {
        titleTv.setText("Orders");
        fragmentManager.beginTransaction().hide(activeFragment).show(ordersFragment).commit();
        activeFragment = ordersFragment;
    }

    public void loadRequestFragment() {
        titleTv.setText("Requests");
        fragmentManager.beginTransaction().hide(activeFragment).show(requestFragment).commit();
        activeFragment = requestFragment;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //handle bottom navigation item clicks
        switch (item.getItemId()) {
            case R.id.nav_home: {
                //load Home Data
                loadHomeFragment();
                return true;
            }
            case R.id.nav_shops: {
                //load Shops Data
                loadShopsFragment();
                return true;
            }
            case R.id.nav_orders: {
                //load Orders Data
                loadOrderFragment();
                return true;
            }
            case R.id.nav_request: {
                //load Request Data
                loadRequestFragment();
                return true;
            }
        }
        return false;
    }


}