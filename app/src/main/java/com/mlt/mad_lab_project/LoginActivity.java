package com.mlt.mad_lab_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {

    private static final String USER_PREFS = "user_data";  // Must be identical
    private static final String USER_LIST_KEY = "user_list";
    private static final String CURRENT_USER = "current_user";
    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE); // ✅ Initialize first
        Log.d("PrefsDebug", "All prefs: " + sharedPreferences.getAll().toString()); // ✅ Safe now

        setContentView(R.layout.activity_login);

        // Initialize views
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> handleLogin());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void handleLogin() {
        String email = etLoginEmail.getText().toString().trim().toLowerCase();
        String password = etLoginPassword.getText().toString().trim();

        // Debug: Print input values
        Log.d("LoginDebug", "Attempting login with email: " + email);
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        List<User> userList = getUserList();
        // Debug: Print all registered users
        Log.d("LoginDebug", "Registered users: " + userList.toString());
        User authenticatedUser = authenticateUser(userList, email, password);

        if (authenticatedUser != null) {
            Log.d("LoginDebug", "Authentication successful for: " + authenticatedUser.getEmail());
            saveCurrentUser(authenticatedUser);
            showToast("Login successful");
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            showToast("Invalid credentials");
        }
    }

    private List<User> getUserList() {
        String json = sharedPreferences.getString(USER_LIST_KEY, null);
        Log.d("LoginDebug", "Retrieved JSON: " + json);  // Debug
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<User>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private User authenticateUser(List<User> userList, String email, String password) {
        for (User user : userList) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                // First check email (case insensitive), then password (case sensitive)
                if (user.getPassword().equals(hashPassword(password))) {
                    return user;
                } else {
                    // Password mismatch
                    return null;
                }
            }
        }
        return null; // Email not found
    }

    private void saveCurrentUser(User user) {
        String userJson = gson.toJson(user);
        sharedPreferences.edit()
                .putString(CURRENT_USER, userJson)
                .apply();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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