package com.mlt.mad_lab_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

public class ProfileFragment extends Fragment {
    private TextView tvWelcome, tvUserDetails;
    private MaterialButton btnTogglePassword;
    boolean isPasswordVisible = false;
    private String realPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views from fragment_profile.xml
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvUserDetails = view.findViewById(R.id.tvUserDetails);
        btnTogglePassword = view.findViewById(R.id.btnTogglePassword);
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);

        // Get SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("USER_PREFS", 0);

        // Load user data
        displayUserData(sharedPreferences, false);

        // Toggle password visibility
        btnTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            displayUserData(sharedPreferences, isPasswordVisible);
            btnTogglePassword.setIconResource(isPasswordVisible ?
                    R.drawable.ic_visibility : R.drawable.ic_visibility_off);
            btnTogglePassword.setText(isPasswordVisible ?
                    "Hide Password" : "Show Password");
        });

        // Logout button
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("current_user");
            editor.apply();
            requireActivity().finish();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });

        return view;
    }

    void displayUserData(SharedPreferences sharedPreferences, boolean showPassword) {
        String userJson = sharedPreferences.getString("current_user", null);

        if (userJson == null) {
            tvWelcome.setText("Welcome!");
            tvUserDetails.setText("User data not found.");
            return;
        }

        User currentUser = new Gson().fromJson(userJson, User.class);
        String name = currentUser.getName();
        String email = currentUser.getEmail();
        realPassword = currentUser.getPassword();

        String passwordDisplay = showPassword ? realPassword : maskPassword(realPassword);

        String userDetails = "Account Details:\n\n" +
                "Name: " + name + "\n" +
                "Email: " + email + "\n" +
                "Password: " + passwordDisplay;

        tvWelcome.setText("Welcome,to AppSuite 😁😉" + name + "!");
        tvUserDetails.setText(userDetails);
    }

    private String maskPassword(String password) {
        return "••••••••";
    }
}