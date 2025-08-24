package com.itstep.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.itstep.myapplication.R;
import com.itstep.myapplication.Model.ReturnedItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReturnedItemAdapter extends RecyclerView.Adapter<ReturnedItemAdapter.ViewHolder> {

    private List<ReturnedItem> originalItems;
    private List<ReturnedItem> filteredItems;
    private OnItemClickListener onItemClickListener;
    private String searchQuery = "";
    private SortBy sortBy = SortBy.DATE;
    private SortOrder sortOrder = SortOrder.DESCENDING;

    public enum SortBy {
        DATE, TITLE, LOCATION
    }

    public enum SortOrder {
        ASCENDING, DESCENDING
    }

    public interface OnItemClickListener {
        void onViewDetailsClick(ReturnedItem item);
    }

    public ReturnedItemAdapter(List<ReturnedItem> items) {
        this.originalItems = new ArrayList<>(items);
        this.filteredItems = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_returned_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReturnedItem item = filteredItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public void setItems(List<ReturnedItem> items) {
        this.originalItems = new ArrayList<>(items);
        applyFiltersAndSort();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query.toLowerCase().trim();
        applyFiltersAndSort();
    }

    public void setSortOptions(SortBy sortBy, SortOrder sortOrder) {
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
        applyFiltersAndSort();
    }

    public int getTotalItemCount() {
        return originalItems.size();
    }

    public int getFilteredItemCount() {
        return filteredItems.size();
    }

    private void applyFiltersAndSort() {
        filteredItems.clear();
        for (ReturnedItem item : originalItems) {
            if (searchQuery.isEmpty() || matchesSearchQuery(item)) {
                filteredItems.add(item);
            }
        }

        Collections.sort(filteredItems, getComparator());
        notifyDataSetChanged();
    }

    private boolean matchesSearchQuery(ReturnedItem item) {
        return (item.getTitle() != null && item.getTitle().toLowerCase().contains(searchQuery)) ||
               (item.getDescription() != null && item.getDescription().toLowerCase().contains(searchQuery)) ||
               (item.getLocation() != null && item.getLocation().toLowerCase().contains(searchQuery)) ||
               (item.getCategory() != null && item.getCategory().toLowerCase().contains(searchQuery)) ||
               (item.getOwnerName() != null && item.getOwnerName().toLowerCase().contains(searchQuery)) ||
               (item.getFinderName() != null && item.getFinderName().toLowerCase().contains(searchQuery));
    }

    private Comparator<ReturnedItem> getComparator() {
        Comparator<ReturnedItem> comparator;

        switch (sortBy) {
            case TITLE:
                comparator = (a, b) -> {
                    String titleA = a.getTitle() != null ? a.getTitle() : "";
                    String titleB = b.getTitle() != null ? b.getTitle() : "";
                    return titleA.compareToIgnoreCase(titleB);
                };
                break;
            case LOCATION:
                comparator = (a, b) -> {
                    String locationA = a.getLocation() != null ? a.getLocation() : "";
                    String locationB = b.getLocation() != null ? b.getLocation() : "";
                    return locationA.compareToIgnoreCase(locationB);
                };
                break;
            case DATE:
            default:
                comparator = (a, b) -> {
                    String dateA = a.getReturnedDate() != null ? a.getReturnedDate() : "";
                    String dateB = b.getReturnedDate() != null ? b.getReturnedDate() : "";
                    return dateA.compareTo(dateB);
                };
                break;
        }

        if (sortOrder == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView locationTextView;
        private TextView dateTextView;
        private TextView categoryTextView;
        private TextView ownerTextView;
        private TextView finderTextView;
        private ImageView itemImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.item_title);
            descriptionTextView = itemView.findViewById(R.id.item_description);
            locationTextView = itemView.findViewById(R.id.item_location);
            dateTextView = itemView.findViewById(R.id.item_date);
            categoryTextView = itemView.findViewById(R.id.item_category);
            ownerTextView = itemView.findViewById(R.id.owner_name);
            finderTextView = itemView.findViewById(R.id.finder_name);
            itemImageView = itemView.findViewById(R.id.item_image);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onViewDetailsClick(filteredItems.get(position));
                    }
                }
            });
        }

        public void bind(ReturnedItem item) {
            titleTextView.setText(item.getTitle());

            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                descriptionTextView.setText(item.getDescription());
                descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }

            if (item.getLocation() != null && !item.getLocation().isEmpty()) {
                locationTextView.setText(item.getLocation());
                locationTextView.setVisibility(View.VISIBLE);
            } else {
                locationTextView.setVisibility(View.GONE);
            }

            if (item.getReturnedDate() != null && !item.getReturnedDate().isEmpty()) {
                dateTextView.setText("Returned: " + item.getReturnedDate());
                dateTextView.setVisibility(View.VISIBLE);
            } else {
                dateTextView.setVisibility(View.GONE);
            }

            if (item.getCategory() != null && !item.getCategory().isEmpty()) {
                categoryTextView.setText(item.getCategory());
                categoryTextView.setVisibility(View.VISIBLE);
            } else {
                categoryTextView.setVisibility(View.GONE);
            }

            if (item.getOwnerName() != null && !item.getOwnerName().isEmpty()) {
                ownerTextView.setText(item.getOwnerName());
                ownerTextView.setVisibility(View.VISIBLE);
            } else {
                ownerTextView.setVisibility(View.GONE);
            }

            if (item.getFinderName() != null && !item.getFinderName().isEmpty()) {
                finderTextView.setText(item.getFinderName());
                finderTextView.setVisibility(View.VISIBLE);
            } else {
                finderTextView.setVisibility(View.GONE);
            }

            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                itemImageView.setVisibility(View.VISIBLE);
                // TODO: Load image using image loading library (Glide, Picasso, etc.)
            } else {
                itemImageView.setVisibility(View.GONE);
            }
        }
    }
}
