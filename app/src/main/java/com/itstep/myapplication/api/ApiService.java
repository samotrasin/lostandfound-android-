package com.itstep.myapplication.api;

import com.itstep.myapplication.Model.FoundItem;
import com.itstep.myapplication.Model.LoginRequest;
import com.itstep.myapplication.Model.LoginResponse;
import com.itstep.myapplication.Model.LostItem;
import com.itstep.myapplication.Model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // PUBLIC ENDPOINTS (No Auth Required)

    @POST("api/users")
    Call<User> registerUser(@Body User user);

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("api/lost-items")
    Call<List<LostItem>> getAllLostItems();

    @GET("api/found-items")
    Call<List<FoundItem>> getAllFoundItems();

    @GET("api/users/{id}")
    Call<User> getUserById(@Path("id") Long userId);

    // PROTECTED ENDPOINTS (Basic Auth Required)

    @POST("api/lost-items")
    Call<LostItem> createLostItem(@Body LostItem lostItem);

    @PUT("api/lost-items/{id}")
    Call<LostItem> updateLostItem(@Path("id") Long id, @Body LostItem lostItem);

    @DELETE("api/lost-items/{id}")
    Call<Void> deleteLostItem(@Path("id") Long id);

    @POST("api/found-items")
    Call<FoundItem> createFoundItem(@Body FoundItem foundItem);

    @PUT("api/found-items/{id}")
    Call<FoundItem> updateFoundItem(@Path("id") Long id, @Body FoundItem foundItem);

    @DELETE("api/found-items/{id}")
    Call<Void> deleteFoundItem(@Path("id") Long id);
}
