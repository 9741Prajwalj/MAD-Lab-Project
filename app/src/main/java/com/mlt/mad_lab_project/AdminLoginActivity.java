package com.mlt.mad_lab_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etAdminLoginEmail, etAdminLoginPassword;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private static final String ADMIN_PREFS = "admin_data";
    private static final String ADMIN_LIST_KEY = "admin_list";
    private static final String CURRENT_ADMIN = "current_admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        etAdminLoginEmail = findViewById(R.id.etAdminLoginEmail);
        etAdminLoginPassword = findViewById(R.id.etAdminLoginPassword);
        Button btnAdminLogin = findViewById(R.id.btnAdminLogin);
        TextView tvUserLogin = findViewById(R.id.tvUserLogin);

        sharedPreferences = getSharedPreferences(ADMIN_PREFS, MODE_PRIVATE);

        btnAdminLogin.setOnClickListener(v -> handleAdminLogin());
        tvUserLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void handleAdminLogin() {
        String email = etAdminLoginEmail.getText().toString().trim().toLowerCase();
        String password = etAdminLoginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Admin> adminList = getAdminList();
        Admin authenticatedAdmin = authenticateAdmin(adminList, email, password);

        if (authenticatedAdmin != null) {
            saveCurrentAdmin(authenticatedAdmin);
            Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminHomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Admin> getAdminList() {
        String json = sharedPreferences.getString(ADMIN_LIST_KEY, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Admin>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private Admin authenticateAdmin(List<Admin> adminList, String email, String password) {
        for (Admin admin : adminList) {
            if (admin.getEmail().equalsIgnoreCase(email) &&
                    admin.getPassword().equals(hashPassword(password))) {
                return admin;
            }
        }
        return null;
    }

    private void saveCurrentAdmin(Admin admin) {
        String adminJson = gson.toJson(admin);
        sharedPreferences.edit()
                .putString(CURRENT_ADMIN, adminJson)
                .apply();
    }

    private String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(hash);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
        return plainPassword;
    }
}