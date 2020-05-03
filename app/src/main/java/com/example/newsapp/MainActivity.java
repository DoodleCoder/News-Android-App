package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_ID = 44;
    private BottomNavigationView mMainNav;
    private FrameLayout mFrame;


    private HomeFragment homeFragment;
    private HeadlinesFragment headlinesFragment;
    private TrendingFragment trendingFragment;
    protected BookmarksFragment bookmarksFragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        mFrame = (FrameLayout) findViewById(R.id.main_appframe);
        mMainNav = (BottomNavigationView) findViewById(R.id.bottom_navbar);

        homeFragment = new HomeFragment();
        headlinesFragment = new HeadlinesFragment();
        trendingFragment = new TrendingFragment();
        bookmarksFragment = new BookmarksFragment();

        setFragment(homeFragment);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottom_nav_home:
                        setFragment(homeFragment);
                        return true;

                    case R.id.bottom_nav_headlines:
                        setFragment(headlinesFragment);
                        return  true;

                    case R.id.bottom_nav_trending:
                        setFragment(trendingFragment);
                        return  true;

                    case R.id.bottom_nav_bookmarks:
                        setFragment(bookmarksFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(Fragment f) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_appframe, f);
        fragmentTransaction.commit();
    }

}
