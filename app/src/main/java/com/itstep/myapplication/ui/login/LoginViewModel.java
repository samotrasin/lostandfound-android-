package com.itstep.myapplication.ui.login;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.itstep.myapplication.manager.AuthManager;

public class LoginViewModel extends AndroidViewModel {
    private MutableLiveData<String> loginResult = new MutableLiveData<>();
    private AuthManager authManager;

    public LoginViewModel(Application application) {
        super(application);
        authManager = new AuthManager(application);
    }

    public LiveData<String> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        authManager.login(email, password, new AuthManager.AuthCallback() {
            @Override
            public void onSuccess(com.itstep.myapplication.Model.User user) {
                loginResult.setValue("Login successful");
            }

            @Override
            public void onError(String errorMessage) {
                loginResult.setValue("Login failed: " + errorMessage);
            }
        });
    }

    public boolean isLoggedIn() {
        return authManager.isLoggedIn();
    }
}