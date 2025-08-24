package com.itstep.myapplication.api;

import android.content.Context;
import android.util.Log;
import com.itstep.myapplication.Model.FoundItem;
import com.itstep.myapplication.Model.LostItem;
import com.itstep.myapplication.Model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRepository {
    private static final String TAG = "ApiRepository";
    private ApiService apiService;
    private Context context;

    public ApiRepository(Context context) {
        this.context = context;
        this.apiService = ApiClient.getApiService(context);
    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
    }

    // Lost Items operations
    public void getAllLostItems(DataCallback<List<LostItem>> callback) {
        Call<List<LostItem>> call = apiService.getAllLostItems();
        call.enqueue(new Callback<List<LostItem>>() {
            @Override
            public void onResponse(Call<List<LostItem>> call, Response<List<LostItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load lost items");
                }
            }

            @Override
            public void onFailure(Call<List<LostItem>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to get lost items", t);
            }
        });
    }

    public void createLostItem(LostItem lostItem, DataCallback<LostItem> callback) {
        Call<LostItem> call = apiService.createLostItem(lostItem);
        call.enqueue(new Callback<LostItem>() {
            @Override
            public void onResponse(Call<LostItem> call, Response<LostItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Failed to create lost item";
                    if (response.code() == 401) {
                        errorMsg = "Please login to post items";
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<LostItem> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to create lost item", t);
            }
        });
    }

    public void updateLostItem(Long id, LostItem lostItem, DataCallback<LostItem> callback) {
        Call<LostItem> call = apiService.updateLostItem(id, lostItem);
        call.enqueue(new Callback<LostItem>() {
            @Override
            public void onResponse(Call<LostItem> call, Response<LostItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update lost item");
                }
            }

            @Override
            public void onFailure(Call<LostItem> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to update lost item", t);
            }
        });
    }

    public void deleteLostItem(Long id, DataCallback<Void> callback) {
        Call<Void> call = apiService.deleteLostItem(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to delete lost item");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to delete lost item", t);
            }
        });
    }

    // Found Items operations
    public void getAllFoundItems(DataCallback<List<FoundItem>> callback) {
        Call<List<FoundItem>> call = apiService.getAllFoundItems();
        call.enqueue(new Callback<List<FoundItem>>() {
            @Override
            public void onResponse(Call<List<FoundItem>> call, Response<List<FoundItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load found items");
                }
            }

            @Override
            public void onFailure(Call<List<FoundItem>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to get found items", t);
            }
        });
    }

    public void createFoundItem(FoundItem foundItem, DataCallback<FoundItem> callback) {
        Call<FoundItem> call = apiService.createFoundItem(foundItem);
        call.enqueue(new Callback<FoundItem>() {
            @Override
            public void onResponse(Call<FoundItem> call, Response<FoundItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Failed to create found item";
                    if (response.code() == 401) {
                        errorMsg = "Please login to post items";
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<FoundItem> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to create found item", t);
            }
        });
    }

    public void updateFoundItem(Long id, FoundItem foundItem, DataCallback<FoundItem> callback) {
        Call<FoundItem> call = apiService.updateFoundItem(id, foundItem);
        call.enqueue(new Callback<FoundItem>() {
            @Override
            public void onResponse(Call<FoundItem> call, Response<FoundItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update found item");
                }
            }

            @Override
            public void onFailure(Call<FoundItem> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to update found item", t);
            }
        });
    }

    public void deleteFoundItem(Long id, DataCallback<Void> callback) {
        Call<Void> call = apiService.deleteFoundItem(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to delete found item");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to delete found item", t);
            }
        });
    }

    // User operations
    public void getUserById(Long userId, DataCallback<User> callback) {
        Call<User> call = apiService.getUserById(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load user profile");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to get user", t);
            }
        });
    }
}
