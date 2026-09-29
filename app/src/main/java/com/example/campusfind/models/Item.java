package com.example.campusfind.models;

public class Item {
    private int itemId;
    private String itemName;
    private String description;
    private String location;
    private String date;
    private String status; // "Lost" or "Found"
    private int userId;    // who reported this item

    public Item(int itemId, String itemName, String description, String location,
                 String date, String status, int userId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.location = location;
        this.date = date;
        this.status = status;
        this.userId = userId;
    }

    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public int getUserId() { return userId; }
}
