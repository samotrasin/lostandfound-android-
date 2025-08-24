package com.itstep.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itstep.myapplication.manager.UserSession;
import com.itstep.myapplication.ui.login.LoginFragment;
import com.itstep.myapplication.ui.lost_items.LostItemsFragment;
import com.itstep.myapplication.ui.found_items.FoundItemsFragment;
import com.itstep.myapplication.ui.post_items.PostItemFragment;
import com.itstep.myapplication.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private TextView fragmentTitle;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize user session
        userSession = new UserSession(this);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentTitle = findViewById(R.id.fragment_title);

        // Set navigation listener
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Check if user is logged in
        if (userSession.isLoggedIn()) {
            // User is logged in, show main app with bottom navigation
            bottomNavigationView.setVisibility(android.view.View.VISIBLE);

            // Load default fragment (Lost Items)
            if (savedInstanceState == null) {
                loadFragment(new LostItemsFragment(), "Lost Items");
                bottomNavigationView.setSelectedItemId(R.id.nav_lost);
            }
        } else {
            // User is not logged in, show login fragment and hide bottom navigation
            bottomNavigationView.setVisibility(android.view.View.GONE);

            if (savedInstanceState == null) {
                loadFragment(new LoginFragment(), "Login");
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        String title = "";

        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            selectedFragment = new FoundItemsFragment();
            title = "Found Items";
        } else if (itemId == R.id.nav_lost) {
            selectedFragment = new LostItemsFragment();
            title = "Lost Items";
        } else if (itemId == R.id.nav_post) {
            selectedFragment = new PostItemFragment();
            title = "Post Item";
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
            title = "Profile";
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment, title);
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment, String title) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        if (fragmentTitle != null) {
            fragmentTitle.setText(title);
        }
    }

    public void onLoginSuccess() {
        // Called when user successfully logs in
        bottomNavigationView.setVisibility(android.view.View.VISIBLE);
        loadFragment(new LostItemsFragment(), "Lost Items");
        bottomNavigationView.setSelectedItemId(R.id.nav_lost);
    }

    public void onLogout() {
        // Called when user logs out
        bottomNavigationView.setVisibility(android.view.View.GONE);
        loadFragment(new LoginFragment(), "Login");
    }

    @Override
    public void onBackPressed() {
        // Handle back button press
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
