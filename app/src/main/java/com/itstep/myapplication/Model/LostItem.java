package com.itstep.myapplication.Model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class LostItem {
    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("lostDate")
    private String lostDate;

    @SerializedName("location")
    private String location;

    @SerializedName("category")
    private String category;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("createdDate")
    private Date createdDate;

    @SerializedName("updatedDate")
    private Date updatedDate;

    // Default constructor
    public LostItem() {}

    // Constructor for creating new lost item
    public LostItem(String title, String description, String lostDate, String location, String category, Long userId) {
        this.title = title;
        this.description = description;
        this.lostDate = lostDate;
        this.location = location;
        this.category = category;
        this.userId = userId;
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

    public String getLostDate() { return lostDate; }
    public void setLostDate(String lostDate) { this.lostDate = lostDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
}
