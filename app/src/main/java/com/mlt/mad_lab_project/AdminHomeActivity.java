package com.mlt.mad_lab_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences userSharedPreferences;
    private Gson gson = new Gson();
    private static final String USER_PREFS = "user_data";
    private static final String USER_LIST_KEY = "user_list";
    private static final String ADMIN_PREFS = "admin_data";
    private static final String ADMIN_LIST_KEY = "admin_list";
    private static final String CURRENT_ADMIN = "current_admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Initialize SharedPreferences for USER data
        userSharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE);

        TextView tvWelcomeAdmin = findViewById(R.id.tvWelcomeAdmin);
        rvUsers = findViewById(R.id.rvUsers);
        Button btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        Button btnLogout = findViewById(R.id.btnLogout);

        sharedPreferences = getSharedPreferences(ADMIN_PREFS, MODE_PRIVATE);
        String adminJson = sharedPreferences.getString(CURRENT_ADMIN, null);
        Admin currentAdmin = gson.fromJson(adminJson, Admin.class);

        if (currentAdmin != null) {
            tvWelcomeAdmin.setText("Welcome,to AppSuite Admin 😎❤️" + currentAdmin.getName());
        }

        // Setup RecyclerView
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        List<User> userList = getUserList();
        userAdapter = new UserAdapter(userList);
        rvUsers.setAdapter(userAdapter);

        btnLogout.setOnClickListener(v -> {
            sharedPreferences.edit().remove(CURRENT_ADMIN).apply();
            startActivity(new Intent(this, AdminLoginActivity.class));
            finish();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            if (currentAdmin != null) {
                deleteAdminAccount(currentAdmin);
            }
        });
//        Button btnDebug = findViewById(R.id.btnDebug);
//        btnDebug.setOnClickListener(v -> {
//            List<User> users = getUsersFromSharedPreferences();
//            Toast.makeText(this, "Total users: " + users.size(), Toast.LENGTH_SHORT).show();
//            for(User u : users) {
//                Log.d("UserDebug", u.getName() + " - " + u.getEmail());
//            }
//        });
        loadUserData();
    }

    private List<User> getUserList() {
        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String json = userPrefs.getString(USER_LIST_KEY, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<User>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void deleteAdminAccount(Admin admin) {
        List<Admin> adminList = getAdminList();
        adminList.remove(admin);
        saveAdminList(adminList);

        sharedPreferences.edit()
                .remove(CURRENT_ADMIN)
                .apply();

        Toast.makeText(this, "Admin account deleted", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, AdminLoginActivity.class));
        finish();
    }

    private List<Admin> getAdminList() {
        String json = sharedPreferences.getString(ADMIN_LIST_KEY, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Admin>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void saveAdminList(List<Admin> adminList) {
        String json = gson.toJson(adminList);
        sharedPreferences.edit()
                .putString(ADMIN_LIST_KEY, json)
                .apply();
    }
    private void loadUserData() {
        List<User> userList = getUsersFromSharedPreferences();

        if(userList.isEmpty()) {
            Toast.makeText(this, "No registered users found", Toast.LENGTH_SHORT).show();
        } else {
            userAdapter = new UserAdapter(userList);
            rvUsers.setAdapter(userAdapter);
        }
    }

    private List<User> getUsersFromSharedPreferences() {
        String json = userSharedPreferences.getString("user_list", null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<User>>(){}.getType();
        return gson.fromJson(json, type);
    }
}