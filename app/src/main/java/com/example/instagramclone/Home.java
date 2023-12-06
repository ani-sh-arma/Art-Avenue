package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.instagramclone.Fragments.HomeFragment;
import com.example.instagramclone.Fragments.NotificationFragment;
import com.example.instagramclone.Fragments.ProfileFragment;
import com.example.instagramclone.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.nav_home) {
                    selectorFragments = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_search) {
                    selectorFragments = new SearchFragment();
                } else if (item.getItemId() == R.id.nav_add) {
                    selectorFragments = null;
                    startActivity(new Intent(Home.this , PostActivity.class));
                } else if (item.getItemId() == R.id.nav_favourite) {
                    selectorFragments = new NotificationFragment();
                } else if (item.getItemId() == R.id.nav_profile) {
                    selectorFragments = new ProfileFragment();
                }

                if (selectorFragments != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , selectorFragments).commit();
                }

                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new HomeFragment()).commit();

    }
}