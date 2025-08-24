package com.itstep.myapplication.ui.lost_items;

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
import android.widget.Toast;
import android.util.Log;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.itstep.myapplication.R;
import com.itstep.myapplication.adapter.LostItemAdapter;
import com.itstep.myapplication.api.ApiRepository;
import com.itstep.myapplication.Model.LostItem;
import com.itstep.myapplication.ui.item_details.ItemDetailsFragment;
import java.util.ArrayList;
import java.util.List;

public class LostItemsFragment extends Fragment implements LostItemAdapter.OnItemClickListener {

    private static final String TAG = "LostItemsFragment";

    private RecyclerView recyclerView;
    private LostItemAdapter adapter;
    private LostItemsViewModel mViewModel;
    private ApiRepository apiRepository;

    // UI components for search and sort
    private TextInputEditText searchEditText;
    private MaterialButtonToggleGroup sortToggleGroup;
    private MaterialButton sortByDate, sortByTitle, sortByLocation;
    private MaterialButton sortOrderButton;
    private TextView resultsCountText;
    private View loadingIndicator;

    // Sort state
    private LostItemAdapter.SortBy currentSortBy = LostItemAdapter.SortBy.DATE;
    private LostItemAdapter.SortOrder currentSortOrder = LostItemAdapter.SortOrder.DESCENDING;

    // Data
    private List<LostItem> allLostItems = new ArrayList<>();
    private List<LostItem> filteredLostItems = new ArrayList<>();

    public static LostItemsFragment newInstance() {
        return new LostItemsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lost_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize API repository
        apiRepository = new ApiRepository(requireContext());

        initializeViews(view);
        setupRecyclerView();
        setupSearchAndSort();
        loadLostItems();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LostItemsViewModel.class);
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_lost_items);
        searchEditText = view.findViewById(R.id.search_edit_text);
        sortToggleGroup = view.findViewById(R.id.sort_toggle_group);
        sortByDate = view.findViewById(R.id.sort_by_date);
        sortByTitle = view.findViewById(R.id.sort_by_title);
        sortByLocation = view.findViewById(R.id.sort_by_location);
        sortOrderButton = view.findViewById(R.id.sort_order_button);
        resultsCountText = view.findViewById(R.id.results_count_text);
        loadingIndicator = view.findViewById(R.id.loading_indicator);
    }

    private void setupRecyclerView() {
        adapter = new LostItemAdapter(filteredLostItems);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchAndSort() {
        // Search functionality
        if (searchEditText != null) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterItems(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Sort functionality
        if (sortToggleGroup != null) {
            sortToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    if (checkedId == R.id.sort_by_date) {
                        currentSortBy = LostItemAdapter.SortBy.DATE;
                    } else if (checkedId == R.id.sort_by_title) {
                        currentSortBy = LostItemAdapter.SortBy.TITLE;
                    } else if (checkedId == R.id.sort_by_location) {
                        currentSortBy = LostItemAdapter.SortBy.LOCATION;
                    }
                    applySortAndFilter();
                }
            });
        }

        if (sortOrderButton != null) {
            sortOrderButton.setOnClickListener(v -> {
                currentSortOrder = (currentSortOrder == LostItemAdapter.SortOrder.ASCENDING)
                    ? LostItemAdapter.SortOrder.DESCENDING
                    : LostItemAdapter.SortOrder.ASCENDING;

                sortOrderButton.setText(currentSortOrder == LostItemAdapter.SortOrder.ASCENDING ? "↑" : "↓");
                applySortAndFilter();
            });
        }
    }

    private void loadLostItems() {
        setLoadingState(true);

        apiRepository.getAllLostItems(new ApiRepository.DataCallback<List<LostItem>>() {
            @Override
            public void onSuccess(List<LostItem> lostItems) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        setLoadingState(false);
                        allLostItems.clear();
                        allLostItems.addAll(lostItems);
                        applySortAndFilter();
                        updateResultsCount();
                        Log.d(TAG, "Loaded " + lostItems.size() + " lost items");
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        setLoadingState(false);
                        Toast.makeText(getContext(), "Failed to load lost items: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error loading lost items: " + errorMessage);
                    });
                }
            }
        });
    }

    private void filterItems(String query) {
        filteredLostItems.clear();

        if (query.isEmpty()) {
            filteredLostItems.addAll(allLostItems);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (LostItem item : allLostItems) {
                if ((item.getTitle() != null && item.getTitle().toLowerCase().contains(lowerCaseQuery)) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerCaseQuery)) ||
                    (item.getLocation() != null && item.getLocation().toLowerCase().contains(lowerCaseQuery)) ||
                    (item.getCategory() != null && item.getCategory().toLowerCase().contains(lowerCaseQuery))) {
                    filteredLostItems.add(item);
                }
            }
        }

        applySortAndFilter();
        updateResultsCount();
    }

    private void applySortAndFilter() {
        if (adapter != null) {
            adapter.setSortOptions(currentSortBy, currentSortOrder);
        }
    }

    private void updateResultsCount() {
        if (resultsCountText != null) {
            resultsCountText.setText(getString(R.string.results_count, filteredLostItems.size()));
        }
    }

    private void setLoadingState(boolean isLoading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onViewDetailsClick(LostItem lostItem) {
        // Navigate to item details
        ItemDetailsFragment detailsFragment = ItemDetailsFragment.newInstance(lostItem);
        getParentFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, detailsFragment)
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment resumes
        loadLostItems();
    }
}
