package com.itstep.myapplication.Model;

import com.google.gson.annotations.SerializedName;

public class ReturnedItem {
    private Long id;
    private String title;
    private String description;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("returnedDate")
    private String returnedDate;
    @SerializedName("createdDate")
    private String createdDate;
    @SerializedName("updatedDate")
    private String updatedDate;
    private String location;
    private String category;

    // Original owner (who lost the item)
    private User owner;
    @SerializedName("ownerName")
    private String ownerName;
    @SerializedName("ownerEmail")
    private String ownerEmail;

    // Person who found and returned the item
    private User finder;
    @SerializedName("finderName")
    private String finderName;
    @SerializedName("finderEmail")
    private String finderEmail;

    // References to original items
    @SerializedName("originalLostItemId")
    private Long originalLostItemId;
    @SerializedName("originalFoundItemId")
    private Long originalFoundItemId;

    // Default constructor for Gson
    public ReturnedItem() {}

    // Constructor
    public ReturnedItem(Long id, String title, String description, String imageUrl, String returnedDate,
                       String createdDate, String updatedDate, String location, String category,
                       Long originalLostItemId, Long originalFoundItemId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.returnedDate = returnedDate;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.location = location;
        this.category = category;
        this.originalLostItemId = originalLostItemId;
        this.originalFoundItemId = originalFoundItemId;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getReturnedDate() { return returnedDate; }
    public void setReturnedDate(String returnedDate) { this.returnedDate = returnedDate; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public User getFinder() { return finder; }
    public void setFinder(User finder) { this.finder = finder; }

    public String getFinderName() { return finderName; }
    public void setFinderName(String finderName) { this.finderName = finderName; }

    public String getFinderEmail() { return finderEmail; }
    public void setFinderEmail(String finderEmail) { this.finderEmail = finderEmail; }

    public Long getOriginalLostItemId() { return originalLostItemId; }
    public void setOriginalLostItemId(Long originalLostItemId) { this.originalLostItemId = originalLostItemId; }

    public Long getOriginalFoundItemId() { return originalFoundItemId; }
    public void setOriginalFoundItemId(Long originalFoundItemId) { this.originalFoundItemId = originalFoundItemId; }
}
