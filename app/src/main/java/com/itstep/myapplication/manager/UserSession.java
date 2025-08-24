package com.itstep.myapplication.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import com.itstep.myapplication.Model.User;

public class UserSession {
    private static final String PREF_NAME = "lost_found_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_PASSWORD = "user_password";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public UserSession(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(User user, String password) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, user.getId() != null ? user.getId() : 0);
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONE, user.getPhoneNumber());
        editor.putString(KEY_USER_PASSWORD, password); // Store for Basic Auth
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }

        User user = new User();
        user.setId(pref.getLong(KEY_USER_ID, 0));
        user.setName(pref.getString(KEY_USER_NAME, ""));
        user.setEmail(pref.getString(KEY_USER_EMAIL, ""));
        user.setPhoneNumber(pref.getString(KEY_USER_PHONE, ""));
        return user;
    }

    public String getStoredEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public String getStoredPassword() {
        return pref.getString(KEY_USER_PASSWORD, "");
    }

    public String getBasicAuthHeader() {
        if (!isLoggedIn()) {
            return "";
        }

        String email = getStoredEmail();
        String password = getStoredPassword();
        String credentials = email + ":" + password;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
