package com.itstep.myapplication.manager;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.itstep.myapplication.R;
import com.itstep.myapplication.manager.AuthManager;
import com.itstep.myapplication.ui.found_items.FoundItemsFragment;
import com.itstep.myapplication.ui.login.LoginFragment;

/**
 * Singleton NavigationManager to handle all fragment navigation safely
 */
public class NavigationManager {
    private static final String TAG = "NavigationManager";
    private static NavigationManager instance;
    private AppCompatActivity activity;
    
    private NavigationManager() {
        // Private constructor to prevent direct instantiation
    }
    
    public static synchronized NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }
    
    public void initialize(AppCompatActivity activity) {
        this.activity = activity;
        Log.d(TAG, "NavigationManager initialized with activity: " + activity.getClass().getSimpleName());
    }
    
    /**
     * Navigate to login fragment safely
     */
    public void navigateToLogin() {
        try {
            if (!isActivityValid()) {
                Log.e(TAG, "Activity is not valid for navigation");
                return;
            }
            
            Log.d(TAG, "Starting navigation to LoginFragment");
            
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            
            // Clear backstack
            clearBackStack(fragmentManager);
            
            // Create and navigate to LoginFragment
            Fragment loginFragment = new LoginFragment();
            
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, loginFragment, "LOGIN");
            transaction.commitAllowingStateLoss();
            
            Log.d(TAG, "Successfully navigated to LoginFragment");
            showToast("Please login");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to navigate to login", e);
            showToast("Navigation error: " + e.getMessage());
        }
    }
    
    /**
     * Navigate to home fragment safely
     */
    public void navigateToHome() {
        try {
            if (!isActivityValid()) {
                Log.e(TAG, "Activity is not valid for navigation");
                return;
            }
            
            Log.d(TAG, "Starting navigation to FoundItemsFragment");
            
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            
            // Clear backstack
            clearBackStack(fragmentManager);
            
            // Create and navigate to FoundItemsFragment
            Fragment homeFragment = new FoundItemsFragment();
            
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, homeFragment, "HOME");
            transaction.commitAllowingStateLoss();
            
            Log.d(TAG, "Successfully navigated to FoundItemsFragment");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to navigate to home", e);
            showToast("Navigation error: " + e.getMessage());
        }
    }
    
    /**
     * Handle FAB click with login check - CRASH PROOF VERSION
     */
    public void handleFabClick() {
        Log.d(TAG, "=== FAB CLICKED (CRASH PROOF) ===");

        try {
            // Step 1: Validate activity
            if (activity == null) {
                Log.e(TAG, "Activity is null in handleFabClick");
                return;
            }

            if (activity.isFinishing() || activity.isDestroyed()) {
                Log.e(TAG, "Activity is finishing/destroyed in handleFabClick");
                return;
            }

            Log.d(TAG, "Activity validation passed");

            // Step 2: Try to get AuthManager safely
            AuthManager authManager = null;
            try {
                authManager = new AuthManager(activity);
                Log.d(TAG, "AuthManager obtained successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to get AuthManager", e);
                showToast("Authentication error");
                return;
            }

            if (authManager == null) {
                Log.e(TAG, "AuthManager is null");
                showToast("Authentication not available");
                return;
            }

            // Step 3: Check login status safely
            boolean isLoggedIn = false;
            try {
                isLoggedIn = authManager.isLoggedIn();
                Log.d(TAG, "Login status checked: " + isLoggedIn);
            } catch (Exception e) {
                Log.e(TAG, "Failed to check login status", e);
                showToast("Login check failed");
                return;
            }
            
            // Step 4: Handle based on login status
            if (isLoggedIn) {
                Log.d(TAG, "User is logged in - showing success message");
                showToast("User is logged in! (Post feature coming soon)");
            } else {
                Log.d(TAG, "User not logged in - navigating to login");
                // Use simple navigation instead of complex method
                navigateToLoginSimple();
            }

        } catch (Exception e) {
            Log.e(TAG, "FATAL ERROR in handleFabClick", e);
            try {
                showToast("App error: " + e.getClass().getSimpleName());
            } catch (Exception toastError) {
                Log.e(TAG, "Even toast failed", toastError);
            }
        }

        Log.d(TAG, "=== FAB CLICK END ===");
    }

    /**
     * Super simple navigation to login that cannot crash
     */
    private void navigateToLoginSimple() {
        try {
            Log.d(TAG, "navigateToLoginSimple called");

            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                Log.e(TAG, "Activity not valid for simple navigation");
                return;
            }

            // Create LoginFragment
            Fragment loginFragment = new LoginFragment();

            // Get fragment manager
            FragmentManager fm = activity.getSupportFragmentManager();

            // Simple transaction
            fm.beginTransaction()
                .replace(R.id.fragment_container, loginFragment)
                .commitAllowingStateLoss();

            Log.d(TAG, "Simple navigation to login completed");
            showToast("Please login");

        } catch (Exception e) {
            Log.e(TAG, "Simple navigation failed", e);
            showToast("Navigation failed");
        }
    }
    
    /**
     * Handle profile navigation with login check
     */
    public void handleProfileClick() {
        try {
            if (!isActivityValid()) {
                Log.e(TAG, "Activity is not valid for profile handling");
                return;
            }
            
            Log.d(TAG, "=== PROFILE CLICKED ===");
            
            AuthManager authManager = new AuthManager(activity);
            boolean isLoggedIn = authManager.isLoggedIn();
            
            Log.d(TAG, "User logged in for profile: " + isLoggedIn);
            
            if (isLoggedIn) {
                // User is logged in - show profile placeholder
                navigateToHome(); // For now, go to home until ProfileFragment is fixed
                showToast("Profile area (coming soon)");
                Log.d(TAG, "User logged in - showing profile placeholder");
            } else {
                // User is not logged in - navigate to login
                Log.d(TAG, "User not logged in for profile - navigating to login");
                navigateToLogin();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Profile click error", e);
            showToast("Profile error: " + e.getMessage());
        }
    }
    
    /**
     * Check if activity is valid for operations
     */
    private boolean isActivityValid() {
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }
    
    /**
     * Clear fragment backstack safely
     */
    private void clearBackStack(FragmentManager fragmentManager) {
        try {
            while (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();
            }
            Log.d(TAG, "Backstack cleared successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to clear backstack", e);
        }
    }
    
    /**
     * Show toast message safely
     */
    private void showToast(String message) {
        if (isActivityValid()) {
            activity.runOnUiThread(() -> {
                android.widget.Toast.makeText(activity, message, android.widget.Toast.LENGTH_SHORT).show();
            });
        }
    }
}
