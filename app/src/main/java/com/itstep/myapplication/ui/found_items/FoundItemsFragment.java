package com.itstep.myapplication.ui.found_items;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.itstep.myapplication.R;
import com.itstep.myapplication.adapter.FoundItemAdapter;
import com.itstep.myapplication.api.ApiService;
import com.itstep.myapplication.Model.FoundItem;
import com.itstep.myapplication.ui.item_details.ItemDetailsFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

import com.itstep.myapplication.api.RetrofitClient;
import com.itstep.myapplication.manager.AuthManager;

public class FoundItemsFragment extends Fragment implements FoundItemAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private FoundItemAdapter adapter;
    private FoundItemsViewModel mViewModel;

    // UI components for search and sort
    private TextInputEditText searchEditText;
    private MaterialButtonToggleGroup sortToggleGroup;
    private MaterialButton sortByDate, sortByTitle, sortByLocation;
    private MaterialButton sortOrderButton;
    private TextView resultsCountText;

    // Sort state
    private FoundItemAdapter.SortBy currentSortBy = FoundItemAdapter.SortBy.DATE;
    private FoundItemAdapter.SortOrder currentSortOrder = FoundItemAdapter.SortOrder.DESCENDING;

    public static FoundItemsFragment newInstance() {
        return new FoundItemsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_found_items, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FoundItemsViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab_post_found);
        if (fab != null) {
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        initializeViews(view);
        setupRecyclerView();
        setupSearchAndSort();
        loadItems();
    }

    private void initializeViews(View view) {
        // RecyclerView
        recyclerView = view.findViewById(R.id.found_items_recycler_view);

        // Search components
        searchEditText = view.findViewById(R.id.search_edit_text);
        resultsCountText = view.findViewById(R.id.results_count_text);

        // Sort components
        sortToggleGroup = view.findViewById(R.id.sort_toggle_group);
        sortByDate = view.findViewById(R.id.sort_by_date);
        sortByTitle = view.findViewById(R.id.sort_by_title);
        sortByLocation = view.findViewById(R.id.sort_by_location);
        sortOrderButton = view.findViewById(R.id.sort_order_button);

        // Set initial selection
        sortToggleGroup.check(R.id.sort_by_date);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FoundItemAdapter(new ArrayList<>());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchAndSort() {
        // Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = s.toString().trim();
                adapter.setSearchQuery(searchQuery);
                updateResultsCount();
            }
        });

        // Sort toggle group
        sortToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.sort_by_date) {
                    currentSortBy = FoundItemAdapter.SortBy.DATE;
                } else if (checkedId == R.id.sort_by_title) {
                    currentSortBy = FoundItemAdapter.SortBy.TITLE;
                } else if (checkedId == R.id.sort_by_location) {
                    currentSortBy = FoundItemAdapter.SortBy.LOCATION;
                }
                applySorting();
            }
        });

        // Sort order button
        sortOrderButton.setOnClickListener(v -> {
            if (currentSortOrder == FoundItemAdapter.SortOrder.ASCENDING) {
                currentSortOrder = FoundItemAdapter.SortOrder.DESCENDING;
                sortOrderButton.setText("↓");
            } else {
                currentSortOrder = FoundItemAdapter.SortOrder.ASCENDING;
                sortOrderButton.setText("↑");
            }
            applySorting();
        });
    }

    private void applySorting() {
        adapter.setSortOptions(currentSortBy, currentSortOrder);
        updateResultsCount();
    }

    private void updateResultsCount() {
        int totalItems = adapter.getTotalItemCount();
        int filteredItems = adapter.getFilteredItemCount();

        String countText;
        if (totalItems == filteredItems) {
            countText = "Showing " + totalItems + " found items";
        } else {
            countText = "Showing " + filteredItems + " of " + totalItems + " found items";
        }
        resultsCountText.setText(countText);
    }

    private void loadItems() {
        resultsCountText.setText("Loading found items...");

        android.util.Log.d("FoundItemsFragment", "=== LOADING FOUND ITEMS ===");
        android.util.Log.d("FoundItemsFragment", "Base URL: " + com.itstep.myapplication.api.RetrofitClient.getBaseUrl());

        ApiService apiService = RetrofitClient.getPublicApiService();

        // Use the new found-items endpoint
        android.util.Log.d("FoundItemsFragment", "Calling API endpoint: /found-items");

        apiService.getAllFoundItems().enqueue(new Callback<List<FoundItem>>() {
            @Override
            public void onResponse(Call<List<FoundItem>> call, Response<List<FoundItem>> response) {
                android.util.Log.d("FoundItemsFragment", "=== API RESPONSE RECEIVED ===");
                android.util.Log.d("FoundItemsFragment", "Response Code: " + response.code());
                android.util.Log.d("FoundItemsFragment", "Response Message: " + response.message());
                android.util.Log.d("FoundItemsFragment", "Request URL: " + call.request().url());

                if (response.isSuccessful() && response.body() != null) {
                    List<FoundItem> foundItems = response.body();
                    android.util.Log.d("FoundItemsFragment", "SUCCESS: Received " + foundItems.size() + " found items from API");

                    // Log each item for debugging
                    for (int i = 0; i < foundItems.size(); i++) {
                        FoundItem item = foundItems.get(i);
                        android.util.Log.d("FoundItemsFragment", "Found Item " + (i + 1) + ": Title='" + item.getTitle() + "', Location='" + item.getLocation() + "'");
                    }

                    adapter.setItems(foundItems);
                    updateResultsCount();

                    if (foundItems.size() > 0) {
                        android.widget.Toast.makeText(getContext(),
                                "Loaded " + foundItems.size() + " found items successfully!",
                                android.widget.Toast.LENGTH_SHORT).show();
                    } else {
                        resultsCountText.setText("No found items available");
                    }

                } else {
                    android.util.Log.e("FoundItemsFragment", "API FAILED: " + response.code() + " - " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            android.util.Log.e("FoundItemsFragment", "Error Body: " + errorBody);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("FoundItemsFragment", "Could not read error body", e);
                    }
                    resultsCountText.setText("Failed to load found items");
                    android.widget.Toast.makeText(getContext(),
                            "API Error: " + response.code() + " - " + response.message(),
                            android.widget.Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<FoundItem>> call, Throwable t) {
                android.util.Log.e("FoundItemsFragment", "=== NETWORK ERROR ===");
                android.util.Log.e("FoundItemsFragment", "Error Type: " + t.getClass().getSimpleName());
                android.util.Log.e("FoundItemsFragment", "Error Message: " + t.getMessage());
                android.util.Log.e("FoundItemsFragment", "Request URL: " + call.request().url());
                android.util.Log.e("FoundItemsFragment", "Full Error: ", t);

                resultsCountText.setText("Network error occurred");
                String userMessage = "Network Error: ";
                if (t instanceof java.net.ConnectException) {
                    userMessage += "Cannot connect to server. Check if your API is running on localhost:8080";
                } else if (t instanceof java.net.UnknownHostException) {
                    userMessage += "Cannot resolve host. Check network configuration.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    userMessage += "Request timed out. Server may be slow or unreachable.";
                } else {
                    userMessage += t.getMessage();
                }

                android.widget.Toast.makeText(getContext(), userMessage, android.widget.Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onViewDetailsClick(FoundItem item) {
        ItemDetailsFragment detailsFragment = ItemDetailsFragment.newInstance(item);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit();
    }
}