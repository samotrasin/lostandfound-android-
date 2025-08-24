package com.itstep.myapplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itstep.myapplication.Model.User;
import com.itstep.myapplication.R;
import com.itstep.myapplication.SplashScreenActivity;
import com.itstep.myapplication.manager.AuthManager;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ProfileViewModel mViewModel;
    private AuthManager authManager;

    // UI Components
    private TextView userNameText;
    private TextView userEmailText;
    private TextView userPhoneText;
    private TextView joinedDateText;
    private MaterialButton editProfileButton;
    private MaterialButton logoutButton;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize AuthManager
        authManager = new AuthManager(requireContext());

        // Check if user is logged in
        if (!authManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        initializeViews(view);
        setupClickListeners();
        loadUserProfile();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    private void initializeViews(View view) {
        // Update to use the correct IDs from the layout
        userNameText = view.findViewById(R.id.profile_username);
        userEmailText = view.findViewById(R.id.user_email_text); // This might not exist in layout
        userPhoneText = view.findViewById(R.id.user_phone_text); // This might not exist in layout
        joinedDateText = view.findViewById(R.id.joined_date_textview);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        logoutButton = view.findViewById(R.id.logout_button);

        // Additional buttons from the layout
        MaterialButton listedItemButton = view.findViewById(R.id.listed_item_button);
        MaterialButton loginButton = view.findViewById(R.id.login_button);

        // Set up listed items button click
        if (listedItemButton != null) {
            listedItemButton.setOnClickListener(v -> navigateToListedItems());
        }

        // Hide login button if user is logged in, show logout button
        if (authManager.isLoggedIn()) {
            if (loginButton != null) loginButton.setVisibility(View.GONE);
            if (logoutButton != null) logoutButton.setVisibility(View.VISIBLE);
        } else {
            if (loginButton != null) {
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setOnClickListener(v -> redirectToLogin());
            }
            if (logoutButton != null) logoutButton.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        if (editProfileButton != null) {
            editProfileButton.setOnClickListener(v -> navigateToEditProfile());
        }

        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> showLogoutConfirmation());
        }
    }

    private void loadUserProfile() {
        User currentUser = authManager.getCurrentUser();
        if (currentUser != null) {
            displayUserInfo(currentUser);
        } else {
            Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        }
    }

    private void displayUserInfo(User user) {
        if (userNameText != null) {
            userNameText.setText(user.getName() != null ? user.getName() : "Unknown User");
        }

        if (userEmailText != null) {
            userEmailText.setText(user.getEmail() != null ? user.getEmail() : "No email");
        }

        if (userPhoneText != null) {
            userPhoneText.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "No phone");
        }

        if (joinedDateText != null) {
            // Format the joined date if available
            if (user.getJoinedDate() != null) {
                joinedDateText.setText("Joined: " + user.getJoinedDate().toString());
            } else {
                joinedDateText.setText("Member since registration");
            }
        }
    }

    private void navigateToEditProfile() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new com.itstep.myapplication.ui.profile_settings.ProfileSettingsFragment())
                .addToBackStack(null)
                .commit();
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performLogout() {
        try {
            authManager.logout();
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            Log.d(TAG, "User logged out successfully");
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error during logout", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error during logout", e);
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void navigateToListedItems() {
        // Navigate to user's listed items (create this fragment if it doesn't exist)
        // For now, show a toast - you can implement this later
        Toast.makeText(getContext(), "Listed Items feature coming soon!", Toast.LENGTH_SHORT).show();

        // TODO: Implement navigation to user's posted items
        // Example:
        // MyItemsFragment myItemsFragment = MyItemsFragment.newInstance();
        // requireActivity().getSupportFragmentManager()
        //         .beginTransaction()
        //         .replace(R.id.fragment_container, myItemsFragment)
        //         .addToBackStack(null)
        //         .commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check authentication status and refresh profile
        if (authManager != null && authManager.isLoggedIn()) {
            loadUserProfile();
            // Update button visibility on resume
            updateButtonVisibility();
        } else {
            redirectToLogin();
        }
    }

    private void updateButtonVisibility() {
        View view = getView();
        if (view != null) {
            MaterialButton loginButton = view.findViewById(R.id.login_button);
            MaterialButton logoutButton = view.findViewById(R.id.logout_button);

            if (authManager.isLoggedIn()) {
                if (loginButton != null) loginButton.setVisibility(View.GONE);
                if (logoutButton != null) logoutButton.setVisibility(View.VISIBLE);
            } else {
                if (loginButton != null) loginButton.setVisibility(View.VISIBLE);
                if (logoutButton != null) logoutButton.setVisibility(View.GONE);
            }
        }
    }
}