package com.mlt.mad_lab_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE);

        // Setup bottom navigation
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Load the default fragment (Profile)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                } else if (itemId == R.id.nav_apps) {
                    selectedFragment = new AppsFragment();
                } else if (itemId == R.id.nav_games) {
                    selectedFragment = new GamesFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            };

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh profile data when returning to activity
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof ProfileFragment) {
            ((ProfileFragment) currentFragment).displayUserData(
                    sharedPreferences,
                    ((ProfileFragment) currentFragment).isPasswordVisible
            );
        }
    }

    // Helper method to get current user (can be used by fragments)
    public User getCurrentUser() {
        String userJson = sharedPreferences.getString("current_user", null);
        if (userJson != null) {
            return new Gson().fromJson(userJson, User.class);
        }
        return null;
    }
}