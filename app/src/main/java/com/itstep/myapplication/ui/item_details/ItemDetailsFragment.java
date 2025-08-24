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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itstep.myapplication.Model.LostItem;
import com.itstep.myapplication.Model.FoundItem;
import com.itstep.myapplication.Model.ReturnedItem;
import com.itstep.myapplication.R;

public class ItemDetailsFragment extends Fragment {

    private static final String ARG_ITEM_ID = "item_id";
    private static final String ARG_ITEM_TITLE = "item_title";
    private static final String ARG_ITEM_TYPE = "item_type";
    private static final String ARG_ITEM_DESCRIPTION = "item_description";
    private static final String ARG_ITEM_LOCATION = "item_location";
    private static final String ARG_ITEM_CATEGORY = "item_category";
    private static final String ARG_ITEM_DATE = "item_date";
    private static final String ARG_ITEM_IMAGE_URL = "item_image_url";
    private static final String ARG_OWNER_NAME = "owner_name";
    private static final String ARG_FINDER_NAME = "finder_name";

    private TextInputEditText titleValue, descriptionValue, locationValue, categoryValue, dateValue;
    private TextInputEditText ownerValue, finderValue;
    private TextInputLayout ownerInputLayout, finderInputLayout;
    private TextView itemTypeHeader;
    private ShapeableImageView itemImage;
    private MaterialButton backButton;

    // These are for returned items
    // private TextInputEditText ownerInputLayout, finderInputLayout;

    // Factory methods for different item types
    public static ItemDetailsFragment newInstance(LostItem item) {
        ItemDetailsFragment fragment = new ItemDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, item.getId() != null ? item.getId() : 0L);
        args.putString(ARG_ITEM_TITLE, item.getTitle());
        args.putString(ARG_ITEM_TYPE, "Lost");
        args.putString(ARG_ITEM_DESCRIPTION, item.getDescription());
        args.putString(ARG_ITEM_LOCATION, item.getLocation());
        args.putString(ARG_ITEM_CATEGORY, item.getCategory());
        args.putString(ARG_ITEM_DATE, item.getLostDate());
        args.putString(ARG_ITEM_IMAGE_URL, item.getImageUrl());
        fragment.setArguments(args);
        return fragment;
    }

    public static ItemDetailsFragment newInstance(FoundItem item) {
        ItemDetailsFragment fragment = new ItemDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, item.getId() != null ? item.getId() : 0L);
        args.putString(ARG_ITEM_TITLE, item.getTitle());
        args.putString(ARG_ITEM_TYPE, "Found");
        args.putString(ARG_ITEM_DESCRIPTION, item.getDescription());
        args.putString(ARG_ITEM_LOCATION, item.getLocation());
        args.putString(ARG_ITEM_CATEGORY, item.getCategory());
        args.putString(ARG_ITEM_DATE, item.getFoundDate());
        args.putString(ARG_ITEM_IMAGE_URL, item.getImageUrl());
        fragment.setArguments(args);
        return fragment;
    }

    public static ItemDetailsFragment newInstance(ReturnedItem item) {
        ItemDetailsFragment fragment = new ItemDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, item.getId() != null ? item.getId() : 0L);
        args.putString(ARG_ITEM_TITLE, item.getTitle());
        args.putString(ARG_ITEM_TYPE, "Returned");
        args.putString(ARG_ITEM_DESCRIPTION, item.getDescription());
        args.putString(ARG_ITEM_LOCATION, item.getLocation());
        args.putString(ARG_ITEM_CATEGORY, item.getCategory());
        args.putString(ARG_ITEM_DATE, item.getReturnedDate());
        args.putString(ARG_ITEM_IMAGE_URL, item.getImageUrl());
        args.putString(ARG_OWNER_NAME, item.getOwnerName());
        args.putString(ARG_FINDER_NAME, item.getFinderName());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_details, container, false);

        initViews(view);
        setupData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        titleValue = view.findViewById(R.id.detail_title_value);
        descriptionValue = view.findViewById(R.id.detail_description_value);
        locationValue = view.findViewById(R.id.detail_location_value);
        categoryValue = view.findViewById(R.id.detail_category_value);
        dateValue = view.findViewById(R.id.detail_found_date_value);
        itemImage = view.findViewById(R.id.detail_item_image);
        backButton = view.findViewById(R.id.btn_back);

        // These are for returned items
        ownerValue = view.findViewById(R.id.detail_owner_value);
        finderValue = view.findViewById(R.id.detail_finder_value);
        itemTypeHeader = view.findViewById(R.id.detail_item_type_header);

        // Get the TextInputLayout containers for visibility control
        ownerInputLayout = view.findViewById(R.id.detail_owner_input_layout);
        finderInputLayout = view.findViewById(R.id.detail_finder_input_layout);
    }

    private void setupData() {
        Bundle args = getArguments();
        if (args != null) {
            String itemType = args.getString(ARG_ITEM_TYPE, "");

            titleValue.setText(args.getString(ARG_ITEM_TITLE, ""));

            String description = args.getString(ARG_ITEM_DESCRIPTION, "");
            if (description != null && !description.isEmpty()) {
                descriptionValue.setText(description);
                descriptionValue.setVisibility(View.VISIBLE);
            } else {
                descriptionValue.setText("No description available");
            }

            locationValue.setText(args.getString(ARG_ITEM_LOCATION, ""));

            String category = args.getString(ARG_ITEM_CATEGORY, "");
            if (category != null && !category.isEmpty()) {
                categoryValue.setText(category);
            } else {
                categoryValue.setText("No category");
            }

            // Set appropriate date label based on type
            String date = args.getString(ARG_ITEM_DATE, "");
            String dateLabel = "";
            switch (itemType.toLowerCase()) {
                case "lost":
                    dateLabel = "Lost on: ";
                    break;
                case "found":
                    dateLabel = "Found on: ";
                    break;
                case "returned":
                    dateLabel = "Returned on: ";
                    break;
                default:
                    dateLabel = "Date: ";
                    break;
            }
            dateValue.setText(dateLabel + (date.isEmpty() ? "Unknown" : date));

            // Handle returned item specific fields
            if ("returned".equalsIgnoreCase(itemType)) {
                String ownerName = args.getString(ARG_OWNER_NAME, "");
                String finderName = args.getString(ARG_FINDER_NAME, "");

                if (ownerValue != null && ownerInputLayout != null) {
                    if (!ownerName.isEmpty()) {
                        ownerValue.setText(ownerName);
                        ownerInputLayout.setVisibility(View.VISIBLE);
                    } else {
                        ownerInputLayout.setVisibility(View.GONE);
                    }
                }

                if (finderValue != null && finderInputLayout != null) {
                    if (!finderName.isEmpty()) {
                        finderValue.setText(finderName);
                        finderInputLayout.setVisibility(View.VISIBLE);
                    } else {
                        finderInputLayout.setVisibility(View.GONE);
                    }
                }
            } else {
                // Hide owner/finder fields for lost and found items
                if (ownerInputLayout != null) ownerInputLayout.setVisibility(View.GONE);
                if (finderInputLayout != null) finderInputLayout.setVisibility(View.GONE);
            }

            // Handle image
            String imageUrl = args.getString(ARG_ITEM_IMAGE_URL, "");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                itemImage.setVisibility(View.VISIBLE);
                // TODO: Load image using Glide or Picasso
                // Glide.with(this).load(imageUrl).into(itemImage);
            } else {
                itemImage.setVisibility(View.GONE);
            }

            // Set the item type header with proper formatting
            switch (itemType.toLowerCase()) {
                case "lost":
                    itemTypeHeader.setText("Lost Item");
                    break;
                case "found":
                    itemTypeHeader.setText("Found Item");
                    break;
                case "returned":
                    itemTypeHeader.setText("Returned Item");
                    break;
                default:
                    itemTypeHeader.setText("Item Details");
                    break;
            }
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}
