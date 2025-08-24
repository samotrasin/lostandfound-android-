package com.itstep.myapplication.api;

import android.content.Context;
import com.itstep.myapplication.Model.LoginRequest;
import com.itstep.myapplication.Model.LoginResponse;
import com.itstep.myapplication.Model.RegisterRequest;
import com.itstep.myapplication.Model.User;
import com.itstep.myapplication.manager.AuthManager;
import com.itstep.myapplication.manager.UserSession;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthService {
    private ApiService apiService;
    private AuthManager authManager;
    private UserSession userSession;

    public AuthService(Context context) {
        this.apiService = ApiClient.getApiService(context);
        this.authManager = new AuthManager(context);
        this.userSession = new UserSession(context);
    }

    public interface AuthCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    public void login(String email, String password, AuthCallback<User> callback) {
        authManager.login(email, password, new AuthManager.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                callback.onSuccess(user);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void register(String name, String email, String phoneNumber, String password, AuthCallback<User> callback) {
        authManager.register(name, email, phoneNumber, password, new AuthManager.RegisterCallback() {
            @Override
            public void onSuccess(User user) {
                callback.onSuccess(user);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public boolean isLoggedIn() {
        return authManager.isLoggedIn();
    }

    public User getCurrentUser() {
        return authManager.getCurrentUser();
    }

    public void logout() {
        authManager.logout();
    }

    // Direct API call methods (if needed for custom authentication flows)
    public void loginDirect(LoginRequest loginRequest, AuthCallback<LoginResponse> callback) {
        Call<LoginResponse> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void registerDirect(User user, AuthCallback<User> callback) {
        Call<User> call = apiService.registerUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Registration failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
