package com.itstep.myapplication.manager;

import android.content.Context;
import android.util.Log;
import com.itstep.myapplication.Model.LoginRequest;
import com.itstep.myapplication.Model.LoginResponse;
import com.itstep.myapplication.Model.User;
import com.itstep.myapplication.api.ApiService;
import com.itstep.myapplication.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private Context context;
    private UserSession userSession;
    private ApiService apiService;

    public AuthManager(Context context) {
        this.context = context;
        this.userSession = new UserSession(context);
        this.apiService = RetrofitClient.getPublicApiService(); // Use public API for login (no auth required)
    }

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public interface RegisterCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public void login(String email, String password, AuthCallback callback) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        Call<LoginResponse> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess() && loginResponse.getUser() != null) {
                        // Save user session
                        userSession.createLoginSession(loginResponse.getUser(), password);
                        callback.onSuccess(loginResponse.getUser());
                        Log.d(TAG, "Login successful for user: " + loginResponse.getUser().getEmail());
                    } else {
                        callback.onError(loginResponse.getMessage() != null ?
                            loginResponse.getMessage() : "Login failed");
                    }
                } else {
                    String errorMsg = "Login failed";
                    if (response.code() == 401) {
                        errorMsg = "Invalid email or password";
                    } else if (response.code() == 500) {
                        errorMsg = "Server error. Please try again later";
                    }
                    callback.onError(errorMsg);
                    Log.e(TAG, "Login failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError("Network error. Check your internet connection");
                Log.e(TAG, "Login network error: " + t.getMessage());
            }
        });
    }

    public void register(String name, String email, String phoneNumber, String password, RegisterCallback callback) {
        User newUser = new User(name, email, phoneNumber, password);

        Call<User> call = apiService.registerUser(newUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User registeredUser = response.body();
                    callback.onSuccess(registeredUser);
                    Log.d(TAG, "Registration successful for user: " + registeredUser.getEmail());
                } else {
                    String errorMsg = "Registration failed";
                    if (response.code() == 400) {
                        errorMsg = "Invalid user data. Please check your information";
                    } else if (response.code() == 409) {
                        errorMsg = "User with this email already exists";
                    } else if (response.code() == 500) {
                        errorMsg = "Server error. Please try again later";
                    }
                    callback.onError(errorMsg);
                    Log.e(TAG, "Registration failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Network error. Check your internet connection");
                Log.e(TAG, "Registration network error: " + t.getMessage());
            }
        });
    }

    public void logout() {
        userSession.logout();
        Log.d(TAG, "User logged out successfully");
    }

    public boolean isLoggedIn() {
        return userSession.isLoggedIn();
    }

    public User getCurrentUser() {
        return userSession.getCurrentUser();
    }
}
