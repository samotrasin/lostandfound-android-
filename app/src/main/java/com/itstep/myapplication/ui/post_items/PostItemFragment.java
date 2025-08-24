package com.itstep.myapplication.ui.post_items;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itstep.myapplication.R;
import com.itstep.myapplication.Model.LostItem;
import com.itstep.myapplication.Model.FoundItem;
import com.itstep.myapplication.api.ApiService;
import com.itstep.myapplication.api.RetrofitClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostItemFragment extends Fragment {

    private static final String TAG = "PostItemFragment";
    private Calendar selectedDate = Calendar.getInstance();

    public PostItemFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            android.util.Log.d(TAG, "PostItemFragment onCreateView called");
            View view = inflater.inflate(R.layout.fragment_post_item, container, false);
            android.util.Log.d(TAG, "PostItemFragment view inflated successfully");
            return view;
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error in onCreateView", e);
            android.widget.Toast.makeText(getContext(), "Error loading post form: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            android.util.Log.d(TAG, "PostItemFragment onViewCreated called");
            super.onViewCreated(view, savedInstanceState);

            TextInputLayout titleLayout = view.findViewById(R.id.item_title_input);
            TextInputEditText titleInput = (TextInputEditText) titleLayout.getEditText();

            TextInputLayout descriptionLayout = view.findViewById(R.id.item_description_input);
            AppCompatEditText descriptionInput = (AppCompatEditText) descriptionLayout.getEditText();

            TextInputLayout categoryLayout = view.findViewById(R.id.item_category_input);
            TextInputEditText categoryInput = (TextInputEditText) categoryLayout.getEditText();

            TextInputLayout foundDateLayout = view.findViewById(R.id.found_date_input);
            TextInputEditText foundDateInput = (TextInputEditText) foundDateLayout.getEditText();

            // Setup date picker
            if (foundDateInput != null) {
                foundDateInput.setFocusable(false);
                foundDateInput.setClickable(true);
                foundDateInput.setOnClickListener(v -> showDatePicker(foundDateInput));
            }

            MaterialAutoCompleteTextView locationInput = view.findViewById(R.id.item_location_input_field);
            String[] locations = getResources().getStringArray(R.array.cambodia_provinces);
            ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, locations);
            locationInput.setAdapter(locationAdapter);
            locationInput.setOnClickListener(v -> locationInput.showDropDown());

            MaterialAutoCompleteTextView lostOrFoundInput = view.findViewById(R.id.item_lost_or_found_input_field);
            String[] typeOptions = getResources().getStringArray(R.array.lost_or_found);
            ArrayAdapter<String> lostFoundAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, typeOptions);
            lostOrFoundInput.setAdapter(lostFoundAdapter);
            lostOrFoundInput.setOnClickListener(v -> lostOrFoundInput.showDropDown());
            lostOrFoundInput.setKeyListener(null);

            MaterialButton postButton = view.findViewById(R.id.post_item_button);

            postButton.setOnClickListener(v -> {
                Log.d(TAG, "Post button clicked");

                String title = titleInput != null && titleInput.getText() != null ? titleInput.getText().toString().trim() : "";
                String description = descriptionInput != null && descriptionInput.getText() != null ? descriptionInput.getText().toString().trim() : "";
                String category = categoryInput != null && categoryInput.getText() != null ? categoryInput.getText().toString().trim() : "";
                String location = locationInput.getText() != null ? locationInput.getText().toString().trim() : "";
                String type = lostOrFoundInput.getText() != null ? lostOrFoundInput.getText().toString().trim() : "";
                String date = foundDateInput != null && foundDateInput.getText() != null ? foundDateInput.getText().toString().trim() : "";

                Log.d(TAG, "Form data - Title: " + title + ", Type: " + type + ", Location: " + location + ", Date: " + date);

                // Validate required fields
                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (type.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select Lost or Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (location.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select a location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (date.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show loading state
                postButton.setEnabled(false);
                postButton.setText("Posting...");

                // Use authenticated API service for POST requests (required by SecurityConfig)
                ApiService apiService = RetrofitClient.getApiService();

                // Create appropriate object based on type
                if ("Lost".equalsIgnoreCase(type)) {
                    LostItem newLostItem = new LostItem();
                    newLostItem.setTitle(title);
                    newLostItem.setDescription(description.isEmpty() ? null : description);
                    newLostItem.setLocation(location);
                    newLostItem.setCategory(category.isEmpty() ? null : category);
                    newLostItem.setLostDate(date);

                    Log.d(TAG, "Sending lost item to API: " + newLostItem.getTitle());

                    Call<LostItem> call = apiService.createLostItem(newLostItem);
                    call.enqueue(new Callback<LostItem>() {
                        @Override
                        public void onResponse(Call<LostItem> call, Response<LostItem> response) {
                            handleResponse(response.isSuccessful(), response.code(),
                                    response.isSuccessful() ? response.body().getTitle() : null,
                                    response, postButton);
                        }

                        @Override
                        public void onFailure(Call<LostItem> call, Throwable t) {
                            handleFailure(t, postButton);
                        }
                    });

                } else if ("Found".equalsIgnoreCase(type)) {
                    FoundItem newFoundItem = new FoundItem();
                    newFoundItem.setTitle(title);
                    newFoundItem.setDescription(description.isEmpty() ? null : description);
                    newFoundItem.setLocation(location);
                    newFoundItem.setCategory(category.isEmpty() ? null : category);
                    newFoundItem.setFoundDate(date);

                    Log.d(TAG, "Sending found item to API: " + newFoundItem.getTitle());

                    Call<FoundItem> call = apiService.createFoundItem(newFoundItem);
                    call.enqueue(new Callback<FoundItem>() {
                        @Override
                        public void onResponse(Call<FoundItem> call, Response<FoundItem> response) {
                            handleResponse(response.isSuccessful(), response.code(),
                                    response.isSuccessful() ? response.body().getTitle() : null,
                                    response, postButton);
                        }

                        @Override
                        public void onFailure(Call<FoundItem> call, Throwable t) {
                            handleFailure(t, postButton);
                        }
                    });
                } else {
                    // Reset button state
                    postButton.setEnabled(true);
                    postButton.setText("Post Item");
                    Toast.makeText(requireContext(), "Invalid item type selected", Toast.LENGTH_SHORT).show();
                }
            });

            MaterialButton cancelButton = view.findViewById(R.id.cancel_button);
            if (cancelButton != null) {
                cancelButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
            Toast.makeText(getContext(), "Error initializing post form: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void handleResponse(boolean isSuccessful, int responseCode, String itemTitle, Response<?> response, MaterialButton postButton) {
        // Reset button state
        postButton.setEnabled(true);
        postButton.setText("Post Item");

        Log.d(TAG, "API Response received. Code: " + responseCode);

        if (isSuccessful && itemTitle != null) {
            Log.d(TAG, "Item posted successfully: " + itemTitle);
            Toast.makeText(requireContext(), "Item posted successfully!", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            String errorMsg = "Failed to post item (Error " + responseCode + ")";
            try {
                if (response.errorBody() != null) {
                    String errorBody = response.errorBody().string();
                    Log.e(TAG, "Error body: " + errorBody);
                    errorMsg += ": " + errorBody;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading error body", e);
            }
            Log.e(TAG, errorMsg);
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    private void handleFailure(Throwable t, MaterialButton postButton) {
        // Reset button state
        postButton.setEnabled(true);
        postButton.setText("Post Item");

        Log.e(TAG, "Network error when posting item", t);
        String errorMsg = "Network error: " + t.getMessage();
        if (t instanceof java.net.ConnectException) {
            errorMsg = "Cannot connect to server. Please check your network connection and server status.";
        } else if (t instanceof java.net.SocketTimeoutException) {
            errorMsg = "Request timed out. Please try again.";
        }
        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
    }

    private void showDatePicker(TextInputEditText dateInput) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Use the same format for display but store in ISO format internally
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    dateInput.setText(dateFormat.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}