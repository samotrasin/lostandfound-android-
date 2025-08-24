package com.itstep.myapplication.api;

import android.content.Context;
import com.itstep.myapplication.manager.UserSession;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    private UserSession userSession;

    public AuthInterceptor(Context context) {
        this.userSession = new UserSession(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Skip authentication for login and register endpoints
        String url = originalRequest.url().toString();
        if (url.contains("/auth/login") || url.contains("/users") || url.contains("/register")) {
            return chain.proceed(originalRequest);
        }

        // Add authorization header if user is logged in
        if (userSession.isLoggedIn()) {
            String authHeader = userSession.getBasicAuthHeader();
            if (!authHeader.isEmpty()) {
                Request authenticatedRequest = originalRequest.newBuilder()
                        .header("Authorization", authHeader)
                        .build();
                return chain.proceed(authenticatedRequest);
            }
        }

        // Proceed with original request if no auth needed or not logged in
        return chain.proceed(originalRequest);
    }
}
