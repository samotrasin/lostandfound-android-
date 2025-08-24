package com.itstep.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.itstep.myapplication.R;
import com.itstep.myapplication.Model.FoundItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FoundItemAdapter extends RecyclerView.Adapter<FoundItemAdapter.ViewHolder> {

    private List<FoundItem> originalItems;
    private List<FoundItem> filteredItems;
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
        void onViewDetailsClick(FoundItem item);
    }

    public FoundItemAdapter(List<FoundItem> items) {
        this.originalItems = new ArrayList<>(items);
        this.filteredItems = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_found_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoundItem item = filteredItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public void setItems(List<FoundItem> items) {
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
        // Apply search filter
        filteredItems.clear();
        for (FoundItem item : originalItems) {
            if (searchQuery.isEmpty() || matchesSearchQuery(item)) {
                filteredItems.add(item);
            }
        }

        // Apply sorting
        Collections.sort(filteredItems, getComparator());

        notifyDataSetChanged();
    }

    private boolean matchesSearchQuery(FoundItem item) {
        return (item.getTitle() != null && item.getTitle().toLowerCase().contains(searchQuery)) ||
               (item.getDescription() != null && item.getDescription().toLowerCase().contains(searchQuery)) ||
               (item.getLocation() != null && item.getLocation().toLowerCase().contains(searchQuery)) ||
               (item.getCategory() != null && item.getCategory().toLowerCase().contains(searchQuery));
    }

    private Comparator<FoundItem> getComparator() {
        Comparator<FoundItem> comparator;

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
                    String dateA = a.getFoundDate() != null ? a.getFoundDate() : "";
                    String dateB = b.getFoundDate() != null ? b.getFoundDate() : "";
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
        private ImageView itemImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.item_title);
            descriptionTextView = itemView.findViewById(R.id.item_description);
            locationTextView = itemView.findViewById(R.id.item_location);
            dateTextView = itemView.findViewById(R.id.item_date);
            categoryTextView = itemView.findViewById(R.id.item_category);
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

        public void bind(FoundItem item) {
            titleTextView.setText(item.getTitle());

            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                descriptionTextView.setText(item.getDescription());
                descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }

            locationTextView.setText(item.getLocation());
            dateTextView.setText("Found: " + (item.getFoundDate() != null ? item.getFoundDate() : "Unknown"));

            if (item.getCategory() != null && !item.getCategory().isEmpty()) {
                categoryTextView.setText(item.getCategory());
                categoryTextView.setVisibility(View.VISIBLE);
            } else {
                categoryTextView.setVisibility(View.GONE);
            }

            // TODO: Load image using image loading library like Glide or Picasso
            // For now, hide the image view or set a placeholder
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                // Glide.with(itemView.getContext()).load(item.getImageUrl()).into(itemImageView);
                itemImageView.setVisibility(View.VISIBLE);
            } else {
                itemImageView.setVisibility(View.GONE);
            }
        }
    }
}
