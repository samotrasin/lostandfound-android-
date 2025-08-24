package com.itstep.myapplication.api;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Use 10.0.2.2 for Android Emulator (maps to localhost on your computer)
    // Use 192.168.18.61 for physical device (your computer's IP address)
    private static final String BASE_URL = "http://192.168.18.61:8080/api/";

    private static Retrofit retrofit = null;
    private static Context applicationContext;

    public static void initialize(Context context) {
        applicationContext = context.getApplicationContext();
    }

    public static ApiService getApiService() {
        if (retrofit == null) {
            // Create logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Create OkHttpClient with or without auth interceptor based on context availability
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor);

            // Only add auth interceptor if context is available
            if (applicationContext != null) {
                AuthInterceptor authInterceptor = new AuthInterceptor(applicationContext);
                okHttpClientBuilder.addInterceptor(authInterceptor);
            }

            OkHttpClient okHttpClient = okHttpClientBuilder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    // Get ApiService without authentication for public endpoints
    public static ApiService getPublicApiService() {
        // Create logging interceptor for debugging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create OkHttpClient without auth interceptor for public endpoints
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit publicRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return publicRetrofit.create(ApiService.class);
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
