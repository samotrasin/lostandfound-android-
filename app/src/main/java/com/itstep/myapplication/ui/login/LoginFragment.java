package com.itstep.myapplication.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itstep.myapplication.MainActivity;
import com.itstep.myapplication.R;
import com.itstep.myapplication.Model.User;
import com.itstep.myapplication.manager.AuthManager;
import com.itstep.myapplication.ui.sign_up.SignUpFragment;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    // UI Components
    private TextInputLayout emailLayout;
    private TextInputEditText emailInput;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private TextView signUpLink;

    // Authentication
    private AuthManager authManager;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize AuthManager
        authManager = new AuthManager(requireContext());

        // Initialize UI components
        initializeViews(view);
        setupClickListeners();
    }

    private void initializeViews(View view) {
        emailLayout = view.findViewById(R.id.email_input);
        emailInput = (TextInputEditText) emailLayout.getEditText();
        passwordLayout = view.findViewById(R.id.password_input);
        passwordInput = (TextInputEditText) passwordLayout.getEditText();
        loginButton = view.findViewById(R.id.login_button);
        signUpLink = view.findViewById(R.id.sign_up_link);
    }

    private void setupClickListeners() {
        // Login button click
        loginButton.setOnClickListener(v -> performLogin());

        // Sign up link click
        signUpLink.setOnClickListener(v -> navigateToSignUp());
    }

    private void performLogin() {
        // Clear any previous errors
        emailLayout.setError(null);
        passwordLayout.setError(null);

        // Get input values
        String email = emailInput != null && emailInput.getText() != null ?
                       emailInput.getText().toString().trim() : "";
        String password = passwordInput != null && passwordInput.getText() != null ?
                         passwordInput.getText().toString() : "";

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Perform login
        authManager.login(email, password, new AuthManager.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        setLoadingState(false);
                        Log.d(TAG, "Login successful for user: " + user.getEmail());
                        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show();

                        // Navigate to main app
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).onLoginSuccess();
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        setLoadingState(false);
                        Log.e(TAG, "Login failed: " + errorMessage);
                        Toast.makeText(requireContext(), "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please enter a valid email address");
            isValid = false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        }

        return isValid;
    }

    private void setLoadingState(boolean isLoading) {
        if (loginButton != null) {
            loginButton.setEnabled(!isLoading);
            loginButton.setText(isLoading ? "Logging in..." : getString(R.string.login_button));
        }

        if (emailInput != null) {
            emailInput.setEnabled(!isLoading);
        }

        if (passwordInput != null) {
            passwordInput.setEnabled(!isLoading);
        }

        if (signUpLink != null) {
            signUpLink.setEnabled(!isLoading);
        }
    }

    private void navigateToSignUp() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SignUpFragment())
                .addToBackStack(null)
                .commit();
    }
}
