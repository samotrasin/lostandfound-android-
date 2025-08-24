package com.itstep.myapplication.ui.sign_up;

import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itstep.myapplication.MainActivity;
import com.itstep.myapplication.Model.User;
import com.itstep.myapplication.R;
import com.itstep.myapplication.manager.AuthManager;
import com.itstep.myapplication.ui.login.LoginFragment;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";

    // UI Components
    private TextInputLayout usernameLayout;
    private TextInputEditText usernameInput;
    private TextInputLayout emailLayout;
    private TextInputEditText emailInput;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordInput;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText confirmPasswordInput;
    private TextInputLayout phoneLayout;
    private TextInputEditText phoneInput;
    private MaterialButton signUpButton;
    private TextView loginLink;

    // Authentication
    private AuthManager authManager;
    private SignUpViewModel mViewModel;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize AuthManager
        authManager = new AuthManager(requireContext());

        // Check if user is already logged in
        if (authManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        initializeViews(view);
        setupClickListeners();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
    }

    private void initializeViews(View view) {
        usernameLayout = view.findViewById(R.id.username_input);
        usernameInput = (TextInputEditText) usernameLayout.getEditText();

        emailLayout = view.findViewById(R.id.email_input);
        emailInput = (TextInputEditText) emailLayout.getEditText();

        passwordLayout = view.findViewById(R.id.password_input);
        passwordInput = (TextInputEditText) passwordLayout.getEditText();

        confirmPasswordLayout = view.findViewById(R.id.confirm_password_input);
        confirmPasswordInput = (TextInputEditText) confirmPasswordLayout.getEditText();

        // Phone input might not exist in layout, handle gracefully
        phoneLayout = view.findViewById(R.id.phone_input);
        if (phoneLayout != null) {
            phoneInput = (TextInputEditText) phoneLayout.getEditText();
        }

        signUpButton = view.findViewById(R.id.sign_up_button);
        loginLink = view.findViewById(R.id.login_link);
    }

    private void setupClickListeners() {
        signUpButton.setOnClickListener(v -> handleSignUp());

        loginLink.setOnClickListener(v -> navigateToLogin());
    }

    private void handleSignUp() {
        // Clear previous errors
        clearErrors();

        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";
        String confirmPassword = confirmPasswordInput.getText() != null ? confirmPasswordInput.getText().toString() : "";
        String phone = phoneInput != null && phoneInput.getText() != null ? phoneInput.getText().toString().trim() : "";

        // Validate input
        if (!validateInput(username, email, password, confirmPassword, phone)) {
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Perform registration
        authManager.register(username, email, phone, password, new AuthManager.RegisterCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        setLoadingState(false);
                        Toast.makeText(getContext(), "Account created successfully! Please login.",
                            Toast.LENGTH_LONG).show();

                        // Navigate to login after successful registration
                        navigateToLogin();
                    });
                }
                Log.d(TAG, "Registration successful for user: " + user.getEmail());
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        setLoadingState(false);
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
                Log.e(TAG, "Registration error: " + errorMessage);
            }
        });
    }

    private boolean validateInput(String username, String email, String password, String confirmPassword, String phone) {
        boolean isValid = true;

        if (TextUtils.isEmpty(username)) {
            usernameLayout.setError("Name is required");
            isValid = false;
        } else if (username.length() < 2) {
            usernameLayout.setError("Name must be at least 2 characters");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            isValid = false;
        }

        // Phone validation is optional
        if (!TextUtils.isEmpty(phone) && phone.length() < 10) {
            if (phoneLayout != null) {
                phoneLayout.setError("Please enter a valid phone number");
                isValid = false;
            }
        }

        return isValid;
    }

    private void clearErrors() {
        usernameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
        if (phoneLayout != null) {
            phoneLayout.setError(null);
        }
    }

    private void setLoadingState(boolean isLoading) {
        signUpButton.setEnabled(!isLoading);
        signUpButton.setText(isLoading ? "Creating Account..." : "Sign Up");

        if (usernameInput != null) usernameInput.setEnabled(!isLoading);
        if (emailInput != null) emailInput.setEnabled(!isLoading);
        if (passwordInput != null) passwordInput.setEnabled(!isLoading);
        if (confirmPasswordInput != null) confirmPasswordInput.setEnabled(!isLoading);
        if (phoneInput != null) phoneInput.setEnabled(!isLoading);
        if (loginLink != null) loginLink.setEnabled(!isLoading);
    }

    private void navigateToMain() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void navigateToLogin() {
        // Use fragment replacement instead of navigation component
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }

}