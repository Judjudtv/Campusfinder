package com.example.campusfind.models;

public class Claim {
    private int claimId;
    private int itemId;
    private int userId;
    private String claimStatus; // "Pending", "Approved", "Rejected"

    public Claim(int claimId, int itemId, int userId, String claimStatus) {
        this.claimId = claimId;
        this.itemId = itemId;
        this.userId = userId;
        this.claimStatus = claimStatus;
    }

    public int getClaimId() { return claimId; }
    public int getItemId() { return itemId; }
    public int getUserId() { return userId; }
    public String getClaimStatus() { return claimStatus; }
}
