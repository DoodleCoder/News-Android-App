package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
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
