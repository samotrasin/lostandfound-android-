package com.itstep.myapplication.ui.item_details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.itstep.myapplication.Model.FoundItem;
import com.itstep.myapplication.R;

public class FoundItemDetailsFragment extends Fragment {

    private static final String ARG_ITEM_ID = "item_id";
    private static final String ARG_ITEM_TITLE = "item_title";
    private static final String ARG_ITEM_DESCRIPTION = "item_description";
    private static final String ARG_ITEM_LOCATION = "item_location";
    private static final String ARG_ITEM_CATEGORY = "item_category";
    private static final String ARG_ITEM_FOUND_DATE = "item_found_date";
    private static final String ARG_ITEM_IMAGE_URL = "item_image_url";

    private TextView titleValue, descriptionValue, locationValue, categoryValue, foundDateValue;
    private ShapeableImageView itemImage;
    private MaterialButton backButton;

    public static FoundItemDetailsFragment newInstance(FoundItem item) {
        FoundItemDetailsFragment fragment = new FoundItemDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, item.getId() != null ? item.getId() : 0L);
        args.putString(ARG_ITEM_TITLE, item.getTitle());
        args.putString(ARG_ITEM_DESCRIPTION, item.getDescription());
        args.putString(ARG_ITEM_LOCATION, item.getLocation());
        args.putString(ARG_ITEM_CATEGORY, item.getCategory());
        args.putString(ARG_ITEM_FOUND_DATE, item.getFoundDate());
        args.putString(ARG_ITEM_IMAGE_URL, item.getImageUrl());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found_item_details, container, false);

        // Initialize views
        titleValue = view.findViewById(R.id.title_value);
        descriptionValue = view.findViewById(R.id.description_value);
        locationValue = view.findViewById(R.id.location_value);
        categoryValue = view.findViewById(R.id.category_value);
        foundDateValue = view.findViewById(R.id.found_date_value);
        itemImage = view.findViewById(R.id.item_image);
        backButton = view.findViewById(R.id.back_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get arguments
        Bundle args = getArguments();
        if (args != null) {
            titleValue.setText(args.getString(ARG_ITEM_TITLE, "Unknown"));

            String description = args.getString(ARG_ITEM_DESCRIPTION);
            if (description != null && !description.isEmpty()) {
                descriptionValue.setText(description);
                descriptionValue.setVisibility(View.VISIBLE);
            } else {
                descriptionValue.setText("No description available");
            }

            locationValue.setText(args.getString(ARG_ITEM_LOCATION, "Unknown"));

            String category = args.getString(ARG_ITEM_CATEGORY);
            if (category != null && !category.isEmpty()) {
                categoryValue.setText(category);
            } else {
                categoryValue.setText("No category");
            }

            foundDateValue.setText(args.getString(ARG_ITEM_FOUND_DATE, "Unknown"));

            // TODO: Load image using Glide or Picasso
            String imageUrl = args.getString(ARG_ITEM_IMAGE_URL);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                itemImage.setVisibility(View.VISIBLE);
                // Glide.with(this).load(imageUrl).into(itemImage);
            } else {
                itemImage.setVisibility(View.GONE);
            }
        }

        // Set up back button
        backButton.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().finish();
            }
        });
    }
}
