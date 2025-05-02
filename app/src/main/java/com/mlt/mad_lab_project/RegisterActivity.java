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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegisterActivity extends AppCompatActivity {

    private static final String USER_PREFS = "user_data";  // Must be identical
    private static final String USER_LIST_KEY = "user_list";
    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE);

        btnRegister.setOnClickListener(v -> handleRegistration());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        TextView tvAdminRegister = findViewById(R.id.tvAdminRegister);
        tvAdminRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminRegisterActivity.class));
            finish();
        });
    }

    private void handleRegistration() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address");
            return;
        }

        if (password.length() < 4 ) {
            showToast("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords don't match");
            return;
        }

        List<User> userList = getUserList();
        Log.d("RegisterDebug", "Current users before add: " + userList);  // Debug
        User newUser = new User(name, email, password);

        if (userList.contains(newUser)) {
            showToast("Email already registered");
            return;
        }

        userList.add(new User(name, email, hashPassword(password)));
        saveUserList(userList);

        showToast("Registration successful");
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private List<User> getUserList() {
        String json = sharedPreferences.getString(USER_LIST_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<User>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void saveUserList(List<User> userList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(userList);
        editor.putString(USER_LIST_KEY, json);
        editor.apply();  // Make sure to use apply() or commit()

        // DEBUG: Verify saved data
        Log.d("RegisterDebug", "Saved users: " + json);
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