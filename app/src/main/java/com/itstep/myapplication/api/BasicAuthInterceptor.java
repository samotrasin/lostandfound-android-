package com.itstep.myapplication.api;

import android.content.Context;
import com.itstep.myapplication.manager.UserSession;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class BasicAuthInterceptor implements Interceptor {
    private UserSession userSession;

    public BasicAuthInterceptor(Context context) {
        this.userSession = new UserSession(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();

        // Add Basic Auth header if user is logged in
        if (userSession.isLoggedIn()) {
            String authHeader = userSession.getBasicAuthHeader();
            if (!authHeader.isEmpty()) {
                builder.header("Authorization", authHeader);
            }
        }

        Request request = builder.build();
        return chain.proceed(request);
    }
}
